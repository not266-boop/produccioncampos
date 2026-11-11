package com.chuamany.produccioncampos.dto;

import jakarta.validation.constraints.NotBlank;

// Cuerpo de POST /api/auth/login
public class LoginRequest {

	@NotBlank(message = "El username es obligatorio")
	private String username;

	@NotBlank(message = "El password es obligatorio")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
