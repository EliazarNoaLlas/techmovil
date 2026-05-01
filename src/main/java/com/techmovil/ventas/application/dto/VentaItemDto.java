package com.techmovil.ventas.application.dto;

import java.math.BigDecimal;

public record VentaItemDto(
    Long       productoId,
    Long       activoId,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    BigDecimal descuento
) { }
