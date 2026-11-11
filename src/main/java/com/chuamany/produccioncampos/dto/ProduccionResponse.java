package com.chuamany.produccioncampos.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.chuamany.produccioncampos.model.Produccion;

public record ProduccionResponse(Integer id, Integer comandaId, LocalDateTime fechaHora, String usuario,
		String observacion, List<Consumo> consumos) {

	public record Consumo(Integer insumoId, String insumo, String unidad, Double cantidad) {
	}

	public static ProduccionResponse de(Produccion p) {
		List<Consumo> consumos = p.getConsumos().stream()
				.map(c -> new Consumo(c.getInsumo().getId(), c.getInsumo().getNombre(),
						c.getInsumo().getUnidad(), c.getCantidad()))
				.toList();
		return new ProduccionResponse(p.getId(), p.getComanda().getId(), p.getFechaHora(), p.getUsuario(),
				p.getObservacion(), consumos);
	}
}
