package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa un Usuario del sistema
 * 
 * Esta clase implementa el patrón Entity/Model del patrón MVC.
 * Representa la tabla "Usuarios" en la base de datos y contiene
 * toda la información necesaria para la autenticación y gestión de usuarios.
 * 
 * Anotaciones utilizadas:
 * - @Data: (Lombok) Genera automáticamente getters, setters, toString, equals y hashCode
 * - @Entity: (JPA) Marca la clase como una entidad JPA (tabla en BD)
 * - @Table: (JPA) Especifica el nombre de la tabla en la base de datos
 * 
 * Relaciones:
 * - Un Usuario puede tener múltiples Reservas (relación 1:N)
 */
@Data
@Entity
@Table(name = "Usuarios")
public class Usuario {

    /**
     * Identificador único del usuario (clave primaria)
     * 
     * @Id: (JPA) Marca este campo como clave primaria
     * @GeneratedValue: (JPA) La BD genera automáticamente el valor usando AUTO_INCREMENT
     * @Column: (JPA) Mapeo explícito del nombre de columna en la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario") // Mapeo explícito
    private Integer idUsuario;

    /**
     * Nombre completo del usuario
     * Campo requerido para mostrar en las reservas
     */
    @Column(name = "nombreCompleto") // Mapeo explícito
    private String nombreCompleto;

    /**
     * Correo electrónico del usuario
     * 
     * @Column: (JPA) Configuración de la columna:
     * - unique = true: No puede haber valores duplicados
     * - nullable = false: Campo obligatorio (no puede estar vacío)
     * 
     * Se usa para notificaciones y validación de unicidad
     */
    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    /**
     * Número de teléfono del usuario
     * Campo opcional para contacto adicional
     */
    @Column(name = "telefono")
    private String telefono;
    
    /**
     * Nombre de usuario para el login
     * Debe ser único en el sistema
     */
    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    /**
     * Contraseña del usuario (encriptada)
     * Se almacena usando BCrypt hash por seguridad
     */
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    /**
     * Rol del usuario en el sistema
     * Valores posibles: "USER", "ADMIN"
     * Determina los permisos de acceso
     */
    @Column(name = "rol", nullable = false)
    private String rol;
}