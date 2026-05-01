package com.techmovil.productos.application.usecase;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;
import jakarta.validation.Valid;

// TODO: RF-02 - Registrar nuevo equipo celular en el catalogo
public interface CrearProductoUseCase {
    ProductoResponseDto crear(@Valid ProductoRequestDto request);
}
