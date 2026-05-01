package com.techmovil.shared.exception;

public class StockInsuficienteException extends TechmovilException {
    public StockInsuficienteException(String producto, double disponible) {
        super("STOCK_INSUFICIENTE",
              "Stock insuficiente para '" + producto + "'. Disponible: " + disponible);
    }
}
