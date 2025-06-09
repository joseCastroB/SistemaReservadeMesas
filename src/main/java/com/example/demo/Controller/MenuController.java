package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String mostrarMenu() {
        return "menu"; // Thymeleaf buscará 'menu.html' en /templates
    }
}   
