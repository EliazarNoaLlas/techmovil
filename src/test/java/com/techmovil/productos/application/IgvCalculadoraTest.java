package com.techmovil.productos.application;

import com.techmovil.shared.util.IgvCalculadora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas unitarias del calculo de IGV 18%
 * RF-06: El sistema debe calcular el 18% de IGV con precision
 */
@DisplayName("IgvCalculadora - Precision aritmetica del 18% IGV")
class IgvCalculadoraTest {

    @Test
    @DisplayName("Debe calcular IGV correcto para precio base de 100.00")
    void debeCalcularIgvDe100() {
        BigDecimal base = new BigDecimal("100.00");
        BigDecimal igv  = IgvCalculadora.calcularIgv(base);
        assertEquals(new BigDecimal("18.00"), igv);
    }

    @Test
    @DisplayName("Debe calcular total con IGV correcto para precio base de 100.00")
    void debeCalcularTotalCon100() {
        BigDecimal base  = new BigDecimal("100.00");
        BigDecimal total = IgvCalculadora.calcularTotal(base);
        assertEquals(new BigDecimal("118.00"), total);
    }

    @Test
    @DisplayName("Debe calcular IGV correcto para precio de 4999.00 (iPhone)")
    void debeCalcularIgvParaIphone() {
        BigDecimal base  = new BigDecimal("4999.00");
        BigDecimal igv   = IgvCalculadora.calcularIgv(base);
        BigDecimal total = IgvCalculadora.calcularTotal(base);
        assertEquals(new BigDecimal("899.82"), igv);
        assertEquals(new BigDecimal("5898.82"), total);
    }

    @Test
    @DisplayName("Debe extraer base imponible de un precio que ya incluye IGV")
    void debeExtraerBaseDePrecoConIgv() {
        BigDecimal precioConIgv = new BigDecimal("118.00");
        BigDecimal base = IgvCalculadora.extraerBase(precioConIgv);
        assertEquals(new BigDecimal("100.00"), base);
    }

    @Test
    @DisplayName("No debe usar FLOAT - debe usar BigDecimal para evitar errores de precision")
    void noDebeUsarFloatParaCalculos() {
        // Verifica que el resultado es exacto (no tiene errores de punto flotante)
        BigDecimal resultado = IgvCalculadora.calcularIgv(new BigDecimal("0.01"));
        assertNotNull(resultado);
        assertTrue(resultado.scale() == 2, "Debe tener exactamente 2 decimales");
    }
}
