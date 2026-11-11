package com.chuamany.produccioncampos.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chuamany.produccioncampos.dto.ComandaDTO;
import com.chuamany.produccioncampos.dto.ComandaDetalleDTO;
import com.chuamany.produccioncampos.dto.ComandaResponse;
import com.chuamany.produccioncampos.dto.EscaladoResponse;
import com.chuamany.produccioncampos.dto.RequerimientosResponse;
import com.chuamany.produccioncampos.exception.ConflictException;
import com.chuamany.produccioncampos.exception.ResourceNotFoundException;
import com.chuamany.produccioncampos.model.Comanda;
import com.chuamany.produccioncampos.model.ComandaDetalle;
import com.chuamany.produccioncampos.model.EstadoComanda;
import com.chuamany.produccioncampos.model.Insumo;
import com.chuamany.produccioncampos.model.OrigenComanda;
import com.chuamany.produccioncampos.model.Receta;
import com.chuamany.produccioncampos.repository.ComandaRepository;
import com.chuamany.produccioncampos.repository.RecetaRepository;

@Service
public class ComandaService {

	private static final Set<String> ORDEN_PERMITIDO = Set.of("fecha", "turno");

	// Cantidad total de un insumo que necesita la comanda
	public record Requerido(Insumo insumo, double cantidad) {
	}

	private final ComandaRepository repo;
	private final RecetaRepository recetaRepo;

	public ComandaService(ComandaRepository repo, RecetaRepository recetaRepo) {
		this.repo = repo;
		this.recetaRepo = recetaRepo;
	}

	@Transactional(readOnly = true)
	public Page<ComandaResponse> buscar(EstadoComanda estado, OrigenComanda origen, Pageable pageable) {
		return repo.buscar(estado, origen, seguro(pageable)).map(ComandaResponse::de);
	}

	@Transactional(readOnly = true)
	public ComandaResponse obtener(Integer id) {
		return ComandaResponse.de(buscarEntidad(id));
	}

	// La comanda y sus detalles se guardan juntos o no se guarda nada
	@Transactional
	public ComandaResponse crear(ComandaDTO dto) {
		Comanda comanda = new Comanda();
		copiarDatos(dto, comanda);
		comanda.setEstado(EstadoComanda.PENDIENTE);
		comanda = repo.saveAndFlush(comanda);

		agregarDetalles(comanda, dto.getDetalles());
		repo.flush();
		return ComandaResponse.de(comanda);
	}

	@Transactional
	public ComandaResponse actualizar(Integer id, ComandaDTO dto) {
		Comanda comanda = buscarPendiente(id);
		copiarDatos(dto, comanda);
		comanda.getDetalles().clear();
		agregarDetalles(comanda, dto.getDetalles());
		repo.flush();
		return ComandaResponse.de(comanda);
	}

	@Transactional
	public void eliminar(Integer id) {
		repo.delete(buscarPendiente(id));
	}

	@Transactional(readOnly = true)
	public RequerimientosResponse requerimientos(Integer id) {
		Comanda comanda = buscarEntidad(id);
		List<EscaladoResponse.Requerimiento> insumos = requeridos(comanda).stream()
				.map(r -> new EscaladoResponse.Requerimiento(r.insumo().getId(), r.insumo().getNombre(),
						r.insumo().getUnidad(), r.cantidad(), r.insumo().getStock(),
						r.insumo().getStock() >= r.cantidad()))
				.toList();
		boolean suficiente = insumos.stream().allMatch(EscaladoResponse.Requerimiento::suficiente);
		return new RequerimientosResponse(comanda.getId(), suficiente, insumos);
	}

	// Suma por insumo: cantidad pedida x cantidad en la receta / rendimiento de la receta
	public List<Requerido> requeridos(Comanda comanda) {
		Map<Integer, Insumo> insumos = new LinkedHashMap<>();
		Map<Integer, Double> totales = new LinkedHashMap<>();
		for (ComandaDetalle detalle : comanda.getDetalles()) {
			Receta receta = detalle.getReceta();
			double factor = detalle.getCantidad().doubleValue() / receta.getRendimiento();
			receta.getDetalles().forEach(rd -> {
				Insumo insumo = rd.getInsumo();
				insumos.putIfAbsent(insumo.getId(), insumo);
				totales.merge(insumo.getId(), rd.getCantidad() * factor, Double::sum);
			});
		}
		List<Requerido> resultado = new ArrayList<>();
		totales.forEach((insumoId, total) -> resultado.add(new Requerido(insumos.get(insumoId), redondear(total))));
		return resultado;
	}

	public Comanda buscarEntidad(Integer id) {
		return repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comanda no encontrada con id " + id));
	}

	private Comanda buscarPendiente(Integer id) {
		Comanda comanda = buscarEntidad(id);
		if (comanda.getEstado() != EstadoComanda.PENDIENTE) {
			throw new ConflictException("La comanda " + id + " ya fue producida y no se puede modificar");
		}
		return comanda;
	}

	private void copiarDatos(ComandaDTO dto, Comanda comanda) {
		comanda.setOrigen(dto.getOrigen());
		comanda.setFecha(dto.getFecha());
		comanda.setTurno(dto.getTurno());
		comanda.setObservacion(dto.getObservacion());
	}

	private void agregarDetalles(Comanda comanda, List<ComandaDetalleDTO> detalles) {
		Set<Integer> usadas = new HashSet<>();
		for (ComandaDetalleDTO item : detalles) {
			if (!usadas.add(item.getRecetaId())) {
				throw new ConflictException("La receta " + item.getRecetaId() + " esta repetida en la comanda");
			}
			Receta receta = recetaRepo.findById(item.getRecetaId())
					.orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id " + item.getRecetaId()));
			if (!Boolean.TRUE.equals(receta.getActiva())) {
				throw new ConflictException("La receta '" + receta.getNombre() + "' esta inactiva");
			}
			ComandaDetalle detalle = new ComandaDetalle();
			detalle.setReceta(receta);
			detalle.setCantidad(item.getCantidad());
			comanda.agregarDetalle(detalle);
		}
	}

	private static double redondear(double valor) {
		return Math.round(valor * 1000) / 1000.0;
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
