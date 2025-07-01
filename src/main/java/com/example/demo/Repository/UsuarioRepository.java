package com.example.demo.Repository;

import com.example.demo.Entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Usuario
 * 
 * Esta interfaz implementa el patrón Repository/DAO usando Spring Data JPA.
 * Spring Boot crea automáticamente la implementación de todos los métodos
 * basándose en las convenciones de nomenclatura.
 * 
 * Funcionalidades automáticas proporcionadas por JpaRepository:
 * - save(entity): Guardar o actualizar usuario
 * - findById(id): Buscar usuario por ID
 * - findAll(): Obtener todos los usuarios
 * - delete(entity): Eliminar usuario
 * - count(): Contar total de usuarios
 * 
 * Métodos de consulta personalizados:
 * Spring Data JPA genera automáticamente la implementación basándose
 * en el nombre del método. Los prefijos 'findBy' indican consultas SELECT.
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * Spring Data JPA creará automáticamente la implementación:
     * Query SQL generada: SELECT * FROM usuarios WHERE usuario = ?
     * 
     * @param usuario El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra, 
     *         Optional.empty() si no existe.
     */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Busca un usuario por su correo electrónico.
     * 
     * Útil para:
     * - Verificar si un correo ya está registrado durante el registro
     * - Implementar funcionalidad de "olvidé mi contraseña" (futuro)
     * - Validaciones de unicidad
     * 
     * Query SQL generada: SELECT * FROM usuarios WHERE correo = ?
     * 
     * @param correo El correo a buscar.
     * @return Un Optional que contiene el usuario si se encuentra,
     *         Optional.empty() si no existe.
     */
    Optional<Usuario> findByCorreo(String correo);
}