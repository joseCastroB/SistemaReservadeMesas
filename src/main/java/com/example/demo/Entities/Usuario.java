package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String usuario;
    private String contrasena;
    private String rol;
}