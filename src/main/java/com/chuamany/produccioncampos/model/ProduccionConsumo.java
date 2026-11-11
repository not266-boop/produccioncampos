package com.chuamany.produccioncampos.model;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Cantidad de insumo descontada del stock en una produccion
@Entity
@Table(name = "produccion_consumos")
public class ProduccionConsumo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produccion_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Produccion produccion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "insumo_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Insumo insumo;

	@Column(nullable = false)
	private Double cantidad;

	public Integer getId() { return id; }

	public Produccion getProduccion() { return produccion; }
	public void setProduccion(Produccion produccion) { this.produccion = produccion; }

	public Insumo getInsumo() { return insumo; }
	public void setInsumo(Insumo insumo) { this.insumo = insumo; }

	public Double getCantidad() { return cantidad; }
	public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
}
