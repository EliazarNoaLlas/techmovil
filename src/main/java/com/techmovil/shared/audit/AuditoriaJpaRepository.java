package com.techmovil.shared.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, Long> {
}
