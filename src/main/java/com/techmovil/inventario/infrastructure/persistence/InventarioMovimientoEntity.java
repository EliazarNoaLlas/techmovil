package com.techmovil.inventario.infrastructure.persistence;

import com.techmovil.usuarios.infrastructure.persistence.EmpresaEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventario_movimientos")
@Data
public class InventarioMovimientoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaEntity empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_origen_id")
    private AlmacenEntity almacenOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_destino_id")
    private AlmacenEntity almacenDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // ENTRADA, SALIDA, TRASLADO

    private String motivo;
    
    @Column(name = "documento_referencia")
    private String documentoReferencia;

    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;

    private String observaciones;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoDetalleEntity> detalles = new ArrayList<>();
}
