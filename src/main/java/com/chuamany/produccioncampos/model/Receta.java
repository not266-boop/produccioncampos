package com.chuamany.produccioncampos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Una receta tiene muchos detalles (OneToMany)
@Entity
@Table(name = "recetas")
public class Receta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String nombre;

	// Unidades de producto que rinde la receta
	@Column(nullable = false)
	private Integer rendimiento;

	@Column(nullable = false)
	private Boolean activa;

	@OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecetaDetalle> detalles = new ArrayList<>();

	// Mantiene sincronizados los dos lados de la relacion
	public void agregarDetalle(RecetaDetalle detalle) {
		detalles.add(detalle);
		detalle.setReceta(this);
	}

	public Integer getId() { return id; }

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public Integer getRendimiento() { return rendimiento; }
	public void setRendimiento(Integer rendimiento) { this.rendimiento = rendimiento; }

	public Boolean getActiva() { return activa; }
	public void setActiva(Boolean activa) { this.activa = activa; }

	public List<RecetaDetalle> getDetalles() { return detalles; }
}
