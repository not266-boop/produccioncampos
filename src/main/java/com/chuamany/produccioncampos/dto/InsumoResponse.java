package com.chuamany.produccioncampos.dto;

import com.chuamany.produccioncampos.model.Insumo;

// Datos que la API devuelve de un insumo
public record InsumoResponse(Integer id, String nombre, String unidad, Double stock,
		Double stockMinimo, Double costo, Boolean activo) {

	public static InsumoResponse de(Insumo i) {
		return new InsumoResponse(i.getId(), i.getNombre(), i.getUnidad(), i.getStock(),
				i.getStockMinimo(), i.getCosto(), i.getActivo());
	}
}
