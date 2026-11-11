package com.chuamany.produccioncampos.dto;

import java.time.LocalDate;
import java.util.List;

import com.chuamany.produccioncampos.model.OrigenComanda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Datos de entrada de una comanda con sus productos
public class ComandaDTO {

	@Schema(example = "TIENDA")
	@NotNull(message = "El origen es obligatorio")
	private OrigenComanda origen;

	@Schema(example = "2026-11-11")
	@NotNull(message = "La fecha es obligatoria")
	private LocalDate fecha;

	@Schema(example = "1")
	@NotNull(message = "El turno es obligatorio")
	@Min(value = 1, message = "El turno debe ser 1 o 2")
	@Max(value = 2, message = "El turno debe ser 1 o 2")
	private Integer turno;

	@Size(max = 255, message = "La observacion no puede superar 255 caracteres")
	private String observacion;

	@Valid
	@NotEmpty(message = "La comanda debe tener al menos un producto")
	private List<ComandaDetalleDTO> detalles;

	public OrigenComanda getOrigen() { return origen; }
	public void setOrigen(OrigenComanda origen) { this.origen = origen; }

	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }

	public Integer getTurno() { return turno; }
	public void setTurno(Integer turno) { this.turno = turno; }

	public String getObservacion() { return observacion; }
	public void setObservacion(String observacion) { this.observacion = observacion; }

	public List<ComandaDetalleDTO> getDetalles() { return detalles; }
	public void setDetalles(List<ComandaDetalleDTO> detalles) { this.detalles = detalles; }
}
