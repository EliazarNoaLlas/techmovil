package com.techmovil.usuarios.application.usecase;

import com.techmovil.usuarios.application.dto.UsuarioRequestDto;
import com.techmovil.usuarios.application.dto.UsuarioResponseDto;

public interface ActualizarUsuarioUseCase {
    UsuarioResponseDto ejecutar(Long id, UsuarioRequestDto dto);
}
