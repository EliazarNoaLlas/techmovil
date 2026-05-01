package com.techmovil.inventario.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class StockValidatorTest {
    
    // RF-09: Alertas de stock crítico (< 3 unidades)
    @Test
    void esStockCritico_StockMenor3_RetornaTrue() {
        assertTrue(StockValidator.esStockCritico(2));
        assertTrue(StockValidator.esStockCritico(1));
        assertTrue(StockValidator.esStockCritico(0));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {3, 5, 10, 100})
    void esStockCritico_StockMayorOIgual3_RetornaFalse(int stock) {
        assertFalse(StockValidator.esStockCritico(stock));
    }
}
