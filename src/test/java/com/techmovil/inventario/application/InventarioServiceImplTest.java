package com.techmovil.inventario.application;

import com.techmovil.inventario.application.dto.MovimientoRequestDto;
import com.techmovil.inventario.application.dto.MovimientoResponseDto;
import com.techmovil.inventario.application.impl.InventarioServiceImpl;
import com.techmovil.inventario.infrastructure.persistence.*;
import com.techmovil.productos.infrastructure.persistence.ProductoEntity;
import com.techmovil.productos.infrastructure.persistence.ProductoJpaRepository;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock private InventarioMovimientoJpaRepository movimientoRepository;
    @Mock private AlmacenJpaRepository almacenRepository;
    @Mock private ProductoJpaRepository productoRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private StockAlmacenJpaRepository stockRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void debeRegistrarEntradaDeStock() {
        // Arrange
        MovimientoRequestDto.DetalleMovimientoDto detalle = new MovimientoRequestDto.DetalleMovimientoDto(1L, new BigDecimal("10"), new BigDecimal("500"));
        MovimientoRequestDto request = new MovimientoRequestDto("ENTRADA", "COMPRA", "FAC-001", "", null, 1L, 1L, List.of(detalle));

        UsuarioEntity usuario = new UsuarioEntity(); usuario.setId(1L); usuario.setUsername("admin");
        AlmacenEntity almacen = new AlmacenEntity(); almacen.setId(1L); almacen.setNombre("Almacen 1");
        ProductoEntity producto = new ProductoEntity(); producto.setId(1L); producto.setStockActual(BigDecimal.ZERO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(stockRepository.findByAlmacenIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(movimientoRepository.save(any())).thenAnswer(i -> {
            InventarioMovimientoEntity m = (InventarioMovimientoEntity) i.getArguments()[0];
            m.setId(1L);
            return m;
        });

        // Act
        MovimientoResponseDto response = inventarioService.ejecutar(request);

        // Assert
        assertNotNull(response);
        assertEquals("ENTRADA", response.tipoMovimiento());
        verify(stockRepository, times(1)).save(any());
        verify(productoRepository, times(1)).save(any());
    }
}
