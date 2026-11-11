package com.chuamany.produccioncampos.exception;

import java.time.Instant;
import java.util.Map;

// Formato único de error de la API
public class ApiError {

	private final Instant timestamp = Instant.now();
	private int status;
	private String error;
	private String message;
	private Map<String, String> fieldErrors;

	public ApiError(int status, String error, String message) {
		this.status = status;
		this.error = error;
		this.message = message;
	}

	public ApiError(int status, String error, String message, Map<String, String> fieldErrors) {
		this(status, error, message);
		this.fieldErrors = fieldErrors;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}

}
