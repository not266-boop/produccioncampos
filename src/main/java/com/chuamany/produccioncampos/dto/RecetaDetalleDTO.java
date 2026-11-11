package com.chuamany.produccioncampos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Un insumo y su cantidad dentro de la receta
public class RecetaDetalleDTO {

	@NotNull(message = "El insumo es obligatorio")
	private Integer insumoId;

	@NotNull(message = "La cantidad es obligatoria")
	@Positive(message = "La cantidad debe ser mayor a 0")
	private Double cantidad;

	public Integer getInsumoId() { return insumoId; }
	public void setInsumoId(Integer insumoId) { this.insumoId = insumoId; }

	public Double getCantidad() { return cantidad; }
	public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
}
