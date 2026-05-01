package com.techmovil.productos.application.usecase;

import com.techmovil.productos.application.dto.ProductoResponseDto;
import java.util.List;

public interface BuscarProductosUseCase {
    List<ProductoResponseDto> buscar(Long marcaId, Long categoriaId, String modelo);
}
