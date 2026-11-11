package com.chuamany.produccioncampos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuamany.produccioncampos.dto.AuthResponse;
import com.chuamany.produccioncampos.dto.LoginRequest;
import com.chuamany.produccioncampos.dto.RegisterRequest;
import com.chuamany.produccioncampos.exception.ConflictException;
import com.chuamany.produccioncampos.exception.TooManyAttemptsException;
import com.chuamany.produccioncampos.model.Usuario;
import com.chuamany.produccioncampos.repository.UsuarioRepository;
import com.chuamany.produccioncampos.security.JwtService;
import com.chuamany.produccioncampos.security.LoginAttemptService;

import jakarta.validation.Valid;

// Único Controller público (/api/auth/**)
@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final LoginAttemptService loginAttempts;

	public AuthController(
			AuthenticationManager authenticationManager,
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			LoginAttemptService loginAttempts) {
		this.authenticationManager = authenticationManager;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.loginAttempts = loginAttempts;
	}

	// Crea usuario; password hasheado con BCrypt
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
		// username repetido → 409
		if (usuarioRepository.existsByUsername(req.getUsername())) {
			throw new ConflictException("El usuario '" + req.getUsername() + "' ya existe");
		}

		Usuario usuario = new Usuario();
		usuario.setUsername(req.getUsername());
		usuario.setPassword(passwordEncoder.encode(req.getPassword()));
		// el rol nunca viene del cliente; el auto-registro es siempre ROLE_USER
		usuario.setRol("ROLE_USER");

		usuarioRepository.save(usuario);

		// Registra y devuelve token
		String token = jwtService.generateToken(
				org.springframework.security.core.userdetails.User
						.withUsername(usuario.getUsername())
						.password(usuario.getPassword())
						.authorities(usuario.getRol())
						.build(),
				usuario.getRol());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new AuthResponse(token, usuario.getUsername(), usuario.getRol()));
	}

	// Login: devuelve JWT; credenciales inválidas → 401 (GlobalExceptionHandler)
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
		// usuario bloqueado por fallos seguidos → 429
		if (loginAttempts.estaBloqueado(req.getUsername())) {
			throw new TooManyAttemptsException("Demasiados intentos fallidos. Intenta de nuevo mas tarde");
		}

		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
		} catch (BadCredentialsException ex) {
			loginAttempts.registrarFallo(req.getUsername());
			throw ex;
		}
		loginAttempts.registrarExito(req.getUsername());

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String rol = userDetails.getAuthorities().iterator().next().getAuthority();

		String token = jwtService.generateToken(userDetails, rol);

		return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), rol));
	}

}
