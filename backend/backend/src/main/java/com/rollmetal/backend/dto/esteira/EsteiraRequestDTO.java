package com.rollmetal.backend.dto.esteira;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EsteiraRequestDTO(
        @NotBlank @Size(max = 100) String numeroSerial
) {
}
