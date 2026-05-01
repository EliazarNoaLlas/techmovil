package com.techmovil.shared.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaServicio {

    private final AuditoriaJpaRepository repository;

    @Async
    public void registrar(String usuario, String accion, String modulo, String detalle) {
        AuditoriaEntity entity = new AuditoriaEntity();
        entity.setUsuario(usuario);
        entity.setAccion(accion);
        entity.setModulo(modulo);
        entity.setDetalle(detalle);
        entity.setFechaHora(LocalDateTime.now());
        repository.save(entity);
    }
}
