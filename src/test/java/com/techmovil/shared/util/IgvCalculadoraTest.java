package com.techmovil.shared.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class IgvCalculadoraTest {
    
    // TC-007: Cálculo correcto del 18% de IGV
    @Test
    void calcularIGV_PrecioBase1500_Retorna270() {
        // Arrange
        BigDecimal precioBase = new BigDecimal("1500.00");
        
        // Act
        BigDecimal igv = IgvCalculadora.calcularIgv(precioBase);
        BigDecimal total = IgvCalculadora.calcularTotal(precioBase);
        
        // Assert
        assertEquals(new BigDecimal("270.00"), igv, 
            "El IGV del 18% sobre 1500 debe ser 270.00");
        assertEquals(new BigDecimal("1770.00"), total,
            "El total debe ser precio + IGV = 1770.00");
    }
    
    @ParameterizedTest
    @CsvSource({
        "100.00, 18.00, 118.00",
        "500.00, 90.00, 590.00",
        "1000.00, 180.00, 1180.00",
        "0.01, 0.00, 0.01" // Note: 0.01 * 0.18 = 0.0018 -> 0.00 scaled
    })
    void calcularIGV_MultiplesPrecios_CalculoExacto(
        String precio, String igvEsperado, String totalEsperado) {
        
        BigDecimal precioBase = new BigDecimal(precio);
        BigDecimal igv = IgvCalculadora.calcularIgv(precioBase);
        BigDecimal total = IgvCalculadora.calcularTotal(precioBase);
        
        assertEquals(new BigDecimal(igvEsperado), igv);
        assertEquals(new BigDecimal(totalEsperado), total);
    }

    @Test
    void extraerBase_PrecioConIgv_RetornaBaseCorrecta() {
        BigDecimal total = new BigDecimal("118.00");
        BigDecimal base = IgvCalculadora.extraerBase(total);
        assertEquals(new BigDecimal("100.00"), base);
    }

    @Test
    void calcularMontoIgv_IncluyeIgvTrue_RetornaDiferencia() {
        BigDecimal precio = new BigDecimal("118.00");
        BigDecimal igv = IgvCalculadora.calcularMontoIgv(precio, true);
        assertEquals(new BigDecimal("18.00"), igv);
    }

    @Test
    void calcularMontoTotalConIgv_IncluyeIgvFalse_RetornaSuma() {
        BigDecimal precio = new BigDecimal("100.00");
        BigDecimal total = IgvCalculadora.calcularMontoTotalConIgv(precio, false);
        assertEquals(new BigDecimal("118.00"), total);
    }
}
