package com.techmovil.productos.application.impl;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;
import com.techmovil.productos.application.usecase.CrearProductoUseCase;
import com.techmovil.productos.domain.Producto;
import com.techmovil.productos.infrastructure.persistence.*;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.shared.util.IgvCalculadora;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements 
        CrearProductoUseCase, 
        com.techmovil.productos.application.usecase.BuscarProductosUseCase,
        com.techmovil.productos.application.usecase.ActualizarProductoUseCase,
        com.techmovil.productos.application.usecase.DesactivarProductoUseCase,
        com.techmovil.productos.application.usecase.ObtenerProductoUseCase {

    private static final String PRODUCTO_NO_ENCONTRADO = "Producto no encontrado";
    private static final String MARCA_NO_ENCONTRADA = "Marca no encontrada";
    private static final String CATEGORIA_NO_ENCONTRADA = "Categoría no encontrada";

    private final ProductoJpaRepository productoRepository;
    private final MarcaJpaRepository marcaRepository;
    private final CategoriaJpaRepository categoriaRepository;

    @Override
    public ProductoResponseDto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ReglaDeNegocioException(PRODUCTO_NO_ENCONTRADO));
    }

    @Override
    public List<ProductoResponseDto> buscar(Long marcaId, Long categoriaId, String modelo) {
        Specification<ProductoEntity> spec = Specification.allOf(
                ProductoSpecification.porMarca(marcaId),
                ProductoSpecification.porCategoria(categoriaId),
                ProductoSpecification.porModelo(modelo)
        );
        
        return productoRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDto actualizar(Long id, ProductoRequestDto dto) {
        ProductoEntity entity = productoRepository.findById(id)
                .orElseThrow(() -> new ReglaDeNegocioException(PRODUCTO_NO_ENCONTRADO));
        
        actualizarCamposEntity(entity, dto);

        ProductoEntity guardado = productoRepository.save(entity);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        ProductoEntity entity = productoRepository.findById(id)
                .orElseThrow(() -> new ReglaDeNegocioException(PRODUCTO_NO_ENCONTRADO));
        entity.setActivo(false);
        productoRepository.save(entity);
    }

    @Override
    @Transactional
    public ProductoResponseDto crear(ProductoRequestDto dto) {
        ProductoEntity entity = new ProductoEntity();
        actualizarCamposEntity(entity, dto);
        entity.setStockActual(dto.stockInicial());
        entity.setActivo(true);
        entity.setAplicaIgv(true);

        ProductoEntity guardado = productoRepository.save(entity);
        return mapToResponse(guardado);
    }

    private void actualizarCamposEntity(ProductoEntity entity, ProductoRequestDto dto) {
        MarcaEntity marca = marcaRepository.findById(dto.marcaId())
                .orElseThrow(() -> new ReglaDeNegocioException(MARCA_NO_ENCONTRADA));
        CategoriaEntity categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ReglaDeNegocioException(CATEGORIA_NO_ENCONTRADA));

        entity.setSku(dto.sku());
        entity.setNombre(dto.nombre());
        entity.setMarca(marca);
        entity.setCategoria(categoria);
        entity.setModelo(dto.modelo());
        entity.setColor(dto.color());
        entity.setCapacidad(dto.capacidad());
        entity.setPrecioCompra(dto.precioCompra());
        entity.setPrecioVenta(dto.precioVenta());
        entity.setStockMinimo(dto.stockMinimo());
    }

    private ProductoResponseDto mapToResponse(ProductoEntity entity) {
        Producto domain = mapToDomain(entity);
        return new ProductoResponseDto(
                entity.getId(),
                entity.getSku(),
                entity.getNombre(),
                entity.getMarca() != null ? entity.getMarca().getNombre() : null,
                entity.getCategoria() != null ? entity.getCategoria().getNombre() : null,
                entity.getModelo(),
                entity.getColor(),
                entity.getCapacidad(),
                entity.getPrecioVenta(),
                IgvCalculadora.calcularMontoIgv(entity.getPrecioVenta(), true),
                IgvCalculadora.calcularMontoTotalConIgv(entity.getPrecioVenta(), true),
                entity.getStockActual(),
                entity.getStockMinimo(),
                domain.tieneStockCritico(),
                entity.isActivo()
        );
    }

    private Producto mapToDomain(ProductoEntity entity) {
        return Producto.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .nombre(entity.getNombre())
                .precioVenta(entity.getPrecioVenta())
                .stockActual(entity.getStockActual())
                .stockMinimo(entity.getStockMinimo())
                .activo(entity.isActivo())
                .build();
    }
}
