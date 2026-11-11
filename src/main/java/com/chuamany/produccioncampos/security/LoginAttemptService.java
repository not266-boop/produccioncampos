package com.chuamany.produccioncampos.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// bloquea el login de un usuario tras N fallos seguidos (en memoria; se pierde al reiniciar)
@Component
public class LoginAttemptService {

	private record Intentos(int fallos, Instant bloqueadoHasta) {}

	private final Map<String, Intentos> registro = new ConcurrentHashMap<>();
	private final int maxIntentos;
	private final Duration bloqueo;

	public LoginAttemptService(
			@Value("${app.login.max-attempts:5}") int maxIntentos,
			@Value("${app.login.lock-minutes:15}") long minutos) {
		this.maxIntentos = maxIntentos;
		this.bloqueo = Duration.ofMinutes(minutos);
	}

	// true si el usuario sigue bloqueado; limpia el bloqueo vencido
	public boolean estaBloqueado(String username) {
		Intentos i = registro.get(clave(username));
		if (i == null || i.bloqueadoHasta() == null) return false;
		if (Instant.now().isAfter(i.bloqueadoHasta())) {
			registro.remove(clave(username));
			return false;
		}
		return true;
	}

	// Suma un fallo; al llegar al máximo fija la hora de desbloqueo
	public void registrarFallo(String username) {
		registro.merge(clave(username), new Intentos(1, null), (viejo, nuevo) -> {
			int fallos = viejo.fallos() + 1;
			return new Intentos(fallos, fallos >= maxIntentos ? Instant.now().plus(bloqueo) : null);
		});
	}

	// Login correcto: reinicia el contador
	public void registrarExito(String username) {
		registro.remove(clave(username));
	}

	private String clave(String username) {
		return username == null ? "" : username.toLowerCase();
	}

}
