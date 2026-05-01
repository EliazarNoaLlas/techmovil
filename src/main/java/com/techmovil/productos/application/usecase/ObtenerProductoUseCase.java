package com.techmovil.productos.application.usecase;

import com.techmovil.productos.application.dto.ProductoResponseDto;

public interface ObtenerProductoUseCase {
    ProductoResponseDto obtenerPorId(Long id);
}
