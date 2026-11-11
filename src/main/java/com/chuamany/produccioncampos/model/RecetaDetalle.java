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

// Cantidad de un insumo dentro de una receta (ManyToOne hacia Receta e Insumo)
// SQLite no agrega claves foraneas con ALTER TABLE: la integridad se valida en los Services
@Entity
@Table(name = "receta_detalles")
public class RecetaDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receta_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Receta receta;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "insumo_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Insumo insumo;

	@Column(nullable = false)
	private Double cantidad;

	public Integer getId() { return id; }

	public Receta getReceta() { return receta; }
	public void setReceta(Receta receta) { this.receta = receta; }

	public Insumo getInsumo() { return insumo; }
	public void setInsumo(Insumo insumo) { this.insumo = insumo; }

	public Double getCantidad() { return cantidad; }
	public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
}
