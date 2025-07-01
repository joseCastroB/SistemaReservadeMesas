package com.example.demo.dto;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para el formulario de registro de usuarios
 * 
 * Este DTO implementa el patrón Data Transfer Object para manejar
 * los datos del formulario de registro de nuevos usuarios.
 * 
 * Funcionalidades:
 * - Captura datos del formulario de registro
 * - Se valida en UsuarioService antes de crear la entidad Usuario
 * - La contraseña se encripta antes de almacenar en base de datos
 * - Se asigna automáticamente el rol "USER" a nuevos registros
 * 
 * Validaciones aplicadas en el servicio:
 * - Unicidad del nombre de usuario
 * - Unicidad del correo electrónico
 * - Encriptación de contraseña con BCrypt
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Data
public class UsuarioRegistroDto {
    
    /**
     * Nombre completo del usuario
     * Se usará para mostrar en las reservas y perfil
     */
    private String nombreCompleto;
    
    /**
     * Correo electrónico del usuario
     * Debe ser único en el sistema
     * Se usará para futuras notificaciones
     */
    private String correo;
    
    /**
     * Número de teléfono del usuario
     * Campo opcional para contacto adicional
     */
    private String telefono;
    
    /**
     * Nombre de usuario para login
     * Debe ser único en el sistema
     * Se usa junto con la contraseña para autenticación
     */
    private String usuario;
    
    /**
     * Contraseña del usuario en texto plano
     * Se encriptará con BCrypt antes de almacenar
     * Nota: Se usa 'contrasena' sin tilde para evitar problemas de codificación
     */
    private String contrasena; // Cambiado de contraseña
}
