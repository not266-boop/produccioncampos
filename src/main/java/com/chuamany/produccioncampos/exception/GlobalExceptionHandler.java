package com.chuamany.produccioncampos.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Manejo centralizado de errores de los Controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 404: recurso no existe
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// 400: falla Bean Validation (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

		ApiError error = new ApiError(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Uno o mas campos no son validos",
				fieldErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 400: parametro no permitido, por ejemplo un orden fuera de la lista blanca
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 401: credenciales inválidas en login
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
		ApiError error = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "Usuario o password incorrectos");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	// 403: autenticado sin rol suficiente
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
		ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), "Forbidden", "No tienes permisos para realizar esta accion");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	// 400 por JSON malformado o cuerpo ilegible
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "El cuerpo de la peticion no es un JSON valido");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 400 por parámetro de ruta con tipo incorrecto (ej. /insumos/abc)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "El parametro '" + ex.getName() + "' no tiene el formato esperado");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 429 por bloqueo temporal de login
	@ExceptionHandler(TooManyAttemptsException.class)
	public ResponseEntity<ApiError> handleTooMany(TooManyAttemptsException ex) {
		ApiError error = new ApiError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", ex.getMessage());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
	}

	// 409 por duplicado detectado en el Service/Controller
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> handleDuplicate(ConflictException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	// 409 por violación de unicidad (ej. username repetido)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Conflict", "El recurso ya existe o viola una restriccion de datos");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	// 404 por ruta inexistente (evita que caiga en el 500 genérico)
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", "La ruta solicitada no existe");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// 500 genérico con ApiError; no filtra el detalle interno al cliente
	// Las excepciones de MVC (405, 415...) implementan ErrorResponse y conservan su status
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
		if (ex instanceof ErrorResponse er) {
			HttpStatusCode code = er.getStatusCode();
			HttpStatus http = HttpStatus.resolve(code.value());
			String reason = http != null ? http.getReasonPhrase() : "Error";
			ApiError error = new ApiError(code.value(), reason, "La peticion no puede procesarse: " + reason);
			return ResponseEntity.status(code).body(error);
		}
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "Error interno del servidor");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

}
