package com.techmovil.shared.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculadora de IGV centralizada.
 * RF-06: El sistema debe calcular el 18% de IGV automaticamente.
 */
public final class IgvCalculadora {

    private IgvCalculadora() {}

    public static final BigDecimal TASA_IGV = new BigDecimal("0.18");

    /** Calcula el monto de IGV dado un precio base */
    public static BigDecimal calcularIgv(BigDecimal subtotal) {
        return subtotal.multiply(TASA_IGV).setScale(2, RoundingMode.HALF_UP);
    }

    /** Calcula el total incluyendo IGV */
    public static BigDecimal calcularTotal(BigDecimal subtotal) {
        return subtotal.add(calcularIgv(subtotal));
    }

    /** Extrae la base imponible de un precio que YA incluye IGV */
    public static BigDecimal extraerBase(BigDecimal precioConIgv) {
        return precioConIgv.divide(BigDecimal.ONE.add(TASA_IGV), 2, RoundingMode.HALF_UP);
    }

    /** Métodos compatibles con la implementación de productos */
    public static BigDecimal calcularMontoIgv(BigDecimal precio, boolean incluyeIgv) {
        if (incluyeIgv) {
            BigDecimal base = extraerBase(precio);
            return precio.subtract(base);
        }
        return calcularIgv(precio);
    }

    public static BigDecimal calcularMontoTotalConIgv(BigDecimal precio, boolean incluyeIgv) {
        if (incluyeIgv) return precio;
        return calcularTotal(precio);
    }
}
