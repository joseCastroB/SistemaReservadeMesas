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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Declaración del objeto Logger (¡Esta línea es crucial!)
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // --- INICIO DE LOGS DE DEPURACIÓN ---
        log.info("--- loadUserByUsername invocado para el usuario: '{}' ---", username);

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> {
                    log.error("!!! ERROR: Usuario '{}' no fue encontrado en la base de datos.", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        log.info(">>> ÉXITO: Usuario '{}' encontrado en la base de datos.", usuario.getUsuario());
        log.info("    \\--> Rol asignado: {}", usuario.getRol());
        log.info("    \\--> Creando objeto UserDetails de Spring Security...");
        // --- FIN DE LOGS DE DEPURACIÓN ---

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        return new User(usuario.getUsuario(), usuario.getContrasena(), authorities);
    }
}