package com.techmovil.auth.application;

import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.auth.application.dto.LoginResponseDto;

/**
 * Caso de uso: Autenticacion de usuario.
 * RF-01: login segun usuario y contrasena para roles Administrador y Vendedor.
 */
public interface LoginUseCase {
    LoginResponseDto ejecutar(LoginRequestDto request);
}
