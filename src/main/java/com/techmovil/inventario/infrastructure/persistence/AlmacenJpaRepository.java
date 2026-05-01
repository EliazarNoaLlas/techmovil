package com.techmovil.inventario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenJpaRepository extends JpaRepository<AlmacenEntity, Long> {
}
