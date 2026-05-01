package com.techmovil.usuarios.application.dto;

import java.util.Set;

public record UsuarioResponseDto(
        Long id,
        String username,
        String nombres,
        String apellidos,
        String email,
        boolean activo,
        boolean bloqueado,
        Set<String> roles
) { }
