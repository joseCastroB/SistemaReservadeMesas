package com.example.demo.Services;

import com.example.demo.Entities.*;
import com.example.demo.Repository.*;

// Importaciones para el logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;

/**
 * Servicio personalizado para cargar usuarios durante el proceso de
 * autenticación
 * 
 * Esta clase implementa la interfaz UserDetailsService de Spring Security.
 * Su función principal es actuar como puente entre el sistema de autenticación
 * de Spring Security y nuestra base de datos de usuarios.
 * 
 * Proceso que maneja:
 * 1. Spring Security llama a loadUserByUsername() durante el login
 * 2. Se busca el usuario en la base de datos usando UsuarioRepository
 * 3. Se convierte la entidad Usuario a UserDetails (formato que entiende Spring
 * Security)
 * 4. Se asignan los roles y permisos correspondientes
 * 
 * Anotaciones utilizadas:
 * - @Service: (Spring) Marca esta clase como un servicio de Spring
 * - @Override: (Java) Indica que estamos sobrescribiendo un método de la
 * interfaz
 * 
 * Flujo de autenticación:
 * Usuario ingresa credenciales → Spring Security → CustomUserDetailsService →
 * Base de Datos
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Logger para depuración y monitoreo del proceso de autenticación
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * Constructor con inyección de dependencias
     * Spring automáticamente inyecta UsuarioRepository
     */
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método principal llamado por Spring Security durante la autenticación
     * 
     * Este método se ejecuta cuando un usuario intenta hacer login.
     * Spring Security proporciona el nombre de usuario ingresado y espera
     * recibir un objeto UserDetails con la información completa del usuario.
     * 
     * Proceso step-by-step:
     * 1. Recibe el nombre de usuario del formulario de login
     * 2. Busca el usuario en la base de datos usando UsuarioRepository
     * 3. Si no existe → lanza UsernameNotFoundException
     * 4. Si existe → convierte Usuario entity a UserDetails
     * 5. Asigna roles: "USER" se convierte en "ROLE_USER", "ADMIN" en "ROLE_ADMIN"
     * 6. Retorna UserDetails para que Spring Security verifique la contraseña
     * 
     * @param username Nombre de usuario ingresado en el formulario de login
     * @return UserDetails objeto con información del usuario para Spring Security
     * @throws UsernameNotFoundException Si el usuario no existe en la base de datos
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Logging para depuración - ayuda a rastrear problemas de autenticación
        log.info("--- loadUserByUsername invocado para el usuario: '{}' ---", username);

        // Buscar usuario en la base de datos
        // findByUsuario() es un método derivado de Spring Data JPA
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> {
                    log.error("!!! ERROR: Usuario '{}' no fue encontrado en la base de datos.", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        // Logging de éxito
        log.info(">>> ÉXITO: Usuario '{}' encontrado en la base de datos.", usuario.getUsuario());
        log.info("    \\--> Rol asignado: {}", usuario.getRol());
        log.info("    \\--> Creando objeto UserDetails de Spring Security...");

        // Convertir rol de la base de datos a formato Spring Security
        // Si usuario.getRol() = "USER" → se convierte en "ROLE_USER"
        // Si usuario.getRol() = "ADMIN" → se convierte en "ROLE_ADMIN"
        Collection<? extends GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        // Crear y retornar UserDetails
        // User.builder() es una clase de Spring Security, no nuestra entidad Usuario
        return User.builder()
                .username(usuario.getUsuario()) // Nombre de usuario
                .password(usuario.getContrasena()) // Contraseña encriptada (Spring Security la verificará)
                .authorities(authorities) // Roles y permisos
                .accountExpired(false) // Cuenta no expirada
                .accountLocked(false) // Cuenta no bloqueada
                .credentialsExpired(false) // Credenciales no expiradas
                .disabled(false) // Cuenta habilitada
                .build();
    }
}