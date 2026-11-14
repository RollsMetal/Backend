package com.rollmetal.backend.database.repository;

import com.rollmetal.backend.database.model.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<SensorEntity, Long> {
    Optional<SensorEntity> findByEsteira_IdEsteiraAndPorta(Long idEsteira, Short porta);

    List<SensorEntity> findByEsteira_IdEsteiraOrderByPorta(Long idEsteira);
}
