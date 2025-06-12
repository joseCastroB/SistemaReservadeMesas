package com.example.demo.Controller;

import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.Services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ProfileController {

    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService; // Añadimos el servicio

    public ProfileController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
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

    // --- NUEVO MÉTODO POST: Para guardar cambios del perfil ---
    @PostMapping("/perfil/actualizar")
    public String handleProfileUpdate(Authentication authentication,
                                      @RequestParam String nombreCompleto,
                                      @RequestParam String telefono,
                                      RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            usuarioService.actualizarPerfil(username, nombreCompleto, telefono);
            redirectAttributes.addFlashAttribute("successMessage", "¡Perfil actualizado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
        }
        return "redirect:/perfil";
    }
    
    // --- NUEVO MÉTODO POST: Para cambiar la contraseña ---
    @PostMapping("/perfil/cambiar-contrasena")
    public String handleChangePassword(Authentication authentication,
                                       @RequestParam String contrasenaActual,
                                       @RequestParam String nuevaContrasena,
                                       @RequestParam String confirmarContrasena,
                                       RedirectAttributes redirectAttributes) {
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("passwordError", "Las nuevas contraseñas no coinciden.");
            return "redirect:/perfil";
        }

        try {
            String username = authentication.getName();
            usuarioService.cambiarContrasena(username, contrasenaActual, nuevaContrasena);
            redirectAttributes.addFlashAttribute("passwordSuccess", "¡Contraseña actualizada correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordError", "Error al cambiar la contraseña: " + e.getMessage());
        }
        return "redirect:/perfil";
    }

}