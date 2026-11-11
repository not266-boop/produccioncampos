package com.chuamany.produccioncampos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chuamany.produccioncampos.model.RecetaDetalle;

public interface RecetaDetalleRepository extends JpaRepository<RecetaDetalle, Integer> {

	boolean existsByInsumo_Id(Integer insumoId);
}
