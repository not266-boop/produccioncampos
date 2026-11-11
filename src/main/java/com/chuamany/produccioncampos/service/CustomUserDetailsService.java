package com.chuamany.produccioncampos.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chuamany.produccioncampos.model.Usuario;
import com.chuamany.produccioncampos.repository.UsuarioRepository;

// Traduce Usuario a UserDetails para Spring Security
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UsuarioRepository repo;

	public CustomUserDetailsService(UsuarioRepository repo) {
		this.repo = repo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Usuario usuario = repo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

		return org.springframework.security.core.userdetails.User.builder()
				.username(usuario.getUsername())
				.password(usuario.getPassword())
				.authorities(List.of(new SimpleGrantedAuthority(usuario.getRol())))
				.build();
	}

}
