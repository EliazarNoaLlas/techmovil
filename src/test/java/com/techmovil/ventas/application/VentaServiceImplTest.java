package com.techmovil.ventas.application;

import com.techmovil.inventario.infrastructure.persistence.*;
import com.techmovil.productos.infrastructure.persistence.ProductoEntity;
import com.techmovil.productos.infrastructure.persistence.ProductoJpaRepository;
import com.techmovil.shared.audit.AuditoriaServicio;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioJpaRepository;
import com.techmovil.ventas.application.dto.VentaRequestDto;
import com.techmovil.ventas.application.dto.VentaResponseDto;
import com.techmovil.ventas.application.impl.VentaServiceImpl;
import com.techmovil.ventas.infrastructure.persistence.VentaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
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
class VentaServiceImplTest {

    @Mock private VentaJpaRepository ventaRepository;
    @Mock private ProductoJpaRepository productoRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private InventarioMovimientoJpaRepository movimientoRepository;
    @Mock private AlmacenJpaRepository almacenRepository;
    @Mock private StockAlmacenJpaRepository stockRepository;
    @Mock private AuditoriaServicio auditoriaServicio;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private UsuarioEntity mockUsuario;
    private ProductoEntity mockProducto;

    @BeforeEach
    void setUp() {
        mockUsuario = new UsuarioEntity();
        mockUsuario.setId(1L);
        mockUsuario.setUsername("testuser");

        mockProducto = new ProductoEntity();
        mockProducto.setId(1L);
        mockProducto.setNombre("Xiaomi Redmi Note 13");
        mockProducto.setPrecioVenta(new BigDecimal("1000.00"));
        mockProducto.setStockActual(new BigDecimal("10"));
    }

    @Test
    void debeRegistrarVentaExitosamente() {
        // Arrange
        VentaRequestDto.DetalleVentaDto detalle = new VentaRequestDto.DetalleVentaDto(1L, new BigDecimal("2"), new BigDecimal("1000.00"));
        VentaRequestDto request = new VentaRequestDto("Juan Perez", "12345678", 1L, "BOLETA", List.of(detalle));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(mockProducto));
        when(ventaRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Act
        VentaResponseDto response = ventaService.ejecutar(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("2360.00"), response.total()); // (1000 * 2) + 18% IGV
        assertEquals(new BigDecimal("360.00"), response.igvMonto());
        verify(productoRepository, atLeastOnce()).save(any());
        verify(movimientoRepository, times(1)).save(any());
        verify(auditoriaServicio, times(1)).registrar(any(), any(), any(), any());
    }

    @Test
    void debeLanzarExceptionSiNoHayStock() {
        // Arrange
        VentaRequestDto.DetalleVentaDto detalle = new VentaRequestDto.DetalleVentaDto(1L, new BigDecimal("15"), new BigDecimal("1000.00"));
        VentaRequestDto request = new VentaRequestDto("Juan Perez", "12345678", 1L, "BOLETA", List.of(detalle));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(mockProducto));

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> ventaService.ejecutar(request));
        verify(ventaRepository, never()).save(any());
    }
}
