package com.rollmetal.backend.database.model;

import com.rollmetal.backend.enums.TipoMaterial;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "sensor_leituras")
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

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorEntity sensor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false)
    @Builder.Default
    private TipoMaterial tipoMaterial = TipoMaterial.SEM_MATERIAL;

    @Column(name = "valor", nullable = false, length = 64)
    private String valorLeitura;

    @Column(name = "data", nullable = false)
    private OffsetDateTime data;

    @PrePersist
    public void registrarData() {
        if (data == null) {
            data = OffsetDateTime.now();
        }
    }
}
