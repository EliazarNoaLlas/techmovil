package com.techmovil.productos.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad de dominio Producto (celular o accesorio).
 * RF-02, RF-03: gestion de equipos con marca, modelo, precio y stock.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Long       id;
    private String     sku;
    private String     nombre;
    private String     modelo;
    private String     color;
    private String     capacidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal igvPorcentaje;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private boolean    esRastreable;
    private boolean    activo;

    /** RF-09: verifica si el stock esta en nivel critico */
    public boolean tieneStockCritico() {
        if (this.stockActual == null || this.stockMinimo == null) return false;
        return this.stockActual.compareTo(this.stockMinimo) <= 0;
    }

    /** RF-07: descuenta stock al confirmar una venta */
    public void descontarStock(BigDecimal cantidad) {
        if (this.stockActual == null) return;
        if (cantidad.compareTo(this.stockActual) > 0) {
            throw new com.techmovil.shared.exception.StockInsuficienteException(
                this.nombre, this.stockActual.doubleValue()
            );
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    /** RF-08: agrega stock al registrar ingreso de mercaderia */
    public void agregarStock(BigDecimal cantidad) {
        if (this.stockActual == null) this.stockActual = BigDecimal.ZERO;
        this.stockActual = this.stockActual.add(cantidad);
    }
}
