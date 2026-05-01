package com.techmovil.inventario.infrastructure.persistence;

import com.techmovil.productos.infrastructure.persistence.ProductoEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "movimiento_detalles")
@Data
public class MovimientoDetalleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id")
    private InventarioMovimientoEntity movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    private BigDecimal cantidad;
    
    @Column(name = "costo_unitario")
    private BigDecimal costoUnitario;
}
