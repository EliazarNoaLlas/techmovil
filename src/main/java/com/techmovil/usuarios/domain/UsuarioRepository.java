package com.techmovil.usuarios.domain;

import java.util.Optional;

/**
 * Puerto (interfaz) del repositorio de usuarios.
 * Definido en el dominio, implementado en infraestructura.
 */
public interface UsuarioRepository {
    Optional<Usuario> buscarPorUsername(String username);
    Optional<Usuario> buscarPorId(Long id);
    Usuario guardar(Usuario usuario);
    boolean existePorUsername(String username);
    java.util.List<Usuario> listarTodo();
}
