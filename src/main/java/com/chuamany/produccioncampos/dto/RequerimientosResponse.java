package com.chuamany.produccioncampos.dto;

import java.util.List;

// Insumos totales que necesita una comanda y si alcanza el stock
public record RequerimientosResponse(Integer comandaId, boolean stockSuficiente,
		List<EscaladoResponse.Requerimiento> insumos) {
}
