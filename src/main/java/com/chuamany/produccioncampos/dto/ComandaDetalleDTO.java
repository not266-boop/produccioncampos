package com.chuamany.produccioncampos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ComandaDetalleDTO {

	@NotNull(message = "La receta es obligatoria")
	private Integer recetaId;

	@NotNull(message = "La cantidad es obligatoria")
	@Positive(message = "La cantidad debe ser mayor a 0")
	private Integer cantidad;

	public Integer getRecetaId() { return recetaId; }
	public void setRecetaId(Integer recetaId) { this.recetaId = recetaId; }

	public Integer getCantidad() { return cantidad; }
	public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
