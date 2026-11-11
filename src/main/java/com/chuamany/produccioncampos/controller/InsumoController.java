package com.chuamany.produccioncampos.controller;

import java.util.List;

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

import com.chuamany.produccioncampos.dto.InsumoDTO;
import com.chuamany.produccioncampos.dto.InsumoResponse;
import com.chuamany.produccioncampos.service.InsumoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Insumos")
@RestController
@RequestMapping("api/insumos")
public class InsumoController {

	private final InsumoService service;

	public InsumoController(InsumoService service) {
		this.service = service;
	}

	@Operation(summary = "Lista insumos con busqueda y paginacion")
	@GetMapping
	public ResponseEntity<Page<InsumoResponse>> listar(
			@RequestParam(required = false) String texto,
			@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
		return ResponseEntity.ok(service.buscar(texto, pageable));
	}

	@Operation(summary = "Lista insumos con stock menor o igual al minimo")
	@GetMapping("/stock-bajo")
	public ResponseEntity<List<InsumoResponse>> stockBajo(
			@RequestParam(defaultValue = "true") Boolean activo) {
		return ResponseEntity.ok(service.stockBajo(activo));
	}

	@Operation(summary = "Obtiene un insumo")
	@GetMapping("/{id}")
	public ResponseEntity<InsumoResponse> obtener(@PathVariable Integer id) {
		return ResponseEntity.ok(service.obtener(id));
	}

	@Operation(summary = "Crea un insumo")
	@PostMapping
	public ResponseEntity<InsumoResponse> crear(@Valid @RequestBody InsumoDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
	}

	@Operation(summary = "Actualiza un insumo")
	@PutMapping("/{id}")
	public ResponseEntity<InsumoResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody InsumoDTO dto) {
		return ResponseEntity.ok(service.actualizar(id, dto));
	}

	@Operation(summary = "Elimina un insumo (solo ADMIN)")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
