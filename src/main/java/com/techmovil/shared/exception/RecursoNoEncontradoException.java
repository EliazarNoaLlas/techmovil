package com.techmovil.shared.exception;

public class RecursoNoEncontradoException extends TechmovilException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super("RECURSO_NO_ENCONTRADO", recurso + " con id " + id + " no encontrado");
    }
}
