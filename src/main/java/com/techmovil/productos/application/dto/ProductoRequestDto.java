package com.techmovil.productos.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO de entrada para crear/editar producto */
public record ProductoRequestDto(
    @NotBlank  String     sku,
    @NotBlank  String     nombre,
    @NotNull   Long       marcaId,
    @NotNull   Long       categoriaId,
               String     modelo,
               String     color,
               String     capacidad,
    @NotNull @Positive BigDecimal precioCompra,
    @NotNull @Positive BigDecimal precioVenta,
    @PositiveOrZero    BigDecimal stockInicial,
    @PositiveOrZero    BigDecimal stockMinimo
) { }
