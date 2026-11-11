package com.chuamany.produccioncampos.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chuamany.produccioncampos.dto.EscaladoResponse;
import com.chuamany.produccioncampos.dto.RecetaDTO;
import com.chuamany.produccioncampos.dto.RecetaDetalleDTO;
import com.chuamany.produccioncampos.dto.RecetaResponse;
import com.chuamany.produccioncampos.exception.ConflictException;
import com.chuamany.produccioncampos.exception.ResourceNotFoundException;
import com.chuamany.produccioncampos.model.Insumo;
import com.chuamany.produccioncampos.model.Receta;
import com.chuamany.produccioncampos.model.RecetaDetalle;
import com.chuamany.produccioncampos.repository.ComandaDetalleRepository;
import com.chuamany.produccioncampos.repository.InsumoRepository;
import com.chuamany.produccioncampos.repository.RecetaRepository;

@Service
public class RecetaService {

	private static final Set<String> ORDEN_PERMITIDO = Set.of("nombre", "rendimiento");

	private final RecetaRepository repo;
	private final InsumoRepository insumoRepo;
	private final ComandaDetalleRepository comandaDetalleRepo;

	public RecetaService(RecetaRepository repo, InsumoRepository insumoRepo,
			ComandaDetalleRepository comandaDetalleRepo) {
		this.repo = repo;
		this.insumoRepo = insumoRepo;
		this.comandaDetalleRepo = comandaDetalleRepo;
	}

	// La transaccion mantiene la sesion abierta para leer los detalles
	@Transactional
	public Page<RecetaResponse> buscar(String texto, Pageable pageable) {
		String filtro = texto == null ? "" : texto.trim();
		return repo.buscar(filtro, seguro(pageable)).map(RecetaResponse::de);
	}

	@Transactional
	public RecetaResponse obtener(Integer id) {
		return RecetaResponse.de(buscarCompleta(id));
	}

	// Todo o nada: si falla un detalle, se revierte tambien la receta ya guardada
	@Transactional
	public RecetaResponse crear(RecetaDTO dto) {
		if (repo.existsByNombreIgnoreCase(dto.getNombre())) {
			throw new ConflictException("Ya existe una receta con el nombre '" + dto.getNombre() + "'");
		}
		Receta receta = new Receta();
		copiarDatos(dto, receta);
		receta = repo.saveAndFlush(receta);

		agregarDetalles(receta, dto.getDetalles());
		repo.flush();
		return RecetaResponse.de(receta);
	}

	@Transactional
	public RecetaResponse actualizar(Integer id, RecetaDTO dto) {
		Receta receta = buscarCompleta(id);
		if (repo.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id)) {
			throw new ConflictException("Ya existe una receta con el nombre '" + dto.getNombre() + "'");
		}
		copiarDatos(dto, receta);

		// orphanRemoval borra los detalles que salen de la lista
		receta.getDetalles().clear();
		agregarDetalles(receta, dto.getDetalles());
		repo.flush();
		return RecetaResponse.de(receta);
	}

	@Transactional
	public void eliminar(Integer id) {
		Receta receta = buscarCompleta(id);
		if (comandaDetalleRepo.existsByReceta_Id(id)) {
			throw new ConflictException("No se puede eliminar: la receta se usa en una comanda");
		}
		repo.delete(receta);
	}

	// Regla de tres: requerido = cantidad a producir x cantidad del insumo / rendimiento
	@Transactional
	public EscaladoResponse escalar(Integer id, Integer cantidad) {
		if (cantidad == null || cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad a producir debe ser mayor a 0");
		}
		Receta receta = buscarCompleta(id);
		double factor = cantidad.doubleValue() / receta.getRendimiento();

		List<EscaladoResponse.Requerimiento> insumos = receta.getDetalles().stream()
				.map(d -> {
					Insumo insumo = d.getInsumo();
					double requerido = d.getCantidad() * factor;
					return new EscaladoResponse.Requerimiento(insumo.getId(), insumo.getNombre(),
							insumo.getUnidad(), requerido, insumo.getStock(), insumo.getStock() >= requerido);
				})
				.toList();

		boolean suficiente = insumos.stream().allMatch(EscaladoResponse.Requerimiento::suficiente);
		return new EscaladoResponse(receta.getId(), receta.getNombre(), cantidad, factor, suficiente, insumos);
	}

	private Receta buscarCompleta(Integer id) {
		return repo.buscarCompleta(id)
				.orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id " + id));
	}

	private void copiarDatos(RecetaDTO dto, Receta receta) {
		receta.setNombre(dto.getNombre().trim());
		receta.setRendimiento(dto.getRendimiento());
		receta.setActiva(dto.getActiva());
	}

	private void agregarDetalles(Receta receta, List<RecetaDetalleDTO> detalles) {
		Set<Integer> usados = new HashSet<>();
		for (RecetaDetalleDTO item : detalles) {
			if (!usados.add(item.getInsumoId())) {
				throw new ConflictException("El insumo " + item.getInsumoId() + " esta repetido en la receta");
			}
			Insumo insumo = insumoRepo.findById(item.getInsumoId())
					.orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id " + item.getInsumoId()));

			RecetaDetalle detalle = new RecetaDetalle();
			detalle.setInsumo(insumo);
			detalle.setCantidad(item.getCantidad());
			receta.agregarDetalle(detalle);
		}
	}

	private Pageable seguro(Pageable p) {
		p.getSort().forEach(orden -> {
			if (!ORDEN_PERMITIDO.contains(orden.getProperty())) {
				throw new IllegalArgumentException("Orden no permitido: " + orden.getProperty());
			}
		});
		return PageRequest.of(p.getPageNumber(), p.getPageSize(), p.getSort().and(Sort.by("id")));
	}
}
