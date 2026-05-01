package com.techmovil.ventas.application.impl;

import com.techmovil.inventario.infrastructure.persistence.*;
import com.techmovil.productos.infrastructure.persistence.ProductoEntity;
import com.techmovil.productos.infrastructure.persistence.ProductoJpaRepository;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.shared.util.IgvCalculadora;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioJpaRepository;
import com.techmovil.ventas.application.dto.VentaRequestDto;
import com.techmovil.ventas.application.dto.VentaResponseDto;
import com.techmovil.ventas.application.usecase.RegistrarVentaUseCase;
import com.techmovil.ventas.infrastructure.persistence.VentaDetalleEntity;
import com.techmovil.ventas.infrastructure.persistence.VentaEntity;
import com.techmovil.ventas.infrastructure.persistence.VentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements RegistrarVentaUseCase {

    private final VentaJpaRepository ventaRepository;
    private final ProductoJpaRepository productoRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final InventarioMovimientoJpaRepository movimientoRepository;
    private final AlmacenJpaRepository almacenRepository;
    private final StockAlmacenJpaRepository stockRepository;
    private final com.techmovil.shared.audit.AuditoriaServicio auditoriaServicio;

    @Override
    @Transactional
    public VentaResponseDto ejecutar(VentaRequestDto dto) {
        UsuarioEntity usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ReglaDeNegocioException("Usuario no encontrado"));

        VentaEntity venta = new VentaEntity();
        venta.setClienteNombre(dto.clienteNombre());
        venta.setClienteDocumento(dto.clienteDocumento());
        venta.setUsuario(usuario);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setTipoComprobante(dto.tipoComprobante());
        venta.setSerieComprobante("V001");
        venta.setNumeroComprobante(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        BigDecimal totalVenta = BigDecimal.ZERO;
        BigDecimal totalIgv = BigDecimal.ZERO;
        BigDecimal totalSubtotal = BigDecimal.ZERO;

        for (VentaRequestDto.DetalleVentaDto d : dto.detalles()) {
            ProductoEntity producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new ReglaDeNegocioException("Producto no encontrado: " + d.productoId()));

            // Validar stock (RF-07)
            if (producto.getStockActual().compareTo(d.cantidad()) < 0) {
                throw new ReglaDeNegocioException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            VentaDetalleEntity detalle = new VentaDetalleEntity();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(d.cantidad());
            detalle.setPrecioUnitario(d.precioUnitario());

            // Cálculos
            BigDecimal subtotalItem = d.cantidad().multiply(d.precioUnitario());
            BigDecimal igvItem = IgvCalculadora.calcularIgv(subtotalItem);
            BigDecimal totalItem = subtotalItem.add(igvItem);

            detalle.setSubtotal(subtotalItem);
            detalle.setIgv(igvItem);
            detalle.setTotal(totalItem);

            venta.getDetalles().add(detalle);

            totalSubtotal = totalSubtotal.add(subtotalItem);
            totalIgv = totalIgv.add(igvItem);
            totalVenta = totalVenta.add(totalItem);

            // Descontar stock (RF-07)
            descontarStock(producto, d.cantidad());
        }

        venta.setMontoSubtotal(totalSubtotal);
        venta.setMontoIgv(totalIgv);
        venta.setMontoTotal(totalVenta);

        VentaEntity guardada = ventaRepository.save(venta);

        // Registrar auditoría
        auditoriaServicio.registrar(usuario.getUsername(), "REGISTRO_VENTA", "VENTAS", 
                "Venta registrada: " + guardada.getNumeroComprobante() + " por monto " + guardada.getMontoTotal());

        // Registrar movimiento de inventario (RF-12)
        registrarMovimientoSalida(guardada, usuario);

        return mapToResponse(guardada);
    }

    private void descontarStock(ProductoEntity producto, BigDecimal cantidad) {
        producto.setStockActual(producto.getStockActual().subtract(cantidad));
        productoRepository.save(producto);
        
        // También actualizar stock por almacén (suponiendo almacén principal por defecto)
        AlmacenEntity principal = almacenRepository.findAll().stream()
                .filter(AlmacenEntity::isEsPrincipal)
                .findFirst()
                .orElse(null);
                
        if (principal != null) {
            StockAlmacenEntity stock = stockRepository.findByAlmacenIdAndProductoId(principal.getId(), producto.getId())
                    .orElseThrow(() -> new ReglaDeNegocioException("No se encontró stock en almacén principal para descontar"));
            stock.setCantidad(stock.getCantidad().subtract(cantidad));
            stockRepository.save(stock);
        }
    }

    private void registrarMovimientoSalida(VentaEntity venta, UsuarioEntity usuario) {
        InventarioMovimientoEntity movimiento = new InventarioMovimientoEntity();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setMotivo("VENTA " + venta.getTipoComprobante() + " " + venta.getNumeroComprobante());
        movimiento.setDocumentoReferencia(venta.getNumeroComprobante());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setUsuario(usuario);
        
        AlmacenEntity principal = almacenRepository.findAll().stream()
                .filter(AlmacenEntity::isEsPrincipal)
                .findFirst()
                .orElse(null);
        movimiento.setAlmacenOrigen(principal);

        movimiento.setDetalles(venta.getDetalles().stream().map(d -> {
            MovimientoDetalleEntity md = new MovimientoDetalleEntity();
            md.setMovimiento(movimiento);
            md.setProducto(d.getProducto());
            md.setCantidad(d.getCantidad());
            md.setCostoUnitario(d.getProducto().getPrecioCompra());
            return md;
        }).collect(Collectors.toList()));

        movimientoRepository.save(movimiento);
    }

    private VentaResponseDto mapToResponse(VentaEntity v) {
        return new VentaResponseDto(
                v.getId(),
                v.getNumeroComprobante(),
                v.getTipoComprobante(),
                v.getFechaVenta(),
                v.getUsuario().getUsername(),
                v.getClienteNombre(),
                v.getClienteDocumento(),
                v.getDetalles().stream().map(d -> new VentaResponseDto.DetalleVentaResponseDto(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal(),
                        d.getIgv(),
                        d.getTotal()
                )).collect(Collectors.toList()),
                v.getMontoSubtotal(),
                v.getMontoIgv(),
                v.getMontoTotal()
        );
    }
}
