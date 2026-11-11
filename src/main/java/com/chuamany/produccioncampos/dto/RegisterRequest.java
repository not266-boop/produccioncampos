package com.chuamany.produccioncampos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Cuerpo de POST /api/auth/register
public class RegisterRequest {

	@NotBlank(message = "El username es obligatorio")
	private String username;

	@NotBlank(message = "El password es obligatorio")
	@Size(min = 6, message = "El password debe tener al menos 6 caracteres")
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
