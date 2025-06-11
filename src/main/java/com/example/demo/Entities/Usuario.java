package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario") // Mapeo explícito
    private Integer idUsuario;

    @Column(name = "nombreCompleto") // Mapeo explícito
    private String nombreCompleto;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "rol", nullable = false)
    private String rol;
}