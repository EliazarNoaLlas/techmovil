package com.techmovil.inventario.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponseDto(
        Long id,
        String tipoMovimiento,
        String motivo,
        String documentoReferencia,
        LocalDateTime fechaMovimiento,
        String almacenOrigen,
        String almacenDestino,
        String usuario,
        List<DetalleMovimientoResponseDto> detalles
) {
    public record DetalleMovimientoResponseDto(
            Long productoId,
            String productoNombre,
            java.math.BigDecimal cantidad,
            java.math.BigDecimal costoUnitario
    ) {}
}
