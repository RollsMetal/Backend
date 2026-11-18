package com.rollmetal.backend.database.repository;

import com.rollmetal.backend.database.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByEmail(String email);
}
