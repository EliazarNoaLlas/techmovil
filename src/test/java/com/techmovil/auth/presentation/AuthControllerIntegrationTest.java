package com.techmovil.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.usuarios.infrastructure.persistence.RolEntity;
import com.techmovil.usuarios.infrastructure.persistence.RolJpaRepository;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioEntity;
import com.techmovil.usuarios.infrastructure.persistence.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioJpaRepository usuarioRepository;
    @Autowired private RolJpaRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        RolEntity adminRole = new RolEntity();
        adminRole.setNombre("ADMINISTRADOR");
        adminRole = rolRepository.save(adminRole);

        UsuarioEntity user = new UsuarioEntity();
        user.setUsername("admin");
        user.setPasswordHash(passwordEncoder.encode("admin123"));
        user.setRoles(Set.of(adminRole));
        user.setActivo(true);
        usuarioRepository.save(user);
    }

    @Test
    void debeLoguearYRetornarToken() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void debeRetornar401ConCredencialesInvalidas() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
