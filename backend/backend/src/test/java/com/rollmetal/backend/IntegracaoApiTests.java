package com.rollmetal.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:rollmetal-http-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoApiTests {
    private static final String TOKEN = "0123456789abcdef0123456789abcdef";
    @Autowired
    private MockMvc http;

    @Test
    void cadastraEsteiraIngereLeituraEConsultaHistorico() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content(
                """
                        {"numeroSerial":"RM-HTTP-01"}
                        """))
                .andExpect(status().isCreated());

        String ingestao = http.perform(post(
                "/api/integracao/esteiras/RM-HTTP-01/leituras").header("X-Integration-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content(
                        """
                                [{"porta":1,"modelo":"O5D150","hexadecimal":"1a0f"}]
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        assertTrue(ingestao.contains("1a0f"));
        assertTrue(ingestao.contains("METAL"));

        http.perform(post("/api/integracao/esteiras/RM-HTTP-01/leituras")
                        .header("X-Integration-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"porta":1,"modelo":"KI5309","hexadecimal":"07FF"}]
                                """))
                .andExpect(status().isCreated());

        String historico = http.perform(get(
                "/api/esteiras/RM-HTTP-01/leituras?pagina=0&tamanho=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(historico.contains("1a0f"));
        assertTrue(historico.contains("\"totalElements\":2"));
        assertTrue(historico.contains("O5D150"));
        assertTrue(historico.contains("KI5309"));
        assertTrue(historico.contains("CAPACITIVO"));
    }

    @Test
    void modeloDesconhecidoRetorna422ENaoPersisteLeitura() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-02"}
                """))
                .andExpect(status().isCreated());

        http.perform(post(
                "/api/integracao/esteiras/RM-HTTP-02/leituras").header("X-Integration-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content(
                        """
                                [{"porta":1,"modelo":"OUTRO","hexadecimal":"03"}]
                                """))
                .andExpect(status().isUnprocessableEntity());

        String historico = http.perform(get(
                "/api/esteiras/RM-HTTP-02/leituras?pagina=0&tamanho=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(historico.contains("\"totalElements\":0"));
    }

    @Test
    void bloqueiaIngestaoSemToken() throws Exception {
        http.perform(post("/api/integracao/esteiras/RM-HTTP-03/leituras")
                        .contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejeitaLoteVazioEHexadecimalMalformado() throws Exception {
        http.perform(post("/api/integracao/esteiras/RM-HTTP-04/leituras")
                        .header("X-Integration-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isBadRequest());

        http.perform(post("/api/integracao/esteiras/RM-HTTP-04/leituras")
                        .header("X-Integration-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"porta":1,"modelo":"O5D150","hexadecimal":"GG"}]
                                """))
                .andExpect(status().isBadRequest());
    }
}
