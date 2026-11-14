package com.rollmetal.backend.database.repository;

import com.rollmetal.backend.database.model.SensorLeitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorLeituraRepository extends JpaRepository<SensorLeitura, Long> {
    @Query(value = "select leitura from SensorLeitura leitura "
            + "join fetch leitura.sensor sensor join fetch sensor.esteira esteira "
            + "where esteira.idEsteira = :idEsteira order by leitura.data desc",
            countQuery = "select count(leitura) from SensorLeitura leitura "
                    + "join leitura.sensor sensor where sensor.esteira.idEsteira = :idEsteira")
    Page<SensorLeitura> buscarHistoricoDaEsteira(@Param("idEsteira") Long idEsteira, Pageable pageable);
}
