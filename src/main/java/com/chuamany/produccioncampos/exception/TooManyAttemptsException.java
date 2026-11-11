package com.chuamany.produccioncampos.exception;

// demasiados intentos de login → 429
public class TooManyAttemptsException extends RuntimeException {

	public TooManyAttemptsException(String message) {
		super(message);
	}

}
