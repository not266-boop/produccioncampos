package com.chuamany.produccioncampos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Datos de entrada de una receta con sus detalles
public class RecetaDTO {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar 100 caracteres")
	private String nombre;

	@NotNull(message = "El rendimiento es obligatorio")
	@Positive(message = "El rendimiento debe ser mayor a 0")
	private Integer rendimiento;

	@NotNull(message = "El campo activa es obligatorio")
	private Boolean activa;

	@Valid
	@NotEmpty(message = "La receta debe tener al menos un insumo")
	private List<RecetaDetalleDTO> detalles;

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public Integer getRendimiento() { return rendimiento; }
	public void setRendimiento(Integer rendimiento) { this.rendimiento = rendimiento; }

	public Boolean getActiva() { return activa; }
	public void setActiva(Boolean activa) { this.activa = activa; }

	public List<RecetaDetalleDTO> getDetalles() { return detalles; }
	public void setDetalles(List<RecetaDetalleDTO> detalles) { this.detalles = detalles; }
}
