package com.techmovil.shared.response;

import java.util.List;

/** Wrapper para respuestas paginadas */
public record PaginaResponse<T>(
    List<T> contenido,
    int     paginaActual,
    int     totalPaginas,
    long    totalElementos,
    boolean esUltima
) { }
