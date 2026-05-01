package com.techmovil.ventas.domain;

import com.techmovil.shared.util.IgvCalculadora;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad de dominio Venta.
 * RF-04: registro de ventas de un solo equipo o multiples accesorios.
 * RF-06: genera comprobante con desglose de IGV.
 */
public class Venta {
    private Long              id;
    private String            numeroComprobante;
    private TipoComprobante   tipoComprobante;
    private Long              usuarioId;
    private Long              clienteId;
    private List<VentaItem>   items;
    private BigDecimal        subtotal;
    private BigDecimal        descuento;
    private BigDecimal        igvTotal;
    private BigDecimal        total;
    private MetodoPago        metodoPago;
    private EstadoVenta       estado;
    private LocalDateTime     fechaVenta;

    public enum TipoComprobante { BOLETA, FACTURA, NOTA_VENTA, TICKET }
    public enum MetodoPago { EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA, YAPE, PLIN, MIXTO }
    public enum EstadoVenta { EMITIDA, ANULADA, PENDIENTE }

    /** RF-06: calcula totales con IGV 18% para todos los items */
    public void calcularTotales() {
        this.subtotal = items.stream()
            .map(VentaItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal base = subtotal.subtract(descuento != null ? descuento : BigDecimal.ZERO);
        this.igvTotal   = IgvCalculadora.calcularIgv(base);
        this.total      = base.add(igvTotal);
    }
}
