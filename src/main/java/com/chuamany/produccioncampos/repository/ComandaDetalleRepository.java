package com.chuamany.produccioncampos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chuamany.produccioncampos.model.ComandaDetalle;

public interface ComandaDetalleRepository extends JpaRepository<ComandaDetalle, Integer> {

	boolean existsByReceta_Id(Integer recetaId);
}
