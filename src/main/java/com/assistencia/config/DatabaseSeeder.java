package com.assistencia.config;

import com.assistencia.model.Usuario;
import com.assistencia.model.enums.Perfil;
import com.assistencia.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario(
                    null,
                    "Administrador",
                    "admin@techfix.com",
                    passwordEncoder.encode("admin123"),
                    Perfil.ROLE_ADMIN,
                    true
            );

            Usuario tecnico = new Usuario(
                    null,
                    "Técnico Ryan",
                    "tecnico@techfix.com",
                    passwordEncoder.encode("tec123"),
                    Perfil.ROLE_TECNICO,
                    true
            );

            usuarioRepository.save(admin);
            usuarioRepository.save(tecnico);
        }
    }
}
