package com.rollmetal.backend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                "/api/integracao/esteiras/RM-HTTP-01/leituras")
                .contentType(MediaType.APPLICATION_JSON).content(
                        """
                                [{"porta":1,"modelo":"O5D150","hexadecimal":"1a0f"}]
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        assertTrue(ingestao.contains("1a0f"));
        assertTrue(ingestao.contains("METAL"));

        http.perform(post("/api/integracao/esteiras/RM-HTTP-01/leituras")
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
                "/api/integracao/esteiras/RM-HTTP-02/leituras")
                .contentType(MediaType.APPLICATION_JSON).content(
                        """
                                [
                                  {"porta":1,"modelo":"O5D150","hexadecimal":"1A"},
                                  {"porta":2,"modelo":"OUTRO","hexadecimal":"03"}
                                ]
                                """))
                .andExpect(status().isUnprocessableEntity());

        String historico = http.perform(get(
                "/api/esteiras/RM-HTTP-02/leituras?pagina=0&tamanho=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(historico.contains("\"totalElements\":0"));

        String sensores = http.perform(get("/api/esteiras/RM-HTTP-02/sensores"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("[]", sensores);
    }

    @Test
    void rejeitaLoteVazioEHexadecimalMalformado() throws Exception {
        http.perform(post("/api/integracao/esteiras/RM-HTTP-04/leituras")
                        .contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isBadRequest());

        http.perform(post("/api/integracao/esteiras/RM-HTTP-04/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"porta":1,"modelo":"O5D150","hexadecimal":"GG"}]
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejeitaItemNuloEDuplicidadeDePortaNoLote() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-06"}
                """))
                .andExpect(status().isCreated());

        http.perform(post("/api/integracao/esteiras/RM-HTTP-06/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"porta\":1,\"modelo\":\"O5D150\",\"hexadecimal\":\"1A\"},null]"))
                .andExpect(status().isBadRequest());

        http.perform(post("/api/integracao/esteiras/RM-HTTP-06/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"porta":1,"modelo":"O5D150","hexadecimal":"1A"},
                                  {"porta":1,"modelo":"KI5309","hexadecimal":"07"}
                                ]
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void paginaHistoricoComContagemConsistente() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-07"}
                """))
                .andExpect(status().isCreated());

        http.perform(post("/api/integracao/esteiras/RM-HTTP-07/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"porta":1,"modelo":"O5D150","hexadecimal":"1A"},
                                  {"porta":2,"modelo":"KI5309","hexadecimal":"07"}
                                ]
                                """))
                .andExpect(status().isCreated());

        String pagina = http.perform(get("/api/esteiras/RM-HTTP-07/leituras?pagina=0&tamanho=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(pagina.contains("\"totalElements\":2"));
        assertTrue(pagina.contains("\"totalPages\":2"));
        assertTrue(pagina.contains("\"size\":1"));
    }

    @Test
    void readinessEstaDisponivelSemExporDetalhesDoBanco() throws Exception {
        String readiness = http.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(readiness.contains("\"status\":\"UP\""));
        assertFalse(readiness.contains("components"));
    }

    @Test
    void listaEsteirasParaConsultaDoFrontend() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-09"}
                """))
                .andExpect(status().isCreated());
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-08"}
                """))
                .andExpect(status().isCreated());

        String esteiras = http.perform(get("/api/esteiras"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(esteiras.contains("RM-HTTP-08"));
        assertTrue(esteiras.contains("RM-HTTP-09"));
        assertTrue(esteiras.indexOf("RM-HTTP-08") < esteiras.indexOf("RM-HTTP-09"));
    }

    @Test
    void cadastraEValidaCredenciaisComLoginSemExporSenhaOuProtegerOutrasRotas() throws Exception {
        http.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Operador Login","email":"Operador.Login@RollsMetal.com","senha":"SenhaSegura123"}
                """))
                .andExpect(status().isCreated());

        String usuario = http.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":" Operador.Login@RollsMetal.com ","senha":"SenhaSegura123"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(JsonPath.<Integer>read(usuario, "$.id") > 0);
        assertEquals("Operador Login", JsonPath.read(usuario, "$.nome"));
        assertEquals("operador.login@rollsmetal.com", JsonPath.read(usuario, "$.email"));
        assertFalse(usuario.contains("senha"));
        assertFalse(usuario.contains("SenhaSegura123"));

        String senhaIncorreta = http.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"operador.login@rollsmetal.com","senha":"SenhaErrada123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        assertEquals("E-mail ou senha inválidos.", JsonPath.read(senhaIncorreta, "$.message"));
        assertEquals("/api/usuarios/login", JsonPath.read(senhaIncorreta, "$.path"));
        assertEquals("Unauthorized", JsonPath.read(senhaIncorreta, "$.error"));
        assertEquals("{}", JsonPath.read(senhaIncorreta, "$.validationErrors").toString());

        String emailInexistente = http.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nao.existe@rollsmetal.com","senha":"SenhaSegura123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        assertEquals(JsonPath.<String>read(senhaIncorreta, "$.message"),
                JsonPath.<String>read(emailInexistente, "$.message"));

        http.perform(get("/api/esteiras")).andExpect(status().isOk());
    }

    @Test
    void filtraHistoricoPorSensorPortaMaterialEPeriodo() throws Exception {
        http.perform(post("/api/esteiras").contentType(MediaType.APPLICATION_JSON).content("""
                {"numeroSerial":"RM-HTTP-10"}
                """))
                .andExpect(status().isCreated());

        String ingestao = http.perform(post("/api/integracao/esteiras/RM-HTTP-10/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"porta":1,"modelo":"O5D150","hexadecimal":"1A"},
                                  {"porta":2,"modelo":"KI5309","hexadecimal":"07"}
                                ]
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String sensorId = JsonPath.read(ingestao, "$[0].sensorId").toString();
        String dataInicio = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String dataFim = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(5)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String filtrado = http.perform(get("/api/esteiras/RM-HTTP-10/leituras")
                        .queryParam("pagina", "0")
                        .queryParam("tamanho", "20")
                        .queryParam("sensorId", sensorId)
                        .queryParam("porta", "1")
                        .queryParam("tipoMaterial", "METAL")
                        .queryParam("dataInicio", dataInicio)
                        .queryParam("dataFim", dataFim))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(filtrado.contains("\"totalElements\":1"));
        assertTrue(filtrado.contains("\"sensorId\":" + sensorId));
        assertTrue(filtrado.contains("\"tipoMaterial\":\"METAL\""));
    }

    @Test
    void openApiDescreveEndpointsESwaggerUiEstaDisponivel() throws Exception {
        String especificacao = http.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(especificacao.contains("RollsMetal Backend API"));
        assertTrue(especificacao.contains("/api/esteiras"));
        assertTrue(especificacao.contains("/api/integracao/esteiras/{numeroSerial}/leituras"));
        assertTrue(especificacao.contains("/api/usuarios/login"));
        assertTrue(especificacao.contains("dataInicio"));

        http.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
