package com.techmovil.ventas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDto(
        Long id,
        String numeroComprobante,
        String tipoComprobante,
        LocalDateTime fechaVenta,
        String vendedor,
        String clienteNombre,
        String clienteDocumento,
        List<DetalleVentaResponseDto> detalles,
        BigDecimal subtotal,
        BigDecimal igvMonto,
        BigDecimal total
) {
    public record DetalleVentaResponseDto(
            Long productoId,
            String productoNombre,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            BigDecimal igv,
            BigDecimal total
    ) {}
}
