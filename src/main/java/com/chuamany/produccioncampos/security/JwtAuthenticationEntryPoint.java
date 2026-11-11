package com.chuamany.produccioncampos.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Responde 401 a rutas protegidas sin token válido (@RestControllerAdvice no llega a nivel de filtro)
// JSON armado a mano con las claves de ApiError
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		String body = """
				{"timestamp":"%s","status":401,"error":"Unauthorized","message":"Se requiere un token JWT valido (header Authorization: Bearer <token>)"}""".formatted(Instant.now());

		response.getWriter().write(body);
	}

}
