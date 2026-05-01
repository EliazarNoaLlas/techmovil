package com.techmovil.productos.application.dto;

import java.math.BigDecimal;

/** DTO de salida para producto con IGV calculado */
public record ProductoResponseDto(
    Long       id,
    String     sku,
    String     nombre,
    String     marca,
    String     categoria,
    String     modelo,
    String     color,
    String     capacidad,
    BigDecimal precioVenta,
    BigDecimal igvMonto,
    BigDecimal precioConIgv,
    BigDecimal stockActual,
    BigDecimal stockMinimo,
    boolean    stockCritico,
    boolean    activo
) { }
