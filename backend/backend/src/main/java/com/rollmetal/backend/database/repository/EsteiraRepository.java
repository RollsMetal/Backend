package com.rollmetal.backend.database.repository;

import com.rollmetal.backend.database.model.EsteiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EsteiraRepository extends JpaRepository<EsteiraEntity, Long> {
    Optional<EsteiraEntity> findByNumeroSerial(String numeroSerial);

    boolean existsByNumeroSerial(String numeroSerial);
}
