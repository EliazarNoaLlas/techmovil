package com.techmovil.usuarios.application.usecase;

import com.techmovil.usuarios.application.dto.UsuarioRequestDto;
import com.techmovil.usuarios.application.dto.UsuarioResponseDto;

public interface CrearUsuarioUseCase {
    UsuarioResponseDto ejecutar(UsuarioRequestDto dto);
}
