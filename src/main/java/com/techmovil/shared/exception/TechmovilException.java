package com.techmovil.shared.exception;

/** Excepcion base del dominio TECHMOVIL */
public class TechmovilException extends RuntimeException {
    private final String codigo;
    public TechmovilException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    public String getCodigo() { return codigo; }
}
