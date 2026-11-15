# API RollsMetal e integração IO-Link

Fluxo industrial: `AL1300 → Python → Node-RED → Spring Boot → PostgreSQL → Front-end`. O Python consulta o IO-Link Master; o Node-RED encaminha as leituras; o backend identifica o sensor, classifica o valor e persiste o histórico. O Front-end consulta esta API REST.

A hierarquia persistida é `EsteiraEntity → SensorEntity → SensorLeitura`.

## OpenAPI

**Swagger UI:** `/swagger-ui.html`; **documento OpenAPI:** `/v3/api-docs`.

## Endpoints

### Listar esteiras

`GET /api/esteiras` retorna uma lista ordenada por número serial.

### Cadastrar esteira

`POST /api/esteiras` — retorna `201 Created`.

```json
{
  "numeroSerial": "RM-001"
}
```

### Consultar uma esteira

`GET /api/esteiras/{numeroSerial}` — retorna `200 OK` ou `404 Not Found`.

### Consultar sensores da esteira

`GET /api/esteiras/{numeroSerial}/sensores` retorna os sensores ordenados pela porta. O cadastro e a atualização de sensores ocorrem durante a ingestão das leituras.

### Ingerir leituras

`POST /api/integracao/esteiras/{numeroSerial}/leituras` recebe um array e retorna `201 Created` com as leituras persistidas. Não há autenticação por token nesta versão; mantenha a rota em uma rede confiável e use TLS no tráfego implantado.

```json
[
  { "porta": 1, "modelo": "O5D150", "hexadecimal": "1A" },
  { "porta": 2, "modelo": "KI5309", "hexadecimal": "07FF" },
  { "porta": 3, "modelo": "IF6123", "hexadecimal": "02FF" }
]
```

O corpo não pode ser vazio, cada porta pode aparecer uma vez por lote, e cada elemento precisa ser válido. O modelo deve ser `O5D150`, `KI5309` ou `IF6123`; a comparação aceita diferenças de caixa e espaços externos. Modelos não catalogados retornam `422` sem cadastrar sensores ou leituras. Se o modelo conhecido em uma porta mudar, o cadastro atual do sensor é atualizado; cada leitura mantém o modelo e tipo recebidos como snapshot histórico.

O campo `hexadecimal` aceita de 1 a 64 dígitos hexadecimais, sem prefixo `0x`. O texto original, inclusive caixa, é preservado. A classificação provisória usa prefixo `1` para `METAL`, `07` para `PLASTICO`, `02` para `SEM_MATERIAL`; os demais valores válidos são `DESCONHECIDO`.

### Consultar histórico de leituras

`GET /api/esteiras/{numeroSerial}/leituras` retorna histórico paginado, ordenado da leitura mais recente para a mais antiga. `pagina` começa em zero; `tamanho` aceita de 1 a 100. A resposta contém `content`, `page`, `size`, `totalElements` e `totalPages`.

Filtros opcionais:

- `sensorId`: ID do sensor da esteira.
- `porta`: porta física.
- `tipoMaterial`: `METAL`, `PLASTICO`, `SEM_MATERIAL` ou `DESCONHECIDO`.
- `dataInicio` e `dataFim`: limites inclusivos em ISO-8601, com offset/timezone.

Exemplo:

```text
GET /api/esteiras/RM-001/leituras?pagina=0&tamanho=20&sensorId=12&porta=1&tipoMaterial=METAL&dataInicio=2026-11-14T00:00:00Z&dataFim=2026-11-15T00:00:00Z
```

O endpoint também permite consultar leituras de um sensor específico via `sensorId`. Ainda não há endpoint agregado para dashboard; defina primeiro quais contagens e períodos o Front-end precisa para evitar agregar dados de forma inconsistente no cliente.

### Cadastrar usuário

`POST /api/usuarios` — retorna `201 Created`.

```json
{
  "nome": "Operador",
  "email": "operador@rollsmetal.com",
  "senha": "SenhaSegura123"
}
```

Nome e e-mail são normalizados com espaços externos removidos; o e-mail é armazenado em minúsculas. A senha precisa ter ao menos 12 caracteres e é armazenada com BCrypt. Ela não aparece na resposta e não é usada para login nesta versão.

## Erros HTTP

Os erros retornam JSON com `timestamp`, `status`, `error`, `message`, `path` e, para validação de campos, `validationErrors`.

- `400 Bad Request`: payload, paginação, filtro ou lote inválido.
- `404 Not Found`: esteira inexistente.
- `409 Conflict`: serial/e-mail duplicado ou conflito de integridade.
- `422 Unprocessable Entity`: modelo de sensor não suportado.
- `500 Internal Server Error`: falha inesperada.

Nenhum endpoint deste contrato retorna `204 No Content` ou `401 Unauthorized`.

## Configuração e execução

Use [`.env.example`](../.env.example) como referência. Spring Boot não lê `.env` automaticamente; configure as variáveis no terminal, IDE ou plataforma de execução. Variáveis principais:

- `DB_URL`: por padrão `jdbc:postgresql://localhost:5432/rollmetal`.
- `DB_USERNAME`: por padrão `postgres`.
- `DB_PASSWORD`: obrigatório para a conexão configurada.
- `JPA_DDL_AUTO`: padrão `validate`; `update` pode ser usado em desenvolvimento local.

Em produção, use `validate` e aplique migrações revisadas antes de iniciar a nova versão. O repositório ainda não tem Flyway nem migração inicial; compare o schema real e resolva dados legados antes de criar uma baseline. Não remova a coluna `senha` nem outras colunas automaticamente.

## Segurança e saúde operacional

Não há autenticação/autorização de usuários nem token de integração nesta versão. Restrinja a API à rede confiável e proteja o tráfego com TLS. A senha do cadastro é mantida com hash BCrypt por compatibilidade com o contrato e schema existentes; se o produto deixar de exigir essa senha, remova-a em uma mudança versionada da API junto de uma migração controlada.

O Actuator expõe somente `/actuator/health`, `/actuator/health/liveness` e `/actuator/health/readiness`, sem detalhes internos. A prontidão inclui estado da aplicação e conexão com PostgreSQL.

## Testes e validação industrial

Execute `mvnw.cmd clean test` e `mvnw.cmd verify`. Os testes automatizados usam H2; valide diferenças do PostgreSQL em um ambiente de integração antes da implantação.

Os padrões de material são provisórios. Confirme em bancada a codificação do `pdin`, tamanho e ordem dos bytes, comportamento por modelo e valores reais antes de tratar a classificação como regra operacional. O backend não cria leituras artificiais sem resposta do sensor, não limita atualmente o tamanho do lote e não deduplica retries; chamadas repetidas são persistidas como novas observações.
