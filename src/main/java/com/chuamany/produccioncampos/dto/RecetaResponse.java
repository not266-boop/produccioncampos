package com.chuamany.produccioncampos.dto;

import java.util.List;

import com.chuamany.produccioncampos.model.Receta;

// Datos que la API devuelve de una receta
public record RecetaResponse(Integer id, String nombre, Integer rendimiento, Boolean activa,
		List<Detalle> detalles) {

	public record Detalle(Integer insumoId, String insumo, String unidad, Double cantidad) {
	}

	public static RecetaResponse de(Receta r) {
		List<Detalle> detalles = r.getDetalles().stream()
				.map(d -> new Detalle(d.getInsumo().getId(), d.getInsumo().getNombre(),
						d.getInsumo().getUnidad(), d.getCantidad()))
				.toList();
		return new RecetaResponse(r.getId(), r.getNombre(), r.getRendimiento(), r.getActiva(), detalles);
	}
}
