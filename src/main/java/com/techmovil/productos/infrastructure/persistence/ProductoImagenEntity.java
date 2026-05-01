package com.techmovil.productos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "producto_imagenes")
@Getter
@Setter
public class ProductoImagenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    @Column(name = "empresa_id")
    private Long empresaId;

    private String url;
    
    @Column(name = "url_miniatura")
    private String urlMiniatura;

    @Column(name = "texto_alt")
    private String textoAlt;

    private String tipo;
    private int orden;
    
    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "tamano_bytes")
    private Integer tamanoBytes;

    @Column(name = "ancho_px")
    private Integer anchoPx;

    @Column(name = "alto_px")
    private Integer altoPx;

    private String formato;
    private boolean activo;
}
