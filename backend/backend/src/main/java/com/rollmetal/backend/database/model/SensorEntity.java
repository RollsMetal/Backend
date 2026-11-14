
package com.rollmetal.backend.database.model;

import com.rollmetal.backend.enums.TipoSensor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "sensores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sensores_esteira_porta", columnNames = {"esteira_id", "porta"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Sensor")
    private Long idSensor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "esteira_id", nullable = false)
    private EsteiraEntity esteira;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 100)
    private TipoSensor tipoSensor;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Column(name = "porta", nullable = false)
    private Short porta;

}
