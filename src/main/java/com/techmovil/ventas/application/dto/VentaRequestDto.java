package com.techmovil.ventas.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record VentaRequestDto(
        @NotBlank String clienteNombre,
        @NotBlank String clienteDocumento,
        @NotNull Long usuarioId,
        @NotBlank String tipoComprobante,
        @NotEmpty List<DetalleVentaDto> detalles
) {
    public record DetalleVentaDto(
            @NotNull Long productoId,
            @NotNull @Positive BigDecimal cantidad,
            @NotNull @Positive BigDecimal precioUnitario
    ) {}
}
