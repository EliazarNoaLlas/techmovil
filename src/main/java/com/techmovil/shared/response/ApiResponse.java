package com.techmovil.shared.response;

import com.sun.tools.javac.parser.JavacParser;

/** Respuesta estandarizada para todos los endpoints */
public record ApiResponse<T>(
    boolean exito,
    String  mensaje,
    T       datos,
    Object  errores
) {
    public static <T> ApiResponse<T> ok(T datos) {
        return new ApiResponse<>(true, "OK", datos, null);
    }
    public static <T> ApiResponse<T> ok(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos, null);
    }
    public static <T> ApiResponse<T> error(String mensaje, Object errores) {
        return new ApiResponse<>(false, mensaje, null, errores);
    }

    public boolean isSuccess() {
        return false;
    }

    public JavacParser getData() {
        return null;
    }
}
