package com.rollmetal.backend.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "esteiras")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsteiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id_esteira;

    @Column(name = "NumeroSerial", nullable = false, length = 100)
    private String numeroSerial;
}