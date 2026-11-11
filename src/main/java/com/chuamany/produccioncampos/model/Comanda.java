package com.chuamany.produccioncampos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Pedido de produccion que antes llegaba en papel (OneToMany con sus detalles)
@Entity
@Table(name = "comandas")
public class Comanda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrigenComanda origen;

	@Column(nullable = false)
	private LocalDate fecha;

	// Turno 1 o 2
	@Column(nullable = false)
	private Integer turno;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoComanda estado;

	@Column(length = 255)
	private String observacion;

	@OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComandaDetalle> detalles = new ArrayList<>();

	public void agregarDetalle(ComandaDetalle detalle) {
		detalles.add(detalle);
		detalle.setComanda(this);
	}

	public Integer getId() { return id; }

	public OrigenComanda getOrigen() { return origen; }
	public void setOrigen(OrigenComanda origen) { this.origen = origen; }

	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }

	public Integer getTurno() { return turno; }
	public void setTurno(Integer turno) { this.turno = turno; }

	public EstadoComanda getEstado() { return estado; }
	public void setEstado(EstadoComanda estado) { this.estado = estado; }

	public String getObservacion() { return observacion; }
	public void setObservacion(String observacion) { this.observacion = observacion; }

	public List<ComandaDetalle> getDetalles() { return detalles; }
}
