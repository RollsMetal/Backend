# Backend RollsMetal

API REST em Java 25 e Spring Boot 4 para gerenciar esteiras, consultar sensores e armazenar leituras encaminhadas pelo Node-RED. O fluxo industrial é `AL1300 → Python → Node-RED → Spring Boot → PostgreSQL → Front-end`; o backend não consulta o IO-Link Master diretamente.

## Arquitetura

A hierarquia persistida é `EsteiraEntity → SensorEntity → SensorLeitura`. Controllers expõem os contratos HTTP; services concentram regras do domínio e transações; repositories acessam PostgreSQL via Spring Data JPA; DTOs definem entrada e saída. `CatalogoModeloSensor` identifica os modelos confirmados e `ClassificadorMaterial` mantém as regras provisórias do hexadecimal.

Os pacotes ficam em `src/main/java/com/rollmetal/backend`: `config`, `controller`, `database/model`, `database/repository`, `dto`, `enums`, `exception` e `service`.

## Tecnologias e requisitos

- Java 25
- Spring Boot 4.1.2, Spring MVC e Spring Data JPA
- Maven 3.6.3 ou superior (o Maven Wrapper está incluído)
- PostgreSQL para execução normal; H2 nos testes
- Jakarta Validation, Lombok, Springdoc OpenAPI, JUnit 5, Mockito e BCrypt para armazenar a senha cadastrada

## Configuração e execução local

Use [`.env.example`](./.env.example) como referência para `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JPA_DDL_AUTO`. O Spring Boot não carrega arquivos `.env` automaticamente: defina as variáveis no terminal, IDE ou ambiente de execução.

Exemplo em PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/rollmetal"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "sua-senha-local"
$env:JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run
```

O padrão de `JPA_DDL_AUTO` é `validate`. Use `update` apenas em desenvolvimento local; em produção, valide o schema existente e aplique alterações por migrações revisadas. O repositório ainda não tem Flyway nem migração inicial.

## Endpoints para o Front-end e Node-RED

| Método e rota | Uso |
| --- | --- |
| `GET /api/esteiras` | Lista esteiras ordenadas pelo número serial. |
| `POST /api/esteiras` | Cadastra uma esteira. |
| `GET /api/esteiras/{numeroSerial}` | Consulta uma esteira. |
| `GET /api/esteiras/{numeroSerial}/sensores` | Lista sensores associados. |
| `GET /api/esteiras/{numeroSerial}/leituras` | Consulta histórico paginado com filtros opcionais. |
| `POST /api/integracao/esteiras/{numeroSerial}/leituras` | Recebe leituras em lote do Node-RED. |
| `POST /api/usuarios` | Cadastra um usuário. |

O histórico aceita `pagina` (inicia em zero), `tamanho` (1–100), `sensorId`, `porta`, `tipoMaterial`, `dataInicio` e `dataFim`. Datas usam ISO-8601; os dois limites são inclusivos. Exemplo:

```text
GET /api/esteiras/RM-001/leituras?pagina=0&tamanho=20&sensorId=12&porta=1&tipoMaterial=METAL&dataInicio=2026-11-14T00:00:00Z&dataFim=2026-11-15T00:00:00Z
```

A resposta de histórico contém `content`, `page`, `size`, `totalElements` e `totalPages`. Ainda não há um endpoint agregado específico para dashboards; o Front-end pode filtrar o histórico paginado, e um contrato de agregação pode ser definido quando as métricas do produto forem conhecidas.

Os detalhes dos payloads, respostas e erros estão no [guia da integração IO-Link](./docs/integracao-io-link.md).

## OpenAPI e saúde

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Documento OpenAPI: `http://localhost:8080/v3/api-docs`
- Health: `/actuator/health`
- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness` (inclui PostgreSQL)

O Actuator expõe somente saúde, sem detalhes dos componentes. Restrinja Swagger UI e a API à rede apropriada em ambientes implantados.

## Usuários e segurança

Não há autenticação ou autorização implementada. A ingestão do Node-RED também não exige token nesta versão; mantenha a API em uma rede confiável e use TLS no tráfego implantado.

O cadastro atual recebe `senha` e a armazena com BCrypt, mas não existe login que a utilize. Mantive o hash para preservar o contrato atual e a coluna obrigatória do banco; nunca armazene essa senha em texto puro. Se o cadastro deixar de precisar dela, remova o campo por uma mudança versionada da API e uma migração revisada do schema.

## Modelos e classificação

O catálogo aceita somente `O5D150`, `KI5309` e `IF6123`. O backend preserva o hexadecimal original; as regras provisórias são prefixo `1` → `METAL`, `07` → `PLASTICO`, `02` → `SEM_MATERIAL` e demais valores válidos → `DESCONHECIDO`. Confirme o formato e a classificação do `pdin` em bancada antes do uso operacional.

## Testes

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd verify
```
