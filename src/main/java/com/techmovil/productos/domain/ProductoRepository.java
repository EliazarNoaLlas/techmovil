package com.techmovil.productos.domain;

import java.util.List;
import java.util.Optional;

/** Puerto del repositorio de productos */
public interface ProductoRepository {
    Optional<Producto> buscarPorId(Long id);
    Optional<Producto> buscarPorSku(String sku);
    Producto guardar(Producto producto);
    List<Producto> listarActivos(Long empresaId);
    List<Producto> buscarConStockCritico(Long empresaId);
}
