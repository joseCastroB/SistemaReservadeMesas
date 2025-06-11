package com.example.demo.security;

import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Revisa si el usuario 'admin' ya existe en la base de datos
        if (usuarioRepository.findByUsuario("admin").isPresent()) {
            log.info("El usuario administrador ya existe. No se realizarán acciones.");
        } else {
            // Si no existe, crea y guarda el nuevo usuario administrador
            log.info("Creando usuario administrador por primera vez...");
            Usuario adminUser = new Usuario();
            adminUser.setNombreCompleto("Administrador del Sistema");
            adminUser.setCorreo("admin@sietesopas.com");
            adminUser.setTelefono("987654321");
            adminUser.setUsuario("admin");
            // Codifica la contraseña usando el PasswordEncoder de la aplicación
            adminUser.setContrasena(passwordEncoder.encode("adminpass"));
            adminUser.setRol("ADMIN");
            
            usuarioRepository.save(adminUser);
            log.info("Usuario administrador creado exitosamente.");
        }
    }
}