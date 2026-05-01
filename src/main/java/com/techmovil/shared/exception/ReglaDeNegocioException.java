package com.techmovil.shared.exception;

public class ReglaDeNegocioException extends TechmovilException {
    public ReglaDeNegocioException(String mensaje) {
        super("REGLA_NEGOCIO", mensaje);
    }
}
