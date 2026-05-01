package com.techmovil.productos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categorias")
@Data
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id")
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoriaEntity parent;

    private int nivel;
    
    @Column(name = "ruta_completa")
    private String rutaCompleta;

    private String codigo;
    private String nombre;
    private String descripcion;
    private String icono;
    private String color;
    private int orden;
    private boolean activo;
}
