package com.techmovil.reportes.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteVentasDto(
        BigDecimal totalVentas,
        BigDecimal totalIgv,
        Long cantidadVentas,
        List<com.techmovil.ventas.application.dto.VentaResponseDto> ventas
) { }
