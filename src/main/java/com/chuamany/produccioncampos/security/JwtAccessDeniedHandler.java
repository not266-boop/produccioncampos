package com.chuamany.produccioncampos.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 403 a nivel de filtro con el mismo JSON de ApiError (el cliente distingue 401 de 403)
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException {

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		String body = """
				{"timestamp":"%s","status":403,"error":"Forbidden","message":"No tienes permisos para realizar esta accion"}""".formatted(Instant.now());

		response.getWriter().write(body);
	}

}
