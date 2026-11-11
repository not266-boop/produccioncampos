package com.chuamany.produccioncampos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chuamany.produccioncampos.model.Usuario;

// findByUsername: lo usa Spring Security para autenticar
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	Optional<Usuario> findByUsername(String username);

	// evita registrar un username repetido
	boolean existsByUsername(String username);

}
