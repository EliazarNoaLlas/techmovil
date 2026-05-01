package com.techmovil.productos.application.usecase;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;

public interface ActualizarProductoUseCase {
    ProductoResponseDto actualizar(Long id, ProductoRequestDto dto);
}
