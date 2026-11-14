package com.rollmetal.backend.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "esteiras", uniqueConstraints = {
        @UniqueConstraint(name = "uk_esteiras_numero_serial", columnNames = "NumeroSerial")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsteiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idEsteira;

    @Column(name = "NumeroSerial", nullable = false, length = 100)
    private String numeroSerial;
}
