package com.chuamany.produccioncampos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// Registro de una comanda producida: una comanda tiene como maximo una produccion (OneToOne)
@Entity
@Table(name = "producciones")
public class Produccion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "comanda_id", nullable = false, unique = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Comanda comanda;

	@Column(name = "fecha_hora", nullable = false)
	private LocalDateTime fechaHora;

	// Usuario autenticado que registro la produccion
	@Column(nullable = false)
	private String usuario;

	@Column(length = 255)
	private String observacion;

	@OneToMany(mappedBy = "produccion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProduccionConsumo> consumos = new ArrayList<>();

	public void agregarConsumo(ProduccionConsumo consumo) {
		consumos.add(consumo);
		consumo.setProduccion(this);
	}

	public Integer getId() { return id; }

	public Comanda getComanda() { return comanda; }
	public void setComanda(Comanda comanda) { this.comanda = comanda; }

	public LocalDateTime getFechaHora() { return fechaHora; }
	public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

	public String getUsuario() { return usuario; }
	public void setUsuario(String usuario) { this.usuario = usuario; }

	public String getObservacion() { return observacion; }
	public void setObservacion(String observacion) { this.observacion = observacion; }

	public List<ProduccionConsumo> getConsumos() { return consumos; }
}
