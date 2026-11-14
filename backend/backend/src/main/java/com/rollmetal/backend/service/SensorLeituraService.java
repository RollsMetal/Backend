package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.model.SensorEntity;
import com.rollmetal.backend.database.model.SensorLeitura;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.database.repository.SensorLeituraRepository;
import com.rollmetal.backend.database.repository.SensorRepository;
import com.rollmetal.backend.dto.leitura.SensorLeituraRequestDTO;
import com.rollmetal.backend.dto.leitura.SensorLeituraResponseDTO;
import com.rollmetal.backend.dto.leitura.SensorLeiturasPageResponseDTO;
import com.rollmetal.backend.enums.TipoSensor;
import com.rollmetal.backend.exception.ConflitoDeConfiguracaoException;
import com.rollmetal.backend.exception.RecursoNaoEncontradoException;
import com.rollmetal.backend.exception.RequisicaoInvalidaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SensorLeituraService {
    private final EsteiraRepository esteiraRepository;
    private final SensorRepository sensorRepository;
    private final SensorLeituraRepository leituraRepository;
    private final CatalogoModeloSensor catalogoModeloSensor;
    private final ClassificadorMaterial classificadorMaterial;

    public SensorLeituraService(EsteiraRepository esteiraRepository,
                                SensorRepository sensorRepository,
                                SensorLeituraRepository leituraRepository,
                                CatalogoModeloSensor catalogoModeloSensor,
                                ClassificadorMaterial classificadorMaterial) {
        this.esteiraRepository = esteiraRepository;
        this.sensorRepository = sensorRepository;
        this.leituraRepository = leituraRepository;
        this.catalogoModeloSensor = catalogoModeloSensor;
        this.classificadorMaterial = classificadorMaterial;
    }

    @Transactional
    public List<SensorLeituraResponseDTO> registrarLeituras(
            String numeroSerial, List<SensorLeituraRequestDTO> leiturasRecebidas) {
        if (leiturasRecebidas == null || leiturasRecebidas.isEmpty()) {
            throw new RequisicaoInvalidaException("A lista de leituras não pode estar vazia.");
        }

        EsteiraEntity esteira = esteiraRepository.findByNumeroSerial(numeroSerial)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Esteira não encontrada para o número serial informado."));

        // Valida o lote inteiro antes de iniciar qualquer gravação.
        Set<Short> portas = new HashSet<>();
        for (SensorLeituraRequestDTO leitura : leiturasRecebidas) {
            if (!portas.add(leitura.porta())) {
                throw new RequisicaoInvalidaException("A lista contém mais de uma leitura para a mesma porta.");
            }
            catalogoModeloSensor.identificar(leitura.modelo());
        }

        return leiturasRecebidas.stream()
                .map(leitura -> registrarLeitura(esteira, leitura))
                .toList();
    }

    @Transactional(readOnly = true)
    public SensorLeiturasPageResponseDTO listarPorEsteira(String numeroSerial, int pagina, int tamanho) {
        if (pagina < 0 || tamanho < 1 || tamanho > 100) {
            throw new RequisicaoInvalidaException("A página deve ser não negativa e o tamanho deve estar entre 1 e 100.");
        }
        EsteiraEntity esteira = esteiraRepository.findByNumeroSerial(numeroSerial)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Esteira não encontrada para o número serial informado."));
        Page<SensorLeitura> resultado = leituraRepository.buscarHistoricoDaEsteira(
                esteira.getIdEsteira(), PageRequest.of(pagina, tamanho));
        return new SensorLeiturasPageResponseDTO(
                resultado.getContent().stream().map(this::paraResponse).toList(),
                resultado.getNumber(), resultado.getSize(), resultado.getTotalElements(), resultado.getTotalPages());
    }

    private SensorLeituraResponseDTO registrarLeitura(EsteiraEntity esteira, SensorLeituraRequestDTO entrada) {
        String modeloCanonico = catalogoModeloSensor.modeloCanonico(entrada.modelo());
        TipoSensor tipoSensor = catalogoModeloSensor.identificar(modeloCanonico);

        SensorEntity sensor = sensorRepository.findByEsteira_IdEsteiraAndPorta(esteira.getIdEsteira(), entrada.porta())
                .orElseGet(() -> SensorEntity.builder()
                        .esteira(esteira)
                        .porta(entrada.porta())
                        .build());
        sensor.setModelo(modeloCanonico);
        sensor.setTipoSensor(tipoSensor);
        sensor = sensorRepository.save(sensor);

        SensorLeitura leitura = SensorLeitura.builder()
                .sensor(sensor)
                .modeloRecebido(entrada.modelo())
                .tipoSensorRecebido(tipoSensor)
                .valorLeitura(entrada.hexadecimal())
                .tipoMaterial(classificadorMaterial.classificar(entrada.hexadecimal()))
                .data(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        return paraResponse(leituraRepository.save(leitura));
    }

    private SensorLeituraResponseDTO paraResponse(SensorLeitura leitura) {
        SensorEntity sensor = leitura.getSensor();
        return new SensorLeituraResponseDTO(
                leitura.getIdLeitura(), sensor.getIdSensor(), sensor.getPorta(), leitura.getModeloRecebido(),
                leitura.getTipoSensorRecebido(), leitura.getValorLeitura(), leitura.getTipoMaterial(), leitura.getData());
    }
}
