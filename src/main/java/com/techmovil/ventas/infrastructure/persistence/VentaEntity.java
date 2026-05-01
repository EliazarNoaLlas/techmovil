package com.techmovil.ventas.infrastructure.persistence;

import com.techmovil.usuarios.infrastructure.persistence.EmpresaEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class VentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaEntity empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_documento")
    private String clienteDocumento;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Column(name = "monto_subtotal")
    private BigDecimal montoSubtotal;

    @Column(name = "monto_igv")
    private BigDecimal montoIgv;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "tipo_comprobante")
    private String tipoComprobante; // BOLETA, FACTURA

    @Column(name = "serie_comprobante")
    private String serieComprobante;

    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalleEntity> detalles = new ArrayList<>();
}
