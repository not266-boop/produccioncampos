package com.chuamany.produccioncampos.dto;

// Total consumido de un insumo en un periodo (resultado de una consulta JPQL)
public record ConsumoTotalResponse(String insumo, String unidad, Double total) {
}
