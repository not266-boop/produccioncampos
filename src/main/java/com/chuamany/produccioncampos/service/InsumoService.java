package com.chuamany.produccioncampos.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.chuamany.produccioncampos.dto.InsumoDTO;
import com.chuamany.produccioncampos.dto.InsumoResponse;
import com.chuamany.produccioncampos.exception.ConflictException;
import com.chuamany.produccioncampos.exception.ResourceNotFoundException;
import com.chuamany.produccioncampos.model.Insumo;
import com.chuamany.produccioncampos.repository.InsumoRepository;
import com.chuamany.produccioncampos.repository.RecetaDetalleRepository;

@Service
public class InsumoService {

	private static final Set<String> ORDEN_PERMITIDO = Set.of("nombre", "stock", "costo");

	private final InsumoRepository repo;
	private final RecetaDetalleRepository detalleRepo;

	public InsumoService(InsumoRepository repo, RecetaDetalleRepository detalleRepo) {
		this.repo = repo;
		this.detalleRepo = detalleRepo;
	}

	public Page<InsumoResponse> buscar(String texto, Pageable pageable) {
		String filtro = texto == null ? "" : texto.trim();
		return repo.buscar(filtro, seguro(pageable)).map(InsumoResponse::de);
	}

	public InsumoResponse obtener(Integer id) {
		return InsumoResponse.de(buscarEntidad(id));
	}

	public List<InsumoResponse> stockBajo(Boolean activo) {
		return repo.buscarStockBajo(activo).stream().map(InsumoResponse::de).toList();
	}

	public InsumoResponse crear(InsumoDTO dto) {
		if (repo.existsByNombreIgnoreCase(dto.getNombre())) {
			throw new ConflictException("Ya existe un insumo con el nombre '" + dto.getNombre() + "'");
		}
		Insumo insumo = new Insumo();
		copiar(dto, insumo);
		return InsumoResponse.de(repo.save(insumo));
	}

	public InsumoResponse actualizar(Integer id, InsumoDTO dto) {
		Insumo insumo = buscarEntidad(id);
		if (repo.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id)) {
			throw new ConflictException("Ya existe un insumo con el nombre '" + dto.getNombre() + "'");
		}
		copiar(dto, insumo);
		return InsumoResponse.de(repo.save(insumo));
	}

	public void eliminar(Integer id) {
		Insumo insumo = buscarEntidad(id);
		if (detalleRepo.existsByInsumo_Id(id)) {
			throw new ConflictException("No se puede eliminar: el insumo se usa en una receta");
		}
		repo.delete(insumo);
	}

	private Insumo buscarEntidad(Integer id) {
		return repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id " + id));
	}

	private void copiar(InsumoDTO dto, Insumo insumo) {
		insumo.setNombre(dto.getNombre().trim());
		insumo.setUnidad(dto.getUnidad());
		insumo.setStock(dto.getStock());
		insumo.setStockMinimo(dto.getStockMinimo());
		insumo.setCosto(dto.getCosto());
		insumo.setActivo(dto.getActivo());
	}

	// Lista blanca de orden y desempate por id para una paginacion estable
	private Pageable seguro(Pageable p) {
		p.getSort().forEach(orden -> {
			if (!ORDEN_PERMITIDO.contains(orden.getProperty())) {
				throw new IllegalArgumentException("Orden no permitido: " + orden.getProperty());
			}
		});
		return PageRequest.of(p.getPageNumber(), p.getPageSize(), p.getSort().and(Sort.by("id")));
	}
}
