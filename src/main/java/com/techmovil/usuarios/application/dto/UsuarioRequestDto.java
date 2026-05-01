package com.techmovil.usuarios.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UsuarioRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @Email String email,
        @NotNull Long empresaId,
        Set<String> roles
) { }
