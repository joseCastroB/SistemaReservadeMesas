package com.example.demo.Repository;

import com.example.demo.Entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su nombre de usuario.
     * Spring Data JPA creará la implementación automáticamente.
     * @param usuario El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Busca un usuario por su correo electrónico.
     * Útil para verificar si un correo ya está registrado.
     * @param correo El correo a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<Usuario> findByCorreo(String correo);
}