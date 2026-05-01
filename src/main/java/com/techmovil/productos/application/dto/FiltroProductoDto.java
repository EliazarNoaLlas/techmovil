package com.techmovil.productos.application.dto;

import java.math.BigDecimal;

/** Filtros para busqueda de productos (RF-03, RF-10) */
public record FiltroProductoDto(
    Long       marcaId,
    Long       categoriaId,
    String     modelo,
    BigDecimal precioMin,
    BigDecimal precioMax,
    Boolean    soloConStock,
    Boolean    soloCriticos,
    Boolean    activo
) { }
