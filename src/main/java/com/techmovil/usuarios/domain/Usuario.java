package com.techmovil.usuarios.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Entidad de dominio Usuario.
 * El dominio NO depende de JPA ni de Spring (Clean Architecture).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long   id;
    private String username;
    private String passwordHash;
    private String nombres;
    private String apellidos;
    private String email;
    private boolean activo;
    private int intentosFallidos;
    private boolean bloqueado;
    private Set<String> roles;

    /** RF-01: regla de negocio - bloquear tras 3 intentos fallidos */
    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3) {
            this.bloqueado = true;
        }
    }

    public void resetearIntentos() {
        this.intentosFallidos = 0;
        this.bloqueado = false;
    }

    public boolean estaBloqueado() { return bloqueado; }


}
