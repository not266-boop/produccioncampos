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

// Cantidad pedida de una receta dentro de una comanda
@Entity
@Table(name = "comanda_detalles")
public class ComandaDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "comanda_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Comanda comanda;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receta_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Receta receta;

	// Unidades de producto pedidas
	@Column(nullable = false)
	private Integer cantidad;

	public Integer getId() { return id; }

	public Comanda getComanda() { return comanda; }
	public void setComanda(Comanda comanda) { this.comanda = comanda; }

	public Receta getReceta() { return receta; }
	public void setReceta(Receta receta) { this.receta = receta; }

	public Integer getCantidad() { return cantidad; }
	public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
