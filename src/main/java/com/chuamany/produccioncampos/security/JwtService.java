package com.chuamany.produccioncampos.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// Genera y valida JWT (firma HMAC-SHA256 con jwt.secret)
@Component
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-ms}") long expirationMs) {
		// Clave en Base64 (application.properties); se decodifica a bytes
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		this.expirationMs = expirationMs;
	}

	// Genera el token en el login: sub = username, claim propio "rol".
	public String generateToken(UserDetails userDetails, String rol) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(userDetails.getUsername())
				.claim("rol", rol)
				.issuedAt(now)
				.expiration(expiration)
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRol(String token) {
		return extractAllClaims(token).get("rol", String.class);
	}

	// Valida: la firma corresponde a este servidor Y el token no expiro.
	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	private Claims extractAllClaims(String token) {
		// Firma inválida lanza excepción antes de leer el contenido
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

}
