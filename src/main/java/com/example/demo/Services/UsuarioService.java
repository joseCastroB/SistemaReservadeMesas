package com.example.demo.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.dto.UsuarioRegistroDto;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // Inyectamos la dependencia del PasswordEncoder que definimos en SecurityConfig
    private final PasswordEncoder passwordEncoder;

    // El constructor ahora recibe el PasswordEncoder como una dependencia
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(UsuarioRegistroDto registroDTO) {
        if (usuarioRepository.findByUsuario(registroDTO.getUsuario()).isPresent()) {
            throw new IllegalStateException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.findByCorreo(registroDTO.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setCorreo(registroDTO.getCorreo());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setUsuario(registroDTO.getUsuario());
        
        // Usamos el PasswordEncoder inyectado para codificar la contraseña
        usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));
        
        usuario.setRol("USER"); 

        return usuarioRepository.save(usuario);
    }

    // --- NUEVO MÉTODO: Actualizar datos del perfil ---
    @Transactional
    public void actualizarPerfil(String username, String nombreCompleto, String telefono) {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));
        
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setTelefono(telefono);
        
        usuarioRepository.save(usuario);
    }

    // --- NUEVO MÉTODO: Cambiar la contraseña ---
    @Transactional
    public void cambiarContrasena(String username, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        // 1. Verificamos que la contraseña actual sea correcta
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new IllegalStateException("La contraseña actual es incorrecta.");
        }

        // 2. Codificamos y guardamos la nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        
        usuarioRepository.save(usuario);
    }

}