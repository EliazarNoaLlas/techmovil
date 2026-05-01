package com.techmovil.reportes.application.usecase;

import com.techmovil.reportes.application.dto.ReporteVentasDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface GenerarReporteUseCase {
    ReporteVentasDto generarVentas(LocalDate inicio, LocalDate fin);
    List<ProductoResponseDto> generarStockCritico();
}
