package com.techmovil.productos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    private String sku;
    private String nombre;
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    private MarcaEntity marca;

    private String modelo;
    private String color;
    private String capacidad;

    @Column(name = "precio_compra")
    private BigDecimal precioCompra;

    @Column(name = "precio_venta")
    private BigDecimal precioVenta;

    @Column(name = "precio_oferta")
    private BigDecimal precioOferta;

    @Column(name = "igv_porcentaje")
    private BigDecimal igvPorcentaje;

    @Column(name = "aplica_igv")
    private boolean aplicaIgv;

    @Column(name = "stock_actual")
    private BigDecimal stockActual;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo")
    private BigDecimal stockMaximo;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "es_rastreable")
    private boolean esRastreable;

    @Column(name = "requiere_serie")
    private boolean requiereSerie;

    @Column(name = "permite_financiamiento")
    private boolean permiteFinanciamiento;

    @Column(name = "meses_garantia")
    private Integer mesesGarantia;

    private boolean activo;
}
