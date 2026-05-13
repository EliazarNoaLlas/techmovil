package com.techmovil.shared.response;

/** Respuesta estandarizada para todos los endpoints */
public record ApiResponse<T>(
    boolean success,
    String  mensaje,
    T       data,
    Object  errores
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data, null);
    }
    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return new ApiResponse<>(true, mensaje, data, null);
    }
    public static <T> ApiResponse<T> error(String mensaje, Object errores) {
        return new ApiResponse<>(false, mensaje, null, errores);
    }

    public boolean isSuccess() {
        return success;
    }
}
