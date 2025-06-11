package com.example.demo.dto;
import lombok.Data;

@Data
public class UsuarioRegistroDto {
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String usuario;
    private String contrasena; // Cambiado de contraseña
}
