package com.rollmetal.backend.database.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esteiras", uniqueConstraints = {
        @UniqueConstraint(name = "uk_esteiras_numero_serial", columnNames = "NumeroSerial")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EsteiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idEsteira;

    @Column(name = "NumeroSerial", nullable = false, length = 100)
    private String numeroSerial;
}
