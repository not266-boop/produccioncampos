package com.chuamany.produccioncampos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chuamany.produccioncampos.model.Usuario;
import com.chuamany.produccioncampos.repository.UsuarioRepository;

// Solo desarrollo: siembra usuarios de prueba si app.seed-users=true (perfil dev)
@Configuration
public class DataInitializer {

	@Bean
	@ConditionalOnProperty(name = "app.seed-users", havingValue = "true")
	public CommandLineRunner seedUsuarios(UsuarioRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.count() == 0) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin123"));
				admin.setRol("ROLE_ADMIN");
				repo.save(admin);

				Usuario user = new Usuario();
				user.setUsername("user");
				user.setPassword(encoder.encode("user123"));
				user.setRol("ROLE_USER");
				repo.save(user);
			}
		};
	}

}
