package com.chuamany.produccioncampos.dto;

import java.util.List;

// Insumos necesarios para producir una cantidad de una receta
public record EscaladoResponse(Integer recetaId, String receta, Integer cantidad, Double factor,
		boolean stockSuficiente, List<Requerimiento> insumos) {

	public record Requerimiento(Integer insumoId, String insumo, String unidad,
			Double requerido, Double disponible, boolean suficiente) {
	}
}
