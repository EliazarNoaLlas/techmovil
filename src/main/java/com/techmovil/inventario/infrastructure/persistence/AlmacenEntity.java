package com.techmovil.inventario.infrastructure.persistence;

import com.techmovil.usuarios.infrastructure.persistence.EmpresaEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "almacenes")
@Data
public class AlmacenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaEntity empresa;

    private String nombre;
    private String direccion;
    
    @Column(name = "es_principal")
    private boolean esPrincipal;

    private boolean activo;
}
