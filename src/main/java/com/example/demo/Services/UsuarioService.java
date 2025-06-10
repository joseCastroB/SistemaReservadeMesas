package com.example.demo.Services;

import com.example.demo.Entities.Usuario;
import com.example.demo.dto.UsuarioRegistroDto;

public interface UsuarioService {
    void registrarUsuario(UsuarioRegistroDto dto);
    Usuario findByUsuario(String usuario);
}
