package com.techmovil.auth.application.dto;

/** DTO de respuesta con el token JWT generado */
public record LoginResponseDto(
    String token,
    String tipo,
    String username,
    String nombreCompleto,
    java.util.List<String> roles
) { }
