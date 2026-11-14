package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.dto.esteira.EsteiraRequestDTO;
import com.rollmetal.backend.dto.esteira.EsteiraResponseDTO;
import com.rollmetal.backend.exception.ConflitoDeConfiguracaoException;
import com.rollmetal.backend.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EsteiraService {
    private final EsteiraRepository esteiraRepository;

    public EsteiraService(EsteiraRepository esteiraRepository) {
        this.esteiraRepository = esteiraRepository;
    }

    @Transactional
    public EsteiraResponseDTO cadastrar(EsteiraRequestDTO request) {
        String serial = request.numeroSerial().strip();
        if (esteiraRepository.existsByNumeroSerial(serial)) {
            throw new ConflitoDeConfiguracaoException("Já existe uma esteira com o número serial informado.");
        }
        EsteiraEntity esteira = esteiraRepository.save(EsteiraEntity.builder().numeroSerial(serial).build());
        return paraResponse(esteira);
    }

    @Transactional(readOnly = true)
    public EsteiraResponseDTO buscar(String numeroSerial) {
        EsteiraEntity esteira = esteiraRepository.findByNumeroSerial(numeroSerial)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esteira não encontrada."));
        return paraResponse(esteira);
    }

    private EsteiraResponseDTO paraResponse(EsteiraEntity esteira) {
        return new EsteiraResponseDTO(esteira.getIdEsteira(), esteira.getNumeroSerial());
    }
}
