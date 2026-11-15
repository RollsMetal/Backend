package com.rollmetal.backend.database.repository;

import com.rollmetal.backend.database.model.SensorLeitura;
import com.rollmetal.backend.enums.TipoMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface SensorLeituraRepository extends JpaRepository<SensorLeitura, Long> {
    @Query(value = "select leitura from SensorLeitura leitura "
            + "join fetch leitura.sensor sensor join fetch sensor.esteira esteira "
            + "where esteira.idEsteira = :idEsteira "
            + "and (:sensorId is null or sensor.idSensor = :sensorId) "
            + "and (:porta is null or sensor.porta = :porta) "
            + "and (:tipoMaterial is null or leitura.tipoMaterial = :tipoMaterial) "
            + "and (:dataInicio is null or leitura.data >= :dataInicio) "
            + "and (:dataFim is null or leitura.data <= :dataFim) "
            + "order by leitura.data desc, leitura.idLeitura desc",
            countQuery = "select count(leitura) from SensorLeitura leitura "
                    + "join leitura.sensor sensor where sensor.esteira.idEsteira = :idEsteira "
                    + "and (:sensorId is null or sensor.idSensor = :sensorId) "
                    + "and (:porta is null or sensor.porta = :porta) "
                    + "and (:tipoMaterial is null or leitura.tipoMaterial = :tipoMaterial) "
                    + "and (:dataInicio is null or leitura.data >= :dataInicio) "
                    + "and (:dataFim is null or leitura.data <= :dataFim)")
    Page<SensorLeitura> buscarHistoricoDaEsteira(
            @Param("idEsteira") Long idEsteira,
            @Param("sensorId") Long sensorId,
            @Param("porta") Short porta,
            @Param("tipoMaterial") TipoMaterial tipoMaterial,
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim,
            Pageable pageable);
}
