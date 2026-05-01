package com.techmovil.inventario.domain;

/**
 * Validador de stock para alertas críticas.
 * RF-09: Alertas de stock crítico (< 3 unidades).
 */
public final class StockValidator {

    private StockValidator() {}

    /**
     * Verifica si un nivel de stock es considerado crítico.
     * @param stock Cantidad actual en inventario.
     * @return true si el stock es menor a 3.
     */
    public static boolean esStockCritico(int stock) {
        return stock < 3;
    }
}
