package com.chuamany.produccioncampos.dto;

// Respuesta del login; el cliente envía el token en Authorization: Bearer <token>
public class AuthResponse {

	private String token;
	private final String tokenType = "Bearer";
	private String username;
	private String rol;

	public AuthResponse(String token, String username, String rol) {
		this.token = token;
		this.username = username;
		this.rol = rol;
	}

	public String getToken() {
		return token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getUsername() {
		return username;
	}

	public String getRol() {
		return rol;
	}

}
