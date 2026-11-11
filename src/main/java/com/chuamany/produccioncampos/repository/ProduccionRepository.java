package com.chuamany.produccioncampos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chuamany.produccioncampos.dto.ConsumoTotalResponse;
import com.chuamany.produccioncampos.model.Produccion;

public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {

	boolean existsByComanda_Id(Integer comandaId);

	// JPQL con JOIN, SUM y GROUP BY: insumo consumido en un periodo, base para proyectar compras
	@Query("SELECT new com.chuamany.produccioncampos.dto.ConsumoTotalResponse(i.nombre, i.unidad, SUM(c.cantidad)) "
			+ "FROM ProduccionConsumo c JOIN c.insumo i "
			+ "WHERE c.produccion.fechaHora >= :desde AND c.produccion.fechaHora < :hasta "
			+ "GROUP BY i.id, i.nombre, i.unidad ORDER BY SUM(c.cantidad) DESC")
	List<ConsumoTotalResponse> consumoPorInsumo(@Param("desde") LocalDateTime desde,
			@Param("hasta") LocalDateTime hasta);
}
