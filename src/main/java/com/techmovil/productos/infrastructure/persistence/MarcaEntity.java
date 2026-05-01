package com.techmovil.productos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marcas")
@Data
public class MarcaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id")
    private Long empresaId;

    private String nombre;
    
    @Column(name = "pais_origen")
    private String paisOrigen;

    private String descripcion;
    
    @Column(name = "logo_url")
    private String logoUrl;

    private boolean activo;
}
