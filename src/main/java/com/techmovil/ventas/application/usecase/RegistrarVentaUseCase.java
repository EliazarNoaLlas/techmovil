package com.techmovil.ventas.application.usecase;

import com.techmovil.ventas.application.dto.VentaRequestDto;
import com.techmovil.ventas.application.dto.VentaResponseDto;

public interface RegistrarVentaUseCase {
    VentaResponseDto ejecutar(VentaRequestDto dto);
}
