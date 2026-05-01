package com.techmovil.inventario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockAlmacenJpaRepository extends JpaRepository<StockAlmacenEntity, Long> {
    Optional<StockAlmacenEntity> findByAlmacenIdAndProductoId(Long almacenId, Long productoId);
}
