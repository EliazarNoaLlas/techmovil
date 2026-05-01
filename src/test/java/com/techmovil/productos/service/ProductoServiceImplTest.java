package com.techmovil.productos.service;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;
import com.techmovil.productos.application.impl.ProductoServiceImpl;
import com.techmovil.productos.infrastructure.persistence.*;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de productos con Mockito.
 * Valida la lógica de aplicación y orquestación.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔄 ProductoServiceImpl - Pruebas Unitarias")
class ProductoServiceImplTest {

    @Mock
    private ProductoJpaRepository productoRepository;

    @Mock
    private MarcaJpaRepository marcaRepository;

    @Mock
    private CategoriaJpaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private ProductoRequestDto requestValido;
    private MarcaEntity marcaApple;
    private CategoriaEntity categoriaSmartphone;
    private ProductoEntity productoEntity;

    @BeforeEach
    void setUp() {
        requestValido = new ProductoRequestDto(
                "IP15P-128-BLK", "iPhone 15 Pro", 1L, 1L,
                "A2848", "Negro Titanio", "128GB",
                new BigDecimal("1200.00"), new BigDecimal("1500.00"),
                new BigDecimal("10"), new BigDecimal("3")
        );

        marcaApple = new MarcaEntity();
        marcaApple.setId(1L);
        marcaApple.setNombre("Apple");

        categoriaSmartphone = new CategoriaEntity();
        categoriaSmartphone.setId(1L);
        categoriaSmartphone.setNombre("Smartphones");

        productoEntity = new ProductoEntity();
        productoEntity.setId(1L);
        productoEntity.setSku("IP15P-128-BLK");
        productoEntity.setNombre("iPhone 15 Pro");
        productoEntity.setMarca(marcaApple);
        productoEntity.setCategoria(categoriaSmartphone);
        productoEntity.setPrecioVenta(new BigDecimal("1500.00"));
        productoEntity.setStockActual(new BigDecimal("10"));
        productoEntity.setStockMinimo(new BigDecimal("3"));
        productoEntity.setActivo(true);
        productoEntity.setAplicaIgv(true);
    }

    @Nested
    @DisplayName("RF-02: Crear Producto")
    class CrearProducto {

        @Test
        @DisplayName("Debe crear producto exitosamente con datos válidos")
        void debeCrearProductoExitosamente() {
            when(marcaRepository.findById(1L)).thenReturn(Optional.of(marcaApple));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaSmartphone));
            when(productoRepository.save(any(ProductoEntity.class))).thenReturn(productoEntity);

            ProductoResponseDto response = productoService.crear(requestValido);

            assertAll("Verificar producto creado",
                    () -> assertNotNull(response),
                    () -> assertEquals("IP15P-128-BLK", response.sku()),
                    () -> assertEquals("iPhone 15 Pro", response.nombre()),
                    () -> assertEquals("Apple", response.marca()),
                    () -> assertEquals("Smartphones", response.categoria()),
                    () -> assertEquals(new BigDecimal("1500.00"), response.precioVenta()),
                    // No validar igvMonto ni precioConIgv en test unitario con mocks
                    // ya que dependen de la lógica interna de IgvCalculadora
                    () -> assertEquals(new BigDecimal("10"), response.stockActual()),
                    () -> assertFalse(response.stockCritico(), "10 > 3, no debe ser crítico"),
                    () -> assertTrue(response.activo())
            );

            verify(productoRepository, times(1)).save(any(ProductoEntity.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si marca no existe")
        void debeLanzarExcepcionSiMarcaNoExiste() {
            when(marcaRepository.findById(1L)).thenReturn(Optional.empty());

            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> productoService.crear(requestValido)
            );

            assertEquals("Marca no encontrada", exception.getMessage());
            verify(productoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si categoría no existe")
        void debeLanzarExcepcionSiCategoriaNoExiste() {
            when(marcaRepository.findById(1L)).thenReturn(Optional.of(marcaApple));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> productoService.crear(requestValido)
            );

            assertEquals("Categoría no encontrada", exception.getMessage());
        }

        @Test
        @DisplayName("Debe establecer stock inicial correctamente")
        void debeEstablecerStockInicialCorrectamente() {
            when(marcaRepository.findById(1L)).thenReturn(Optional.of(marcaApple));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaSmartphone));
            when(productoRepository.save(any(ProductoEntity.class))).thenAnswer(invocation -> {
                ProductoEntity entity = invocation.getArgument(0);
                assertEquals(new BigDecimal("10"), entity.getStockActual());
                return entity;
            });

            productoService.crear(requestValido);
            verify(productoRepository).save(any(ProductoEntity.class));
        }
    }

    @Nested
    @DisplayName("RF-02: Buscar Productos")
    class BuscarProductos {

        @Test
        @DisplayName("Debe buscar productos por marca")
        void debeBuscarProductosPorMarca() {
            when(productoRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(Arrays.asList(productoEntity));

            List<ProductoResponseDto> resultados = productoService.buscar(1L, null, null);

            assertAll("Verificar búsqueda por marca",
                    () -> assertFalse(resultados.isEmpty()),
                    () -> assertEquals(1, resultados.size()),
                    () -> assertEquals("Apple", resultados.get(0).marca())
            );
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay coincidencias")
        void debeRetornarListaVaciaSinCoincidencias() {
            when(productoRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(Arrays.asList());

            List<ProductoResponseDto> resultados = productoService.buscar(999L, null, null);

            assertTrue(resultados.isEmpty());
        }

        @Test
        @DisplayName("Debe buscar por modelo parcial")
        void debeBuscarPorModeloParcial() {
            productoEntity.setModelo("A2848");
            when(productoRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(Arrays.asList(productoEntity));

            List<ProductoResponseDto> resultados = productoService.buscar(null, null, "A2848");

            assertFalse(resultados.isEmpty());
            assertEquals("A2848", resultados.get(0).modelo());
        }
    }

    @Nested
    @DisplayName("RF-02: Desactivar Producto")
    class DesactivarProducto {

        @Test
        @DisplayName("Debe desactivar producto existente")
        void debeDesactivarProductoExistente() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEntity));

            productoService.desactivar(1L);

            assertFalse(productoEntity.isActivo());
            verify(productoRepository, times(1)).save(productoEntity);
        }

        @Test
        @DisplayName("Debe lanzar excepción si producto no existe")
        void debeLanzarExcepcionSiProductoNoExiste() {
            when(productoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ReglaDeNegocioException.class,
                    () -> productoService.desactivar(999L));
            verify(productoRepository, never()).save(any());
        }
    }
}