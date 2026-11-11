package com.chuamany.produccioncampos.dto;

import java.time.LocalDate;
import java.util.List;

import com.chuamany.produccioncampos.model.Comanda;
import com.chuamany.produccioncampos.model.EstadoComanda;
import com.chuamany.produccioncampos.model.OrigenComanda;

public record ComandaResponse(Integer id, OrigenComanda origen, LocalDate fecha, Integer turno,
		EstadoComanda estado, String observacion, List<Detalle> detalles) {

	public record Detalle(Integer recetaId, String receta, Integer cantidad) {
	}

	public static ComandaResponse de(Comanda c) {
		List<Detalle> detalles = c.getDetalles().stream()
				.map(d -> new Detalle(d.getReceta().getId(), d.getReceta().getNombre(), d.getCantidad()))
				.toList();
		return new ComandaResponse(c.getId(), c.getOrigen(), c.getFecha(), c.getTurno(), c.getEstado(),
				c.getObservacion(), detalles);
	}
}
