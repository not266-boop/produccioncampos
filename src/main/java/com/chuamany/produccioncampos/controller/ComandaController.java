package com.chuamany.produccioncampos.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chuamany.produccioncampos.dto.ComandaDTO;
import com.chuamany.produccioncampos.dto.ComandaResponse;
import com.chuamany.produccioncampos.dto.RequerimientosResponse;
import com.chuamany.produccioncampos.model.EstadoComanda;
import com.chuamany.produccioncampos.model.OrigenComanda;
import com.chuamany.produccioncampos.service.ComandaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Comandas")
@RestController
@RequestMapping("api/comandas")
public class ComandaController {

	private final ComandaService service;

	public ComandaController(ComandaService service) {
		this.service = service;
	}

	@Operation(summary = "Lista comandas con filtros opcionales y paginacion")
	@GetMapping
	public ResponseEntity<Page<ComandaResponse>> listar(
			@RequestParam(required = false) EstadoComanda estado,
			@RequestParam(required = false) OrigenComanda origen,
			@PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(service.buscar(estado, origen, pageable));
	}

	@Operation(summary = "Obtiene una comanda")
	@GetMapping("/{id}")
	public ResponseEntity<ComandaResponse> obtener(@PathVariable Integer id) {
		return ResponseEntity.ok(service.obtener(id));
	}

	@Operation(summary = "Calcula los insumos que necesita la comanda y si alcanza el stock")
	@GetMapping("/{id}/requerimientos")
	public ResponseEntity<RequerimientosResponse> requerimientos(@PathVariable Integer id) {
		return ResponseEntity.ok(service.requerimientos(id));
	}

	@Operation(summary = "Registra una comanda con sus productos")
	@PostMapping
	public ResponseEntity<ComandaResponse> crear(@Valid @RequestBody ComandaDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
	}

	@Operation(summary = "Actualiza una comanda pendiente")
	@PutMapping("/{id}")
	public ResponseEntity<ComandaResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody ComandaDTO dto) {
		return ResponseEntity.ok(service.actualizar(id, dto));
	}

	@Operation(summary = "Elimina una comanda pendiente (solo ADMIN)")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
