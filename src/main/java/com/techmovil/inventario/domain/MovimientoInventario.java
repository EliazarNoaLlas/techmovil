package com.techmovil.inventario.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para movimientos de inventario.
 * RF-07: actualizacion automatica de stock.
 * RF-08: registro de entradas de mercaderia.
 * RF-12: auditoria de cambios con fecha, hora y usuario.
 */
public class MovimientoInventario {
    private Long              id;
    private Long              productoId;
    private TipoMovimiento    tipo;
    private BigDecimal        cantidad;
    private BigDecimal        stockAnterior;
    private BigDecimal        stockNuevo;
    private String            motivo;
    private String            referencia;
    private Long              usuarioId;
    private LocalDateTime     fechaMovimiento;

    public enum TipoMovimiento {
        INGRESO, VENTA, AJUSTE, DEVOLUCION, MERMA, TRASLADO
    }
}
