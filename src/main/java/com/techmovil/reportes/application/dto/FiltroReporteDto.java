package com.techmovil.reportes.application.dto;

import java.time.LocalDate;

/** Filtros para generacion de reportes (RF-10) */
public record FiltroReporteDto(
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Long      marcaId,
    Long      categoriaId,
    Long      almacenId
) { }
