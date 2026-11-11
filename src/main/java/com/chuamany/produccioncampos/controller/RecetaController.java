package com.chuamany.produccioncampos.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.chuamany.produccioncampos.dto.EscaladoResponse;
import com.chuamany.produccioncampos.dto.RecetaDTO;
import com.chuamany.produccioncampos.dto.RecetaResponse;
import com.chuamany.produccioncampos.service.RecetaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Recetas")
@RestController
@RequestMapping("api/recetas")
public class RecetaController {

	private final RecetaService service;

	public RecetaController(RecetaService service) {
		this.service = service;
	}

	@Operation(summary = "Lista recetas con busqueda y paginacion")
	@GetMapping
	public ResponseEntity<Page<RecetaResponse>> listar(
			@RequestParam(required = false) String texto,
			@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
		return ResponseEntity.ok(service.buscar(texto, pageable));
	}

	@Operation(summary = "Obtiene una receta con sus insumos")
	@GetMapping("/{id}")
	public ResponseEntity<RecetaResponse> obtener(@PathVariable Integer id) {
		return ResponseEntity.ok(service.obtener(id));
	}

	@Operation(summary = "Calcula los insumos para producir una cantidad")
	@GetMapping("/{id}/escalar")
	public ResponseEntity<EscaladoResponse> escalar(@PathVariable Integer id, @RequestParam Integer cantidad) {
		return ResponseEntity.ok(service.escalar(id, cantidad));
	}

	@Operation(summary = "Crea una receta con sus detalles en una transaccion")
	@PostMapping
	public ResponseEntity<RecetaResponse> crear(@Valid @RequestBody RecetaDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
	}

	@Operation(summary = "Actualiza una receta y sus detalles")
	@PutMapping("/{id}")
	public ResponseEntity<RecetaResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody RecetaDTO dto) {
		return ResponseEntity.ok(service.actualizar(id, dto));
	}

	@Operation(summary = "Elimina una receta (solo ADMIN)")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
