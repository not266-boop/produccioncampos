package com.chuamany.produccioncampos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// Datos de entrada para crear o actualizar un insumo
public class InsumoDTO {

	@Schema(example = "Harina")
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar 100 caracteres")
	private String nombre;

	@Schema(example = "KG")
	@NotNull(message = "La unidad es obligatoria")
	@Pattern(regexp = "KG|L|UND", message = "La unidad debe ser KG, L o UND")
	private String unidad;

	@Schema(example = "100")
	@NotNull(message = "El stock es obligatorio")
	@PositiveOrZero(message = "El stock no puede ser negativo")
	private Double stock;

	@Schema(example = "20")
	@NotNull(message = "El stock minimo es obligatorio")
	@PositiveOrZero(message = "El stock minimo no puede ser negativo")
	private Double stockMinimo;

	@Schema(example = "3.5")
	@NotNull(message = "El costo es obligatorio")
	@Positive(message = "El costo debe ser mayor a 0")
	private Double costo;

	@Schema(example = "true")
	@NotNull(message = "El campo activo es obligatorio")
	private Boolean activo;

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public String getUnidad() { return unidad; }
	public void setUnidad(String unidad) { this.unidad = unidad; }

	public Double getStock() { return stock; }
	public void setStock(Double stock) { this.stock = stock; }

	public Double getStockMinimo() { return stockMinimo; }
	public void setStockMinimo(Double stockMinimo) { this.stockMinimo = stockMinimo; }

	public Double getCosto() { return costo; }
	public void setCosto(Double costo) { this.costo = costo; }

	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }
}
