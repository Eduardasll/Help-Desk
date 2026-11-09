# Help Desk API — Sistema de Chamados de Suporte

API REST em **Java + Spring Boot** para abrir, acompanhar e resolver chamados de suporte técnico.

Uma empresa precisa controlar pedidos de ajuda ("não consigo acessar o sistema", "meu
computador não liga"). Cada pedido vira um **chamado**, ligado ao **usuário** que o abriu,
com prioridade e status. Esta API é o backend que gerencia tudo isso.

---

## Funcionalidades

- Cadastrar e consultar **usuários**.
- Abrir, consultar, filtrar e editar **chamados**.
- Controlar o fluxo de status: `ABERTO` → `EM_ANDAMENTO` → `RESOLVIDO`.
- Validações de dados e tratamento de erros com os códigos HTTP corretos.

Fluxo de status (não há reabertura no MVP):

```text
ABERTO ── iniciar ──> EM_ANDAMENTO ── resolver ──> RESOLVIDO
```

---

## Tecnologias

- Java 17
- Spring Boot (Spring Web, Spring Data JPA, Bean Validation)
- PostgreSQL (ou H2 para testes rápidos)
- Maven
- JUnit 5 + Mockito

---

## Como rodar no IntelliJ

**Pré-requisitos:** JDK 17 (ou 21) e IntelliJ IDEA.

1. **Open** no IntelliJ apontando para a pasta do projeto (que contém o `pom.xml`).
2. Aguarde o Maven baixar as dependências.
3. Abra `src/main/java/br/com/portfolio/helpdesk/HelpDeskApplication.java` e clique no
   **▶** ao lado do `main`.

A API sobe em `http://localhost:8080`.

### Banco de dados

**Modo rápido (H2 em memória, sem instalar nada):** em **Run → Edit Configurations...**,
no campo **Active profiles**, escreva `h2`. Os dados ficam em memória (somem ao desligar).
Console web: `http://localhost:8080/h2-console`.

**Modo definitivo (PostgreSQL):** crie um banco `helpdesk` e informe as credenciais por
variáveis de ambiente (em **Run → Edit Configurations... → Environment variables**):

```
DB_URL=jdbc:postgresql://localhost:5432/helpdesk
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
```

Se nada for configurado, ele tenta o PostgreSQL local padrão (`localhost:5432`, `postgres`/`postgres`).

---

## Rotas da API

Base: `http://localhost:8080`

### Usuários

| Método | Rota | Finalidade | Sucesso |
|---|---|---|---|
| `POST` | `/usuarios` | Cadastrar usuário | `201` |
| `GET` | `/usuarios` | Listar usuários | `200` |
| `GET` | `/usuarios/{id}` | Consultar usuário | `200` |

### Chamados

| Método | Rota | Finalidade | Sucesso |
|---|---|---|---|
| `POST` | `/chamados` | Abrir chamado | `201` |
| `GET` | `/chamados` | Listar / filtrar | `200` |
| `GET` | `/chamados/{id}` | Consultar chamado | `200` |
| `PUT` | `/chamados/{id}` | Editar título, descrição, categoria e prioridade | `200` |
| `PATCH` | `/chamados/{id}/iniciar` | Iniciar atendimento | `200` |
| `PATCH` | `/chamados/{id}/resolver` | Resolver chamado | `200` |

Filtros (opcionais e combináveis):

```
GET /chamados?status=ABERTO
GET /chamados?prioridade=ALTA
GET /chamados?usuarioId=1
GET /chamados?status=ABERTO&prioridade=ALTA
```

### Códigos de erro

| Situação | HTTP |
|---|---|
| Dados inválidos | `400` |
| Usuário/chamado não encontrado | `404` |
| E-mail já cadastrado | `409` |
| Mudança de status não permitida | `409` |

---

## Exemplos (Postman)

**Criar usuário** — `POST /usuarios`

```json
{
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
```

**Abrir chamado** — `POST /chamados`

```json
{
  "usuarioId": 1,
  "titulo": "Não consigo acessar o sistema",
  "descricao": "Ao tentar entrar, aparece uma mensagem de acesso negado.",
  "categoria": "ACESSO",
  "prioridade": "ALTA"
}
```

Resposta `201`:

```json
{
  "id": 1,
  "titulo": "Não consigo acessar o sistema",
  "descricao": "Ao tentar entrar, aparece uma mensagem de acesso negado.",
  "categoria": "ACESSO",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "usuario": { "id": 1, "nome": "Maria Silva" },
  "abertoEm": "2027-01-10T10:00:00",
  "resolvidoEm": null
}
```

**Iniciar / resolver** — `PATCH /chamados/1/iniciar` e `PATCH /chamados/1/resolver`
(sem corpo).

---

## Estrutura do projeto

```text
src/main/java/br/com/portfolio/helpdesk/
├── HelpDeskApplication.java   → inicia a aplicação
├── controller/                → recebe as requisições HTTP
├── service/                   → aplica as regras de negócio
├── repository/                → acessa o banco de dados
├── model/                     → entidades e enums do domínio
├── dto/                       → objetos de entrada e saída da API
└── exception/                 → erros de negócio e tratamento global
```

---

## Testes

Pela IDE: botão direito em `src/test` → **Run 'All Tests'**.
Pela linha de comando: `mvn test`.

Há testes cobrindo as regras principais: abrir, iniciar e resolver chamados.
