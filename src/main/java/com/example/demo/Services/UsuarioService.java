package com.example.demo.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.dto.UsuarioRegistroDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de usuarios del sistema
 * 
 * Esta clase implementa la capa de servicio (Service Layer) del patrón MVC.
 * Contiene toda la lógica de negocio relacionada con la gestión de usuarios,
 * incluyendo registro, autenticación, y actualización de perfiles.
 * 
 * Responsabilidades:
 * - Registro de nuevos usuarios con validaciones
 * - Encriptación de contraseñas usando BCrypt
 * - Validación de unicidad de usuarios y correos
 * - Actualización de datos de perfil
 * - Cambio de contraseñas con validación de seguridad
 * 
 * Patrones implementados:
 * - Service Layer Pattern: Encapsula lógica de negocio
 * - Dependency Injection: Inyección de dependencias via constructor
 * - Transaction Management: Uso de @Transactional para integridad
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // Inyectamos la dependencia del PasswordEncoder que definimos en SecurityConfig
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias
     * 
     * Spring Boot inyecta automáticamente las dependencias:
     * - UsuarioRepository: Para acceso a datos de usuarios
     * - PasswordEncoder: Para encriptar contraseñas (BCrypt)
     * 
     * @param usuarioRepository Repositorio para operaciones de base de datos
     * @param passwordEncoder Codificador de contraseñas configurado en SecurityConfig
     */
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * Proceso de registro:
     * 1. Valida unicidad del nombre de usuario
     * 2. Valida unicidad del correo electrónico
     * 3. Crea nueva entidad Usuario
     * 4. Encripta la contraseña usando BCrypt
     * 5. Asigna rol "USER" por defecto
     * 6. Persiste el usuario en base de datos
     * 
     * Validaciones aplicadas:
     * - Nombre de usuario único
     * - Correo electrónico único
     * - Contraseña encriptada antes de almacenar
     * 
     * @param registroDTO DTO con los datos del formulario de registro
     * @return Usuario creado y persistido en base de datos
     * @throws IllegalStateException Si el usuario o correo ya existen
     */
    public Usuario registrar(UsuarioRegistroDto registroDTO) {
        // Validación de unicidad del nombre de usuario
        if (usuarioRepository.findByUsuario(registroDTO.getUsuario()).isPresent()) {
            throw new IllegalStateException("El nombre de usuario ya está en uso.");
        }
        
        // Validación de unicidad del correo electrónico
        if (usuarioRepository.findByCorreo(registroDTO.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        // Creación de nueva entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setCorreo(registroDTO.getCorreo());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setUsuario(registroDTO.getUsuario());
        
        // Encriptación de contraseña usando BCrypt
        // BCrypt genera automáticamente el salt y es resistente a ataques rainbow table
        usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));
        
        // Asignación de rol por defecto
        usuario.setRol("USER"); 

        // Persistencia en base de datos
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos del perfil de un usuario existente
     * 
     * Permite modificar:
     * - Nombre completo
     * - Número de teléfono
     * 
     * Nota: No permite cambiar usuario, correo o contraseña
     * (para esos cambios existen métodos específicos)
     * 
     * @param username Nombre de usuario a actualizar
     * @param nombreCompleto Nuevo nombre completo
     * @param telefono Nuevo número de teléfono
     * @throws IllegalStateException Si el usuario no existe
     */
    @Transactional
    public void actualizarPerfil(String username, String nombreCompleto, String telefono) {
        // Búsqueda del usuario por nombre de usuario
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));
        
        // Actualización de campos modificables
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setTelefono(telefono);
        
        // Persistencia de cambios
        // @Transactional garantiza que si hay error, se hace rollback
        usuarioRepository.save(usuario);
    }

    /**
     * Cambia la contraseña de un usuario con validación de seguridad
     * 
     * Proceso de cambio de contraseña:
     * 1. Busca el usuario por nombre de usuario
     * 2. Verifica que la contraseña actual sea correcta
     * 3. Encripta la nueva contraseña
     * 4. Actualiza y persiste los cambios
     * 
     * Medidas de seguridad:
     * - Validación de contraseña actual antes del cambio
     * - Encriptación de nueva contraseña con BCrypt
     * - Uso de transacción para garantizar integridad
     * 
     * @param username Nombre de usuario
     * @param contrasenaActual Contraseña actual (para validación)
     * @param nuevaContrasena Nueva contraseña en texto plano
     * @throws IllegalStateException Si el usuario no existe o la contraseña actual es incorrecta
     */
    @Transactional
    public void cambiarContrasena(String username, String contrasenaActual, String nuevaContrasena) {
        // Búsqueda del usuario
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        // Validación de contraseña actual
        // passwordEncoder.matches() compara texto plano con hash BCrypt
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new IllegalStateException("La contraseña actual es incorrecta.");
        }

        // Encriptación y guardado de nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        
        // Persistencia de cambios
        usuarioRepository.save(usuario);
    }

}