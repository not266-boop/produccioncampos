package com.chuamany.produccioncampos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

// Materia prima usada en las recetas
@Entity
@Table(name = "insumos")
@NamedQuery(
		name = "Insumo.buscarStockBajo",
		query = "SELECT i FROM Insumo i WHERE i.stock <= i.stockMinimo AND i.activo = :activo ORDER BY i.nombre")
public class Insumo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String nombre;

	// KG, L o UND
	@Column(nullable = false)
	private String unidad;

	@Column(nullable = false)
	private Double stock;

	@Column(name = "stock_minimo", nullable = false)
	private Double stockMinimo;

	@Column(nullable = false)
	private Double costo;

	@Column(nullable = false)
	private Boolean activo;

	public Integer getId() { return id; }

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
