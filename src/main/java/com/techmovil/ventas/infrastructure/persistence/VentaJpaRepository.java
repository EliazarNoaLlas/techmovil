package com.techmovil.ventas.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaJpaRepository extends JpaRepository<VentaEntity, Long> {
    List<VentaEntity> findAllByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
}
