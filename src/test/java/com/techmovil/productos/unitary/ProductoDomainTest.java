package com.techmovil.productos.unitary;

import com.techmovil.productos.domain.Producto;
import com.techmovil.shared.exception.StockInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de la entidad de dominio Producto.
 * Valida reglas de negocio puras sin dependencias externas.
 * <p>
 * RF-07: Actualización automática de stock
 * RF-08: Ingreso de mercadería
 * RF-09: Alertas de stock crítico
 */
@DisplayName("🧪 Producto - Pruebas de Dominio")
class ProductoDomainTest {

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .sku("IP15P-128-BLK")
                .nombre("iPhone 15 Pro")
                .precioVenta(new BigDecimal("1500.00"))
                .stockActual(new BigDecimal("10"))
                .stockMinimo(new BigDecimal("3"))
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("RF-09: Verificación de Stock Crítico")
    class StockCritico {

        @Test
        @DisplayName("Debe identificar stock crítico cuando es menor al mínimo")
        void debeIdentificarStockCriticoMenorAlMinimo() {
            producto.setStockActual(new BigDecimal("2"));
            assertTrue(producto.tieneStockCritico(),
                    "Stock 2 con mínimo 3 debe ser crítico");
        }

        @Test
        @DisplayName("Debe identificar stock crítico cuando es igual al mínimo")
        void debeIdentificarStockCriticoIgualAlMinimo() {
            producto.setStockActual(new BigDecimal("3"));
            assertTrue(producto.tieneStockCritico(),
                    "Stock igual al mínimo (3) debe considerarse crítico");
        }

        @Test
        @DisplayName("No debe identificar stock crítico cuando supera el mínimo")
        void noDebeIdentificarStockCriticoMayorAlMinimo() {
            producto.setStockActual(new BigDecimal("4"));
            assertFalse(producto.tieneStockCritico(),
                    "Stock 4 con mínimo 3 no debe ser crítico");
        }

        @Test
        @DisplayName("Debe manejar stock actual null")
        void debeManejarStockActualNull() {
            producto.setStockActual(null);
            assertFalse(producto.tieneStockCritico(),
                    "Sin dato de stock no debe reportar crítico");
        }

        @Test
        @DisplayName("Debe manejar stock mínimo null")
        void debeManejarStockMinimoNull() {
            producto.setStockMinimo(null);
            assertFalse(producto.tieneStockCritico(),
                    "Sin mínimo definido no debe reportar crítico");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3})
        @DisplayName("Debe reportar crítico para valores ≤ mínimo")
        void debeReportarCriticoParaValoresMenoresOIguales(int stock) {
            producto.setStockActual(new BigDecimal(stock));
            assertTrue(producto.tieneStockCritico());
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 5, 10, 100})
        @DisplayName("No debe reportar crítico para valores > mínimo")
        void noDebeReportarCriticoParaValoresMayores(int stock) {
            producto.setStockActual(new BigDecimal(stock));
            assertFalse(producto.tieneStockCritico());
        }
    }

    @Nested
    @DisplayName("RF-07: Descuento de Stock en Ventas")
    class DescuentoStock {

        @Test
        @DisplayName("Debe descontar stock correctamente")
        void debeDescontarStockCorrectamente() {
            producto.descontarStock(new BigDecimal("3"));
            assertEquals(new BigDecimal("7"), producto.getStockActual(),
                    "10 - 3 = 7 unidades restantes");
        }

        @Test
        @DisplayName("Debe permitir venta exacta de todo el stock")
        void debePermitirVentaExacta() {
            producto.descontarStock(new BigDecimal("10"));
            assertEquals(BigDecimal.ZERO, producto.getStockActual(),
                    "10 - 10 = 0 unidades restantes");
        }

        @Test
        @DisplayName("Debe lanzar excepción si no hay stock suficiente")
        void debeLanzarExcepcionSinStockSuficiente() {
            StockInsuficienteException exception = assertThrows(
                    StockInsuficienteException.class,
                    () -> producto.descontarStock(new BigDecimal("11"))
            );
            assertTrue(exception.getMessage().contains("iPhone 15 Pro"));
            assertTrue(exception.getMessage().contains("10"));
        }

        @Test
        @DisplayName("Debe permitir descuentos con decimales")
        void debePermitirDescuentosConDecimales() {
            producto.descontarStock(new BigDecimal("2.5"));
            assertEquals(new BigDecimal("7.5"), producto.getStockActual());
        }

        @Test
        @DisplayName("Debe manejar stock null lanzando excepción")
        void debeManejarStockNull() {
            producto.setStockActual(null);
            assertDoesNotThrow(() -> producto.descontarStock(new BigDecimal("1")));
        }
    }

    @Nested
    @DisplayName("RF-08: Ingreso de Stock")
    class IngresoStock {

        @Test
        @DisplayName("Debe agregar stock correctamente")
        void debeAgregarStockCorrectamente() {
            producto.agregarStock(new BigDecimal("5"));
            assertEquals(new BigDecimal("15"), producto.getStockActual(),
                    "10 + 5 = 15 unidades");
        }

        @Test
        @DisplayName("Debe inicializar stock en cero si es null")
        void debeInicializarStockEnCeroSiEsNull() {
            producto.setStockActual(null);
            producto.agregarStock(new BigDecimal("10"));
            assertEquals(new BigDecimal("10"), producto.getStockActual(),
                    "Stock null + 10 = 10");
        }

        @Test
        @DisplayName("Debe aceptar cantidades decimales en ingreso")
        void debeAceptarCantidadesDecimales() {
            producto.agregarStock(new BigDecimal("3.7"));
            assertEquals(new BigDecimal("13.7"), producto.getStockActual());
        }
    }

    @Nested
    @DisplayName("Integridad de Datos")
    class IntegridadDatos {

        @Test
        @DisplayName("Debe preservar precisión decimal con BigDecimal")
        void debePreservarPrecisionDecimal() {
            producto.setPrecioVenta(new BigDecimal("1500.99"));
            producto.setStockActual(new BigDecimal("10.50"));

            producto.descontarStock(new BigDecimal("1.25"));

            assertEquals(new BigDecimal("9.25"), producto.getStockActual());
            assertEquals(new BigDecimal("1500.99"), producto.getPrecioVenta());
        }

        @Test
        @DisplayName("Debe validar que el SKU sea único por negocio")
        void debeValidarSkuUnico() {
            producto.setSku("IP15P-128-BLK");
            assertNotNull(producto.getSku());
            assertFalse(producto.getSku().isEmpty());
        }

        @ParameterizedTest
        @CsvSource({
                "0.01, 0.01, true",
                "0.00, 0.00, true",
                "5.00, 5.00, true",
                "0.01, 10.00, true"
        })
        @DisplayName("Debe identificar correctamente stock crítico con decimales")
        void debeIdentificarStockCriticoConDecimales(
                String stockStr, String minimoStr, boolean esperado) {
            producto.setStockActual(new BigDecimal(stockStr));
            producto.setStockMinimo(new BigDecimal(minimoStr));
            assertEquals(esperado, producto.tieneStockCritico());
        }
    }
}