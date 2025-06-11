package com.example.demo.Controller;

import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UsuarioRepository usuarioRepository;

    public ProfileController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Muestra la página de perfil del usuario actualmente autenticado.
     * @param authentication El objeto de autenticación de Spring Security, inyectado automáticamente.
     * @param model El modelo para pasar datos a la vista.
     * @return La plantilla de la vista de perfil.
     */
    @GetMapping("/perfil")
    public String showProfilePage(Authentication authentication, Model model) {
        // Obtenemos el nombre de usuario (username) del usuario que ha iniciado sesión
        String username = authentication.getName();

        // Buscamos los datos completos del usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Pasamos el objeto usuario completo a la vista de Thymeleaf
        model.addAttribute("usuario", usuario);

        // Devolvemos el nombre del archivo HTML (sin la extensión)
        return "perfilu"; 
    }
}