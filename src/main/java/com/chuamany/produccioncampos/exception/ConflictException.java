package com.chuamany.produccioncampos.exception;

// Conflicto con el estado de los datos → 409
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}

}
