package com.techmovil.usuarios.application.usecase;

import com.techmovil.usuarios.application.dto.UsuarioResponseDto;
import java.util.List;

public interface ListarUsuariosUseCase {
    List<UsuarioResponseDto> ejecutar();
}
