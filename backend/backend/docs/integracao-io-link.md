# API RollMetal e integração IO-Link

O Node-RED recebe do cliente Python as leituras do AL1300 e encaminha o lote ao backend. A hierarquia persistida permanece `EsteiraEntity -> SensorEntity -> SensorLeitura`.

## Endpoints

### Cadastrar esteira

`POST /api/esteiras` — retorna `201 Created`.

```json
{
  "numeroSerial": "RM-001"
}
```

### Consultar sensores da esteira

`GET /api/esteiras/{numeroSerial}/sensores`

### Ingerir leituras

`POST /api/integracao/esteiras/{numeroSerial}/leituras` — exige `X-Integration-Token`, recebe um array e retorna `201 Created` com as leituras gravadas. Configure `INTEGRATION_TOKEN` com um segredo aleatório de pelo menos 32 caracteres e transmita a chamada por HTTPS.

```json
[
  { "porta": 1, "modelo": "O5D150", "hexadecimal": "1A" },
  { "porta": 2, "modelo": "KI5309", "hexadecimal": "07FF" },
  { "porta": 3, "modelo": "IF6123", "hexadecimal": "02FF" }
]
```

O corpo não pode ser vazio. Cada porta pode aparecer uma vez por lote. O modelo deve ser `O5D150`, `KI5309` ou `IF6123`; a comparação aceita diferenças de caixa e espaços externos. Modelos não catalogados retornam `422` sem cadastrar sensores nem leituras. Se o modelo conhecido em uma porta mudar, o cadastro atual do sensor é atualizado e cada leitura mantém o modelo e o tipo recebidos como snapshot histórico.

O campo `hexadecimal` aceita de 1 a 64 dígitos hexadecimais, sem prefixo `0x`. O texto original, incluindo caixa, é persistido para auditoria. A regra provisória classifica prefixos `1` como `METAL`, `07` como `PLASTICO`, `02` como `SEM_MATERIAL`; demais valores válidos viram `DESCONHECIDO`.

### Consultar histórico

`GET /api/esteiras/{numeroSerial}/leituras?pagina=0&tamanho=20`

As leituras são ordenadas da mais recente para a mais antiga. `pagina` começa em zero e `tamanho` aceita de 1 a 100. A resposta contém `content`, `page`, `size`, `totalElements` e `totalPages`.

### Cadastrar usuário

`POST /api/usuarios` — retorna `201 Created`. A senha deve ter ao menos 12 caracteres e é armazenada com BCrypt; ela nunca aparece no DTO de resposta.

## Erros

Erros retornam JSON com `timestamp`, `status`, `error`, `message`, `path` e, em validações de campos, `validationErrors`. Códigos usados: `400` para payload/parâmetro inválido, `401` para token ausente/inválido, `404` para esteira inexistente, `409` para conflito de cadastro e `422` para modelo de sensor não suportado.

## Configuração e validação industrial

Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `INTEGRATION_TOKEN` no ambiente; a aplicação não inicia sem senha de banco nem token de integração. `JPA_DDL_AUTO` pode ser usado para controlar o modo de esquema; `update` é o padrão de desenvolvimento. Em produção, aplique uma migração revisada e use `JPA_DDL_AUTO=validate`. As colunas de modelo e tipo snapshot aceitam `NULL` em registros legados; o primeiro modelo conhecido recebido atualiza o cadastro atual, e os históricos sem snapshot permanecem nulos. Resolva números seriais duplicados antes de ativar a restrição de unicidade.

O cadastro de usuário e os endpoints administrativos continuam sem autenticação/autorização nesta versão. Restrinja-os na camada de rede ou defina uma política de autenticação antes de disponibilizar a API fora do ambiente confiável.

Os padrões de material são provisórios. Confirme em bancada a codificação de `pdin`, tamanho e ordem dos bytes, comportamento por modelo e valores observados para cada material antes de tratar a classificação como regra operacional. O backend não altera nem normaliza o hexadecimal recebido.
