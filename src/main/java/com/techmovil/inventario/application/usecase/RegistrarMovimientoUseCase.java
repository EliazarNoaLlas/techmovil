package com.techmovil.inventario.application.usecase;

import com.techmovil.inventario.application.dto.MovimientoRequestDto;
import com.techmovil.inventario.application.dto.MovimientoResponseDto;

public interface RegistrarMovimientoUseCase {
    MovimientoResponseDto ejecutar(MovimientoRequestDto dto);
}
