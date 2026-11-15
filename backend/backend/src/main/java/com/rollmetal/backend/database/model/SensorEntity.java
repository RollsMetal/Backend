
package com.rollmetal.backend.database.model;

import com.rollmetal.backend.enums.TipoSensor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "sensores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sensores_esteira_porta", columnNames = {"esteira_id", "porta"})
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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

    public void atualizarIdentificacao(String modelo, TipoSensor tipoSensor) {
        this.modelo = modelo;
        this.tipoSensor = tipoSensor;
    }
}
