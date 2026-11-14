package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.database.repository.SensorRepository;
import com.rollmetal.backend.dto.sensor.SensorResponseDTO;
import com.rollmetal.backend.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SensorService {
    private final EsteiraRepository esteiraRepository;
    private final SensorRepository sensorRepository;

    public SensorService(EsteiraRepository esteiraRepository, SensorRepository sensorRepository) {
        this.esteiraRepository = esteiraRepository;
        this.sensorRepository = sensorRepository;
    }

    @Transactional(readOnly = true)
    public List<SensorResponseDTO> listarPorEsteira(String numeroSerial) {
        EsteiraEntity esteira = esteiraRepository.findByNumeroSerial(numeroSerial)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esteira não encontrada."));
        return sensorRepository.findByEsteira_IdEsteiraOrderByPorta(esteira.getIdEsteira()).stream()
                .map(sensor -> new SensorResponseDTO(sensor.getIdSensor(), numeroSerial, sensor.getPorta(),
                        sensor.getModelo(), sensor.getTipoSensor()))
                .toList();
    }
}
