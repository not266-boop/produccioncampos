package com.chuamany.produccioncampos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chuamany.produccioncampos.model.Comanda;
import com.chuamany.produccioncampos.model.EstadoComanda;
import com.chuamany.produccioncampos.model.OrigenComanda;

public interface ComandaRepository extends JpaRepository<Comanda, Integer> {

	// Filtros opcionales: si un parametro llega null, no filtra por ese campo
	@Query("SELECT c FROM Comanda c WHERE (:estado IS NULL OR c.estado = :estado) AND (:origen IS NULL OR c.origen = :origen)")
	Page<Comanda> buscar(@Param("estado") EstadoComanda estado, @Param("origen") OrigenComanda origen,
			Pageable pageable);
}
