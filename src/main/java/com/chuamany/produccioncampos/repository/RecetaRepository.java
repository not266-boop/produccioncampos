package com.chuamany.produccioncampos.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chuamany.produccioncampos.model.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Integer> {

	@Query("SELECT r FROM Receta r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
	Page<Receta> buscar(@Param("texto") String texto, Pageable pageable);

	// JOIN FETCH trae la receta con sus detalles e insumos en una sola consulta
	@Query("SELECT DISTINCT r FROM Receta r LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.insumo WHERE r.id = :id")
	Optional<Receta> buscarCompleta(@Param("id") Integer id);

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
