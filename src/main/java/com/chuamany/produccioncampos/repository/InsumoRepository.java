package com.chuamany.produccioncampos.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chuamany.produccioncampos.model.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

	// JPQL con parametro nombrado y paginacion
	@Query("SELECT i FROM Insumo i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
	Page<Insumo> buscar(@Param("texto") String texto, Pageable pageable);

	// Usa la Named Query Insumo.buscarStockBajo declarada en la entidad
	List<Insumo> buscarStockBajo(@Param("activo") Boolean activo);

	// Descuenta solo si hay stock suficiente; devuelve 0 filas si no alcanza
	@Modifying(flushAutomatically = true)
	@Query("UPDATE Insumo i SET i.stock = i.stock - :cantidad WHERE i.id = :id AND i.stock >= :cantidad")
	int descontarStock(@Param("id") Integer id, @Param("cantidad") Double cantidad);

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
