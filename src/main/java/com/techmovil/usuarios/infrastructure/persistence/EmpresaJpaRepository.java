package com.techmovil.usuarios.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaJpaRepository extends JpaRepository<EmpresaEntity, Long> {
}
