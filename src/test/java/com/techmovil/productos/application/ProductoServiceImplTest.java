package com.techmovil.productos.application;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;
import com.techmovil.productos.application.impl.ProductoServiceImpl;
import com.techmovil.productos.infrastructure.persistence.*;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock private ProductoJpaRepository productoRepository;
    @Mock private MarcaJpaRepository marcaRepository;
    @Mock private CategoriaJpaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void debeCrearProductoCorrectamente() {
        // Arrange
        ProductoRequestDto request = new ProductoRequestDto(
                "SKU123", "Xiaomi Redmi 13", 1L, 1L, "Note 13", "Negro", "128GB",
                new BigDecimal("800"), new BigDecimal("1000"), new BigDecimal("0"), new BigDecimal("10")
        );

        MarcaEntity marca = new MarcaEntity(); marca.setId(1L); marca.setNombre("Xiaomi");
        CategoriaEntity cat = new CategoriaEntity(); cat.setId(1L); cat.setNombre("Celulares");

        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(productoRepository.save(any())).thenAnswer(i -> {
            ProductoEntity p = (ProductoEntity) i.getArguments()[0];
            p.setId(100L);
            return p;
        });

        // Act
        ProductoResponseDto response = productoService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Xiaomi Redmi 13", response.nombre());
        verify(productoRepository, times(1)).save(any());
    }

    @Test
    void debeLanzarExceptionSiMarcaNoExiste() {
        ProductoRequestDto request = new ProductoRequestDto(
                "SKU123", "Xiaomi Redmi 13", 99L, 1L, "Note 13", "Negro", "128GB",
                new BigDecimal("800"), new BigDecimal("1000"), new BigDecimal("0"), new BigDecimal("10")
        );

        when(marcaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReglaDeNegocioException.class, () -> productoService.crear(request));
    }
}
