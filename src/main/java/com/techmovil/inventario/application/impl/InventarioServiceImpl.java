package com.techmovil.inventario.application.impl;

import com.techmovil.inventario.application.dto.MovimientoRequestDto;
import com.techmovil.inventario.application.dto.MovimientoResponseDto;
import com.techmovil.inventario.application.usecase.RegistrarMovimientoUseCase;
import com.techmovil.inventario.infrastructure.persistence.*;
import com.techmovil.productos.infrastructure.persistence.ProductoEntity;
import com.techmovil.productos.infrastructure.persistence.ProductoJpaRepository;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements RegistrarMovimientoUseCase {

    private final InventarioMovimientoJpaRepository movimientoRepository;
    private final AlmacenJpaRepository almacenRepository;
    private final ProductoJpaRepository productoRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final StockAlmacenJpaRepository stockRepository;

    @Override
    @Transactional
    public MovimientoResponseDto ejecutar(MovimientoRequestDto dto) {
        UsuarioEntity usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ReglaDeNegocioException("Usuario no encontrado"));

        InventarioMovimientoEntity movimiento = new InventarioMovimientoEntity();
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setMotivo(dto.motivo());
        movimiento.setDocumentoReferencia(dto.documentoReferencia());
        movimiento.setObservaciones(dto.observaciones());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        if (dto.almacenOrigenId() != null) {
            movimiento.setAlmacenOrigen(almacenRepository.findById(dto.almacenOrigenId())
                    .orElseThrow(() -> new ReglaDeNegocioException("Almacén origen no encontrado")));
        }

        if (dto.almacenDestinoId() != null) {
            movimiento.setAlmacenDestino(almacenRepository.findById(dto.almacenDestinoId())
                    .orElseThrow(() -> new ReglaDeNegocioException("Almacén destino no encontrado")));
        }

        movimiento.setDetalles(dto.detalles().stream().map(d -> {
            ProductoEntity producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new ReglaDeNegocioException("Producto no encontrado: " + d.productoId()));

            MovimientoDetalleEntity detalle = new MovimientoDetalleEntity();
            detalle.setMovimiento(movimiento);
            detalle.setProducto(producto);
            detalle.setCantidad(d.cantidad());
            detalle.setCostoUnitario(d.costoUnitario());

            actualizarStock(dto.tipoMovimiento(), movimiento.getAlmacenOrigen(), movimiento.getAlmacenDestino(), producto, d.cantidad());

            return detalle;
        }).collect(Collectors.toList()));

        InventarioMovimientoEntity guardado = movimientoRepository.save(movimiento);

        return new MovimientoResponseDto(
                guardado.getId(),
                guardado.getTipoMovimiento(),
                guardado.getMotivo(),
                guardado.getDocumentoReferencia(),
                guardado.getFechaMovimiento(),
                guardado.getAlmacenOrigen() != null ? guardado.getAlmacenOrigen().getNombre() : null,
                guardado.getAlmacenDestino() != null ? guardado.getAlmacenDestino().getNombre() : null,
                guardado.getUsuario().getUsername(),
                guardado.getDetalles().stream().map(d -> new MovimientoResponseDto.DetalleMovimientoResponseDto(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getCostoUnitario()
                )).collect(Collectors.toList())
        );
    }

    private void actualizarStock(String tipo, AlmacenEntity origen, AlmacenEntity destino, ProductoEntity producto, BigDecimal cantidad) {
        if ("ENTRADA".equals(tipo)) {
            modificarStock(destino, producto, cantidad);
        } else if ("SALIDA".equals(tipo)) {
            modificarStock(origen, producto, cantidad.negate());
        } else if ("TRASLADO".equals(tipo)) {
            modificarStock(origen, producto, cantidad.negate());
            modificarStock(destino, producto, cantidad);
        }
        
        // Actualizar stock total en la entidad producto para consultas rápidas
        BigDecimal stockActual = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;
        if ("ENTRADA".equals(tipo)) {
            producto.setStockActual(stockActual.add(cantidad));
        } else if ("SALIDA".equals(tipo)) {
            producto.setStockActual(stockActual.subtract(cantidad));
        }
        productoRepository.save(producto);
    }

    private void modificarStock(AlmacenEntity almacen, ProductoEntity producto, BigDecimal cantidad) {
        if (almacen == null) throw new ReglaDeNegocioException("Almacén requerido para este movimiento");
        
        StockAlmacenEntity stock = stockRepository.findByAlmacenIdAndProductoId(almacen.getId(), producto.getId())
                .orElseGet(() -> {
                    StockAlmacenEntity s = new StockAlmacenEntity();
                    s.setAlmacen(almacen);
                    s.setProducto(producto);
                    s.setCantidad(BigDecimal.ZERO);
                    return s;
                });

        stock.setCantidad(stock.getCantidad().add(cantidad));
        
        if (stock.getCantidad().compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaDeNegocioException("Stock insuficiente en el almacén " + almacen.getNombre() + " para el producto " + producto.getNombre());
        }
        
        stockRepository.save(stock);
    }
}
