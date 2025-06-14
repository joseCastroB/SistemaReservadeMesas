package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Services.UsuarioService;
import com.example.demo.dto.UsuarioRegistroDto;


@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra el formulario de login.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Devuelve login.html
    }
    
    /**
     * Muestra el formulario de registro.
     * Prepara un objeto DTO vacío para el binding del formulario.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDto());
        return "register"; // Devuelve register.html
    }

    /**
     * Procesa el formulario de registro.
     */
    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("usuario") UsuarioRegistroDto registroDTO, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrar(registroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
    
    // El método adminPage() que estaba aquí ha sido eliminado para resolver el conflicto.
    // El nuevo AdminController ahora maneja la ruta /admin.
}