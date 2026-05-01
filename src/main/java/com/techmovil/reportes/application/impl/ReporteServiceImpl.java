package com.techmovil.reportes.application.impl;

import com.techmovil.productos.application.dto.ProductoResponseDto;
import com.techmovil.productos.infrastructure.persistence.ProductoJpaRepository;
import com.techmovil.productos.infrastructure.persistence.ProductoSpecification;
import com.techmovil.reportes.application.dto.ReporteVentasDto;
import com.techmovil.reportes.application.usecase.GenerarReporteUseCase;
import com.techmovil.shared.util.IgvCalculadora;
import com.techmovil.ventas.application.dto.VentaResponseDto;
import com.techmovil.ventas.infrastructure.persistence.VentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements GenerarReporteUseCase {

    private final VentaJpaRepository ventaRepository;
    private final ProductoJpaRepository productoRepository;

    @Override
    public ReporteVentasDto generarVentas(LocalDate inicio, LocalDate fin) {
        List<com.techmovil.ventas.infrastructure.persistence.VentaEntity> ventas = 
                ventaRepository.findAllByFechaVentaBetween(inicio.atStartOfDay(), fin.atTime(LocalTime.MAX));

        BigDecimal totalVentas = ventas.stream().map(v -> v.getMontoTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIgv = ventas.stream().map(v -> v.getMontoIgv()).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VentaResponseDto> dtoList = ventas.stream().map(v -> new VentaResponseDto(
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
        )).collect(Collectors.toList());

        return new ReporteVentasDto(totalVentas, totalIgv, (long) ventas.size(), dtoList);
    }

    @Override
    public List<ProductoResponseDto> generarStockCritico() {
        return productoRepository.findAll(ProductoSpecification.soloCriticos())
                .stream().map(p -> new ProductoResponseDto(
                        p.getId(),
                        p.getSku(),
                        p.getNombre(),
                        p.getMarca() != null ? p.getMarca().getNombre() : null,
                        p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                        p.getModelo(),
                        p.getColor(),
                        p.getCapacidad(),
                        p.getPrecioVenta(),
                        IgvCalculadora.calcularMontoIgv(p.getPrecioVenta(), true),
                        IgvCalculadora.calcularMontoTotalConIgv(p.getPrecioVenta(), true),
                        p.getStockActual(),
                        p.getStockMinimo(),
                        true,
                        p.isActivo()
                )).collect(Collectors.toList());
    }
}
