package com.techmovil.productos.unitary;

import com.techmovil.shared.util.IgvCalculadora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del cálculo de IGV (18%).
 * RF-06: Cálculo exacto de impuestos para comprobantes.
 */
@DisplayName("IGV Calculadora - Pruebas Unitarias")
class IgvCalculadoraTest {

    private static final BigDecimal IGV_PORCENTAJE = new BigDecimal("18.00");

    @Nested
    @DisplayName("Cálculo de Monto IGV")
    class CalculoMontoIgv {

        @ParameterizedTest
        @CsvSource({
                "100.00, 18.00",
                "500.00, 90.00",
                "1000.00, 180.00",
                "1500.00, 270.00",
                "2000.00, 360.00",
                "0.00, 0.00",
                "1.00, 0.18"
        })
        @DisplayName("Debe calcular IGV correctamente para diferentes precios")
        void debeCalcularIgvCorrectamente(String precio, String igvEsperado) {
            BigDecimal montoIgv = IgvCalculadora.calcularMontoIgv(
                    new BigDecimal(precio), true);
            assertEquals(new BigDecimal(igvEsperado), montoIgv);
        }

        @Test
        @DisplayName("Debe retornar cero si no aplica IGV")
        void debeRetornarCeroSinIgv() {
            BigDecimal montoIgv = IgvCalculadora.calcularMontoIgv(
                    new BigDecimal("1500.00"), false);
            assertEquals(BigDecimal.ZERO, montoIgv);
        }

        @ParameterizedTest
        @CsvSource({
                "100.00, 118.00",
                "500.00, 590.00",
                "1000.00, 1180.00",
                "1500.00, 1770.00",
                "0.00, 0.00"
        })
        @DisplayName("Debe calcular total con IGV incluido")
        void debeCalcularTotalConIgv(String precio, String totalEsperado) {
            BigDecimal total = IgvCalculadora.calcularMontoTotalConIgv(
                    new BigDecimal(precio), true);
            assertEquals(new BigDecimal(totalEsperado), total);
        }
    }

    @Nested
    @DisplayName("Precisión Decimal")
    class PrecisionDecimal {

        @Test
        @DisplayName("Debe mantener precisión de 2 decimales")
        void debeMantenerPrecision2Decimales() {
            BigDecimal resultado = IgvCalculadora.calcularMontoIgv(
                    new BigDecimal("33.33"), true);
            assertEquals(2, resultado.scale());
        }

        @Test
        @DisplayName("Debe manejar números grandes sin pérdida de precisión")
        void debeManejarNumerosGrandes() {
            BigDecimal precio = new BigDecimal("999999.99");
            BigDecimal igv = IgvCalculadora.calcularMontoIgv(precio, true);
            BigDecimal total = IgvCalculadora.calcularMontoTotalConIgv(precio, true);

            assertNotNull(igv);
            assertNotNull(total);
            assertTrue(total.compareTo(precio) > 0,
                    "Total con IGV debe ser mayor al precio base");
        }

        @ParameterizedTest
        @CsvSource({
                "1500.00, 270.00, 1770.00",
                "899.99, 161.9982, 1061.9882",
                "2500.50, 450.09, 2950.59"
        })
        @DisplayName("Debe mantener consistencia: precio + IGV = total")
        void debeMantenerConsistenciaPrecioMasIgvIgualTotal(
                String precio, String igvEsperado, String totalEsperado) {
            BigDecimal precioBase = new BigDecimal(precio);
            BigDecimal igv = IgvCalculadora.calcularMontoIgv(precioBase, true);
            BigDecimal total = IgvCalculadora.calcularMontoTotalConIgv(precioBase, true);

            assertEquals(0, total.compareTo(precioBase.add(igv)),
                    "Total debe ser igual a precio + IGV");
        }
    }
}