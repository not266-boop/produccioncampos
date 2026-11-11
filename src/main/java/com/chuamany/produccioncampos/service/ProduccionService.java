package com.chuamany.produccioncampos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chuamany.produccioncampos.dto.ConsumoTotalResponse;
import com.chuamany.produccioncampos.dto.ProduccionDTO;
import com.chuamany.produccioncampos.dto.ProduccionResponse;
import com.chuamany.produccioncampos.exception.ConflictException;
import com.chuamany.produccioncampos.exception.ResourceNotFoundException;
import com.chuamany.produccioncampos.model.Comanda;
import com.chuamany.produccioncampos.model.EstadoComanda;
import com.chuamany.produccioncampos.model.Produccion;
import com.chuamany.produccioncampos.model.ProduccionConsumo;
import com.chuamany.produccioncampos.repository.InsumoRepository;
import com.chuamany.produccioncampos.repository.ProduccionRepository;

@Service
public class ProduccionService {

	private final ProduccionRepository repo;
	private final InsumoRepository insumoRepo;
	private final ComandaService comandaService;

	public ProduccionService(ProduccionRepository repo, InsumoRepository insumoRepo, ComandaService comandaService) {
		this.repo = repo;
		this.insumoRepo = insumoRepo;
		this.comandaService = comandaService;
	}

	@Transactional(readOnly = true)
	public Page<ProduccionResponse> buscar(Pageable pageable) {
		Pageable orden = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Sort.Direction.DESC, "fechaHora").and(Sort.by("id")));
		return repo.findAll(orden).map(ProduccionResponse::de);
	}

	@Transactional(readOnly = true)
	public ProduccionResponse obtener(Integer id) {
		Produccion produccion = repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produccion no encontrada con id " + id));
		return ProduccionResponse.de(produccion);
	}

	// Transaccion: descuenta cada insumo, registra la produccion y cierra la comanda.
	// Si un insumo no alcanza, la excepcion revierte tambien los descuentos ya hechos.
	@Transactional
	public ProduccionResponse producir(ProduccionDTO dto, String usuario) {
		Comanda comanda = comandaService.buscarEntidad(dto.getComandaId());
		if (comanda.getEstado() != EstadoComanda.PENDIENTE || repo.existsByComanda_Id(comanda.getId())) {
			throw new ConflictException("La comanda " + comanda.getId() + " ya fue producida");
		}

		Produccion produccion = new Produccion();
		produccion.setComanda(comanda);
		produccion.setFechaHora(LocalDateTime.now());
		produccion.setUsuario(usuario);
		produccion.setObservacion(dto.getObservacion());

		for (ComandaService.Requerido requerido : comandaService.requeridos(comanda)) {
			int filas = insumoRepo.descontarStock(requerido.insumo().getId(), requerido.cantidad());
			if (filas == 0) {
				throw new ConflictException("Stock insuficiente de " + requerido.insumo().getNombre()
						+ ": se necesitan " + requerido.cantidad() + " " + requerido.insumo().getUnidad()
						+ " y hay " + requerido.insumo().getStock());
			}
			ProduccionConsumo consumo = new ProduccionConsumo();
			consumo.setInsumo(requerido.insumo());
			consumo.setCantidad(requerido.cantidad());
			produccion.agregarConsumo(consumo);
		}

		comanda.setEstado(EstadoComanda.PRODUCIDA);
		repo.save(produccion);
		return ProduccionResponse.de(produccion);
	}

	@Transactional(readOnly = true)
	public List<ConsumoTotalResponse> consumo(LocalDate desde, LocalDate hasta) {
		if (desde.isAfter(hasta)) {
			throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
		}
		return repo.consumoPorInsumo(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
	}
}
