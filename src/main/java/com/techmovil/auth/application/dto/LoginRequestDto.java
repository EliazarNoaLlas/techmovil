package com.techmovil.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de entrada para el endpoint POST /api/auth/login (RF-01) */
public record LoginRequestDto(
    @NotBlank String username,
    @NotBlank String password
) { }
