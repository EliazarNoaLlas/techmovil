package com.techmovil.productos.integration;

import com.techmovil.productos.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración del repositorio JPA.
 * Valida la persistencia real con H2 en memoria.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("🗄️ ProductoJpaRepository - Integración BD")
class ProductoJpaRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductoJpaRepository productoRepository;

    @Autowired
    private MarcaJpaRepository marcaRepository;

    @Autowired
    private CategoriaJpaRepository categoriaRepository;

    private MarcaEntity marcaApple;
    private MarcaEntity marcaSamsung;
    private CategoriaEntity categoriaSmartphone;
    private CategoriaEntity categoriaAccesorios;

    @BeforeEach
    void setUp() {
        // Crear marcas
        marcaApple = new MarcaEntity();
        marcaApple.setNombre("Apple");
        marcaApple.setActivo(true);
        marcaApple = entityManager.persistAndFlush(marcaApple);

        marcaSamsung = new MarcaEntity();
        marcaSamsung.setNombre("Samsung");
        marcaSamsung.setActivo(true);
        marcaSamsung = entityManager.persistAndFlush(marcaSamsung);

        // Crear categorías
        categoriaSmartphone = new CategoriaEntity();
        categoriaSmartphone.setNombre("Smartphones");
        categoriaSmartphone.setNivel(1);
        categoriaSmartphone.setActivo(true);
        categoriaSmartphone = entityManager.persistAndFlush(categoriaSmartphone);

        categoriaAccesorios = new CategoriaEntity();
        categoriaAccesorios.setNombre("Accesorios");
        categoriaAccesorios.setNivel(1);
        categoriaAccesorios.setActivo(true);
        categoriaAccesorios = entityManager.persistAndFlush(categoriaAccesorios);
    }

    @Nested
    @DisplayName("Consultas con Specifications")
    class ConsultasSpecifications {

        @Test
        @DisplayName("Debe filtrar productos por marca")
        void debeFiltrarProductosPorMarca() {
            // Arrange
            crearProducto("iPhone 15", marcaApple, categoriaSmartphone,
                    new BigDecimal("1500.00"), new BigDecimal("10"), new BigDecimal("3"));
            crearProducto("Galaxy S24", marcaSamsung, categoriaSmartphone,
                    new BigDecimal("1200.00"), new BigDecimal("8"), new BigDecimal("2"));

            entityManager.flush();
            entityManager.clear();

            // Act
            Specification<ProductoEntity> spec = ProductoSpecification.porMarca(marcaApple.getId());
            List<ProductoEntity> resultados = productoRepository.findAll(spec);

            // Assert
            assertEquals(1, resultados.size());
            assertEquals("iPhone 15", resultados.get(0).getNombre());
        }

        @Test
        @DisplayName("Debe filtrar productos con stock crítico")
        void debeFiltrarProductosConStockCritico() {
            // Arrange
            crearProducto("iPhone 15", marcaApple, categoriaSmartphone,
                    new BigDecimal("1500.00"), new BigDecimal("2"), new BigDecimal("3"));
            crearProducto("Galaxy S24", marcaSamsung, categoriaSmartphone,
                    new BigDecimal("1200.00"), new BigDecimal("10"), new BigDecimal("2"));

            entityManager.flush();
            entityManager.clear();

            // Act
            List<ProductoEntity> criticos = productoRepository.findAll(
                    ProductoSpecification.soloCriticos());

            // Assert
            assertEquals(1, criticos.size());
            assertEquals("iPhone 15", criticos.get(0).getNombre());
            assertEquals(new BigDecimal("2"), criticos.get(0).getStockActual());
        }

        @Test
        @DisplayName("Debe filtrar por rango de precios")
        void debeFiltrarPorRangoDePrecios() {
            // Arrange
            crearProducto("iPhone 15", marcaApple, categoriaSmartphone,
                    new BigDecimal("1500.00"), new BigDecimal("10"), new BigDecimal("3"));
            crearProducto("Galaxy S24", marcaSamsung, categoriaSmartphone,
                    new BigDecimal("1200.00"), new BigDecimal("8"), new BigDecimal("2"));
            crearProducto("Cargador USB-C", marcaApple, categoriaAccesorios,
                    new BigDecimal("50.00"), new BigDecimal("20"), new BigDecimal("5"));

            entityManager.flush();
            entityManager.clear();

            // Act
            Specification<ProductoEntity> spec = Specification.allOf(
                    ProductoSpecification.porPrecioEntre(
                            new BigDecimal("1000.00"), new BigDecimal("2000.00")));
            List<ProductoEntity> resultados = productoRepository.findAll(spec);

            // Assert
            assertEquals(2, resultados.size());
            assertTrue(resultados.stream().allMatch(p ->
                    p.getPrecioVenta().compareTo(new BigDecimal("1000.00")) >= 0 &&
                            p.getPrecioVenta().compareTo(new BigDecimal("2000.00")) <= 0));
        }

        @Test
        @DisplayName("Debe combinar múltiples filtros")
        void debeCombinarMultiplesFiltros() {
            // Arrange
            crearProducto("iPhone 15", marcaApple, categoriaSmartphone,
                    new BigDecimal("1500.00"), new BigDecimal("10"), new BigDecimal("3"));
            crearProducto("MacBook Air", marcaApple, categoriaAccesorios,
                    new BigDecimal("2500.00"), new BigDecimal("5"), new BigDecimal("2"));

            entityManager.flush();
            entityManager.clear();

            // Act
            Specification<ProductoEntity> spec = Specification.allOf(
                    ProductoSpecification.porMarca(marcaApple.getId()),
                    ProductoSpecification.porPrecioEntre(
                            new BigDecimal("1000.00"), new BigDecimal("2000.00")));
            List<ProductoEntity> resultados = productoRepository.findAll(spec);

            // Assert
            assertEquals(1, resultados.size());
            assertEquals("iPhone 15", resultados.get(0).getNombre());
        }
    }

    private void crearProducto(String nombre, MarcaEntity marca, CategoriaEntity categoria,
                               BigDecimal precioVenta, BigDecimal stock, BigDecimal stockMinimo) {
        ProductoEntity producto = new ProductoEntity();
        producto.setSku("SKU-" + nombre.replace(" ", "-").toUpperCase());
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setCategoria(categoria);
        producto.setPrecioVenta(precioVenta);
        producto.setPrecioCompra(precioVenta.multiply(new BigDecimal("0.8")));
        producto.setStockActual(stock);
        producto.setStockMinimo(stockMinimo);
        producto.setActivo(true);
        producto.setAplicaIgv(true);
        entityManager.persist(producto);
    }
}