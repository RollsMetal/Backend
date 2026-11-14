package com.rollmetal.backend.database.model;

import com.rollmetal.backend.enums.TipoMaterial;
import com.rollmetal.backend.enums.TipoSensor;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "sensor_leituras", indexes = {
        @Index(name = "idx_sensor_leituras_sensor_data", columnList = "sensor_id, data")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idLeitura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorEntity sensor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false)
    private TipoMaterial tipoMaterial;

    @Column(name = "valor", nullable = false, length = 64)
    private String valorLeitura;

    @Column(name = "modelo_recebido", length = 50)
    private String modeloRecebido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sensor_recebido")
    private TipoSensor tipoSensorRecebido;

    @Column(name = "data", nullable = false)
    private OffsetDateTime data;

    @PrePersist
    public void registrarData() {
        if (data == null) {
            data = OffsetDateTime.now(java.time.ZoneOffset.UTC);
        }
    }
}
