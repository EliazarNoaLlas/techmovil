package com.techmovil.usuarios.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "empresas")
@Data
public class EmpresaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruc;
    private String nombre;
    private String direccion;
    
    @Column(name = "razon_social")
    private String razonSocial;
    
    @Column(name = "igv_porcentaje")
    private java.math.BigDecimal igvPorcentaje;
}
