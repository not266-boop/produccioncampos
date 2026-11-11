package com.chuamany.produccioncampos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProduccionDTO {

	@Schema(example = "1")
	@NotNull(message = "La comanda es obligatoria")
	private Integer comandaId;

	@Size(max = 255, message = "La observacion no puede superar 255 caracteres")
	private String observacion;

	public Integer getComandaId() { return comandaId; }
	public void setComandaId(Integer comandaId) { this.comandaId = comandaId; }

	public String getObservacion() { return observacion; }
	public void setObservacion(String observacion) { this.observacion = observacion; }
}
