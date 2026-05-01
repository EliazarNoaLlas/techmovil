package com.techmovil.ventas.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Item de una venta (producto + cantidad + precio) */
public class VentaItem {
    private Long       productoId;
    private Long       activoId;    // IMEI si es rastreable
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoItem;
    private BigDecimal igvPorcentaje;

    public BigDecimal getSubtotal() {
        BigDecimal bruto = precioUnitario.multiply(cantidad);
        BigDecimal desc  = descuentoItem != null ? descuentoItem : BigDecimal.ZERO;
        return bruto.subtract(desc).setScale(2, RoundingMode.HALF_UP);
    }
}
