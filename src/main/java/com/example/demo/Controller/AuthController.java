package com.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.UsuarioService;
import com.example.demo.dto.UsuarioRegistroDto;


@Controller
@RequiredArgsConstructor
public class AuthController {
     private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDto());
        return "register";
    }

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, Model model) {
        usuarioService.registrarUsuario(usuarioRegistroDto);
        model.addAttribute("success", "✅ Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
