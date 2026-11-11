package com.chuamany.produccioncampos.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chuamany.produccioncampos.dto.ConsumoTotalResponse;
import com.chuamany.produccioncampos.dto.ProduccionDTO;
import com.chuamany.produccioncampos.dto.ProduccionResponse;
import com.chuamany.produccioncampos.service.ProduccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Producciones")
@RestController
@RequestMapping("api/producciones")
public class ProduccionController {

	private final ProduccionService service;

	public ProduccionController(ProduccionService service) {
		this.service = service;
	}

	@Operation(summary = "Lista producciones, la mas reciente primero")
	@GetMapping
	public ResponseEntity<Page<ProduccionResponse>> listar(@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(service.buscar(pageable));
	}

	@Operation(summary = "Obtiene una produccion con sus consumos")
	@GetMapping("/{id}")
	public ResponseEntity<ProduccionResponse> obtener(@PathVariable Integer id) {
		return ResponseEntity.ok(service.obtener(id));
	}

	@Operation(summary = "Total de insumos consumidos entre dos fechas (yyyy-MM-dd)")
	@GetMapping("/consumo")
	public ResponseEntity<List<ConsumoTotalResponse>> consumo(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return ResponseEntity.ok(service.consumo(desde, hasta));
	}

	@Operation(summary = "Produce una comanda: descuenta insumos en una transaccion")
	@PostMapping
	public ResponseEntity<ProduccionResponse> producir(@Valid @RequestBody ProduccionDTO dto,
			@Parameter(hidden = true) Authentication authentication) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.producir(dto, authentication.getName()));
	}
}
