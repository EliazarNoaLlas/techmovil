package com.techmovil.inventario.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record MovimientoRequestDto(
        @NotBlank String tipoMovimiento,
        @NotBlank String motivo,
        String documentoReferencia,
        String observaciones,
        Long almacenOrigenId,
        Long almacenDestinoId,
        @NotNull Long usuarioId,
        @NotEmpty List<DetalleMovimientoDto> detalles
) {
    public record DetalleMovimientoDto(
            @NotNull Long productoId,
            @NotNull @Positive BigDecimal cantidad,
            @NotNull @Positive BigDecimal costoUnitario
    ) {}
}
