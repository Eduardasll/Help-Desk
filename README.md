# Help Desk API — Sistema de Chamados de Suporte

API REST em **Java + Spring Boot** para abrir, acompanhar e resolver chamados de suporte técnico.
Este documento explica **o que o projeto faz**, **como rodar** e, principalmente, **o que cada
pedaço do código significa** — mesmo que você nunca tenha programado em Java.

---

## Índice

1. [O que este projeto faz](#1-o-que-este-projeto-faz)
2. [Conceitos básicos antes de começar](#2-conceitos-basicos-antes-de-comecar)
3. [Como rodar no IntelliJ](#3-como-rodar-no-intellij)
4. [As rotas da API (contrato)](#4-as-rotas-da-api-contrato)
5. [Exemplos de uso no Postman](#5-exemplos-de-uso-no-postman)
6. [A estrutura de pastas explicada](#6-a-estrutura-de-pastas-explicada)
7. [O caminho de uma requisição (passo a passo)](#7-o-caminho-de-uma-requisicao-passo-a-passo)
8. [Explicando cada arquivo do código](#8-explicando-cada-arquivo-do-codigo)
9. [As regras de negócio](#9-as-regras-de-negocio)
10. [Os testes automatizados](#10-os-testes-automatizados)
11. [Glossário rápido](#11-glossario-rapido)

---

## 1. O que este projeto faz

Imagine o setor de TI de uma empresa. Toda vez que alguém tem um problema
("não consigo acessar o sistema", "meu computador não liga"), essa pessoa abre um
**chamado**. Este projeto é o **cérebro (backend)** que guarda e controla esses chamados.

Ele permite:

- Cadastrar **usuários** (as pessoas que abrem chamados).
- Abrir **chamados** ligados a um usuário.
- Acompanhar o **status** do chamado: `ABERTO` → `EM_ANDAMENTO` → `RESOLVIDO`.
- Filtrar chamados por status, prioridade ou usuário.
- Impedir ações inválidas (ex.: resolver um chamado que nem começou a ser atendido).

Não há tela/visual (frontend). Você interage com ele enviando mensagens via **Postman**
(explicado abaixo).

---

## 2. Conceitos básicos antes de começar

Se algum termo abaixo for novo, leia esta seção com calma. Ela é a base de tudo.

### O que é uma API REST?

**API** é a "porta de entrada" de um sistema: um conjunto de endereços (URLs) que outros
programas podem chamar para pedir ou enviar informação. **REST** é um estilo de organizar
essa porta usando os verbos do protocolo HTTP:

- **GET** → *buscar/ler* dados ("me mostre os usuários").
- **POST** → *criar* algo novo ("cadastre este usuário").
- **PUT** → *substituir/atualizar* um dado existente.
- **PATCH** → *alterar parcialmente* um dado (aqui, mudar só o status).
- **DELETE** → *apagar* (não usamos neste projeto).

Cada resposta vem com um **código HTTP** que resume o resultado:

| Código | Significado | Quando acontece aqui |
|---|---|---|
| `200 OK` | Deu certo | Busca ou atualização bem-sucedida |
| `201 Created` | Criado | Usuário ou chamado cadastrado |
| `400 Bad Request` | Você enviou dados inválidos | E-mail mal formatado, descrição curta |
| `404 Not Found` | Não existe | ID de usuário/chamado que não existe |
| `409 Conflict` | Conflito com o estado atual | E-mail repetido, transição de status proibida |

### O que é JSON?

É o formato de texto usado para trocar dados. Parece isto:

```json
{
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
```

É só um conjunto de `"chave": valor`. A API recebe JSON e também responde em JSON.

### O que é o Spring Boot?

**Java** é a linguagem de programação. **Spring Boot** é um *framework* — um conjunto de
ferramentas prontas que cuidam da parte chata (subir um servidor web, conversar com o banco
de dados, transformar JSON em objetos Java e vice-versa) para você focar na lógica do negócio.

### O que é JPA / banco de dados?

Os dados precisam ser **guardados** em algum lugar permanente: o **banco de dados**
(aqui, PostgreSQL ou H2). **JPA** é a tecnologia que faz a ponte entre as **classes Java**
e as **tabelas do banco**. Você mexe em objetos Java e o JPA traduz isso em comandos SQL
automaticamente.

### DDD e Clean Code (as "boas práticas" por trás)

- **Clean Code (código limpo):** escrever o código de forma clara, com nomes que explicam
  a intenção, funções pequenas e cada classe com uma única responsabilidade. Por isso o
  código deste projeto **não tem comentários espalhados** — ele foi escrito para se explicar
  sozinho através de bons nomes.
- **DDD (Domain-Driven Design):** colocar as **regras do negócio dentro das próprias
  entidades do domínio**, e não espalhadas pelo sistema. Repare que é a classe `Chamado`
  quem sabe "só posso ser resolvido se eu estiver em andamento" (método `resolver()`). Isso
  se chama **domínio rico**: o objeto protege as próprias regras, ninguém consegue burlá-las
  por fora.

---

## 3. Como rodar no IntelliJ

### Pré-requisitos

- **JDK 17** (ou 21) instalado.
- **IntelliJ IDEA** (a edição Community já serve).
- **Maven** (vem embutido no IntelliJ).

### Passo a passo

1. Abra o IntelliJ e escolha **Open** apontando para a pasta deste projeto (a que contém o
   `pom.xml`). O IntelliJ reconhece que é um projeto Maven e baixa as dependências sozinho.
2. Espere a barra de progresso inferior terminar de baixar tudo.
3. Abra o arquivo `src/main/java/br/com/portfolio/helpdesk/HelpDeskApplication.java`.
4. Clique no **triângulo verde ▶** ao lado do método `main` para iniciar a aplicação.

A aplicação sobe em `http://localhost:8080`.

### Escolhendo o banco de dados

Este projeto vem pronto para **dois modos**:

**Modo rápido (H2, banco em memória — recomendado para testar agora):**
não precisa instalar nada. Rode com o *profile* `h2` ativo. No IntelliJ:

- Vá em **Run → Edit Configurations...**
- No campo **Active profiles**, escreva: `h2`
- Rode novamente.

Nesse modo os dados ficam na memória e somem ao desligar a aplicação — ótimo para testes.
Você ainda pode abrir `http://localhost:8080/h2-console` para ver as tabelas.

**Modo definitivo (PostgreSQL — dados que persistem):**
instale o PostgreSQL, crie um banco chamado `helpdesk` e informe as credenciais por
**variáveis de ambiente** (assim você nunca escreve senha dentro do código):

```
DB_URL=jdbc:postgresql://localhost:5432/helpdesk
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
```

No IntelliJ você as coloca em **Run → Edit Configurations... → Environment variables**.
Se você não configurar nada, ele tenta o PostgreSQL local em `localhost:5432` com
usuário/senha `postgres`.

---

## 4. As rotas da API (contrato)

Endereço base: `http://localhost:8080`

### Usuários

| Método | Rota | O que faz | Sucesso |
|---|---|---|---|
| `POST` | `/usuarios` | Cadastra um usuário | `201` |
| `GET` | `/usuarios` | Lista todos os usuários | `200` |
| `GET` | `/usuarios/{id}` | Busca um usuário pelo ID | `200` |

### Chamados

| Método | Rota | O que faz | Sucesso |
|---|---|---|---|
| `POST` | `/chamados` | Abre um chamado | `201` |
| `GET` | `/chamados` | Lista / filtra chamados | `200` |
| `GET` | `/chamados/{id}` | Busca um chamado pelo ID | `200` |
| `PUT` | `/chamados/{id}` | Edita título, descrição, categoria e prioridade | `200` |
| `PATCH` | `/chamados/{id}/iniciar` | Muda status para `EM_ANDAMENTO` | `200` |
| `PATCH` | `/chamados/{id}/resolver` | Muda status para `RESOLVIDO` | `200` |

Filtros (todos opcionais, podem ser combinados):

```
GET /chamados?status=ABERTO
GET /chamados?prioridade=ALTA
GET /chamados?usuarioId=1
GET /chamados?status=ABERTO&prioridade=ALTA
```

---

## 5. Exemplos de uso no Postman

**Postman** é um programa onde você monta e envia requisições HTTP manualmente para testar
a API. Baixe em [postman.com](https://www.postman.com/).

### Criar um usuário

- Método: `POST` — URL: `http://localhost:8080/usuarios`
- Aba **Body** → **raw** → tipo **JSON**:

```json
{
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
```

Resposta `201`:

```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
```

### Abrir um chamado

- Método: `POST` — URL: `http://localhost:8080/chamados`

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

### Iniciar e resolver

- `PATCH http://localhost:8080/chamados/1/iniciar` (sem corpo) → status vira `EM_ANDAMENTO`.
- `PATCH http://localhost:8080/chamados/1/resolver` (sem corpo) → status vira `RESOLVIDO`
  e `resolvidoEm` é preenchido.

Se você tentar resolver um chamado ainda `ABERTO`, recebe `409`:

```json
{
  "mensagem": "Só é possível resolver um chamado que está EM_ANDAMENTO"
}
```

---

## 6. A estrutura de pastas explicada

O código é organizado em **camadas**. Cada camada tem uma responsabilidade e conversa só
com a vizinha. Isso é organização de "arquitetura em camadas", prima do Clean Architecture.

```text
src/main/java/br/com/portfolio/helpdesk/
├── HelpDeskApplication.java     → o "botão de ligar" da aplicação
├── controller/                  → recebe as requisições HTTP (a recepção)
├── service/                     → aplica as regras de negócio (o cérebro)
├── repository/                  → fala com o banco de dados (o arquivista)
├── model/                       → as entidades e enums (os "substantivos" do sistema)
├── dto/                         → objetos de entrada/saída (os "formulários")
└── exception/                   → erros de negócio e o tradutor de erros em HTTP
```

Analogia de um restaurante:

- **Controller** = o garçom: anota o pedido e entrega a resposta, sem cozinhar.
- **Service** = o chef: decide como preparar, seguindo as regras da cozinha.
- **Repository** = a despensa: guarda e busca os ingredientes (dados).
- **Model** = os ingredientes e as receitas (as regras que cada prato carrega).
- **DTO** = o cardápio e a comanda: o formato combinado de pedir e entregar.

**Por que separar assim?** Porque fica fácil achar as coisas, testar cada parte isolada e
mudar uma camada sem quebrar as outras. Colocar tudo no Controller daria um "código
espaguete" impossível de manter.

---

## 7. O caminho de uma requisição (passo a passo)

Quando você faz `POST /chamados` no Postman, acontece isto, em ordem:

```
Postman
  │  (envia JSON)
  ▼
ChamadoController   → recebe o JSON, o Spring o transforma em um ChamadoRequest
  │                   e valida os campos (@Valid)
  ▼
ChamadoService      → aplica as regras: o usuário existe? cria o objeto Chamado
  │
  ▼
Chamado (model)     → o próprio objeto nasce já com status ABERTO e data de abertura
  │
  ▼
ChamadoRepository   → salva no banco de dados
  │
  ▼
ChamadoResponse     → o resultado é convertido num JSON de resposta
  │
  ▼
Postman             → recebe 201 Created + o JSON do chamado criado
```

Se algo der errado no meio (usuário não existe, dado inválido), uma **exceção** é lançada e
o `GlobalExceptionHandler` a transforma no código HTTP certo (404, 400, 409...).

---

## 8. Explicando cada arquivo do código

### `HelpDeskApplication.java` — o ponto de partida

```java
@SpringBootApplication
public class HelpDeskApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelpDeskApplication.class, args);
    }
}
```

- `main` é o método que o Java executa primeiro ao ligar qualquer programa.
- `@SpringBootApplication` é uma **anotação** (uma etiqueta que dá poderes a uma classe).
  Ela diz ao Spring: "procure por controllers, services etc. e ligue o servidor web".
- `SpringApplication.run(...)` é o comando que efetivamente inicia tudo.

### Pasta `model/` — as entidades (o domínio)

**Enums** (`StatusChamado`, `Prioridade`, `CategoriaChamado`) são listas fechadas de valores
possíveis. Um status só pode ser `ABERTO`, `EM_ANDAMENTO` ou `RESOLVIDO` — nada além disso.
Isso evita erros de digitação e deixa claro o que é permitido.

**`Usuario.java`** representa a tabela `usuarios`:

- `@Entity` e `@Table(name = "usuarios")` dizem ao JPA: "esta classe vira uma tabela".
- `@Id` + `@GeneratedValue(strategy = IDENTITY)` → o `id` é a chave primária e o banco o
  gera sozinho (1, 2, 3...).
- `@Column(unique = true)` no e-mail garante que não existam dois usuários com o mesmo e-mail.
- O construtor vazio `protected Usuario()` existe porque o JPA exige um — mas ninguém usa de
  fora. O construtor público `Usuario(nome, email)` é o que usamos para criar um usuário de
  verdade, já com os dados obrigatórios.

**`Chamado.java`** é o coração do DDD neste projeto. Além dos campos (`titulo`, `descricao`,
`status`, datas...), ele contém **comportamento** — as regras vivem aqui dentro:

```java
public void resolver() {
    if (this.status != StatusChamado.EM_ANDAMENTO) {
        throw new TransicaoStatusInvalidaException(
            "Só é possível resolver um chamado que está EM_ANDAMENTO");
    }
    this.status = StatusChamado.RESOLVIDO;
    this.resolvidoEm = LocalDateTime.now();
}
```

- Ninguém consegue "forçar" um chamado para `RESOLVIDO` por fora: o único jeito é chamar
  `resolver()`, e ele **só deixa** se o chamado estiver `EM_ANDAMENTO`. Isso é o **domínio
  rico** protegendo a regra.
- `@ManyToOne` no campo `usuario` representa o relacionamento: **muitos** chamados podem
  pertencer a **um** usuário. No banco isso vira a coluna `usuario_id` (a **chave
  estrangeira**, que aponta para `usuarios.id`).
- Ao criar um `Chamado`, o construtor já o coloca em `ABERTO`, grava `abertoEm = agora` e
  deixa `resolvidoEm = null`. O objeto nasce num estado válido.

### Pasta `repository/` — o acesso ao banco

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
}
```

- É uma **interface** (um contrato sem implementação). O Spring Data JPA **cria o código
  por baixo dos panos** automaticamente.
- Ao herdar de `JpaRepository<Usuario, Long>`, você já ganha de graça: `save`, `findById`,
  `findAll`, `delete` etc. (`Usuario` é o tipo guardado, `Long` é o tipo do id).
- `existsByEmail` é uma **query derivada**: o Spring lê o nome do método e monta sozinho a
  consulta "existe algum usuário com este e-mail?".

No `ChamadoRepository` usamos uma consulta escrita à mão com `@Query` (em JPQL, uma linguagem
parecida com SQL mas baseada nas classes Java). Ela permite **filtros combináveis**: se um
filtro vier nulo, ele é ignorado (`:status IS NULL OR c.status = :status`). Assim um único
método atende a todas as combinações de filtro.

### Pasta `dto/` — os objetos de entrada e saída

**DTO** = *Data Transfer Object* (objeto de transferência de dados). São objetos simples que
representam **só o que entra e o que sai** da API, separados das entidades do banco.

**Por que não enviar a entidade `Chamado` direto no JSON?** Três motivos:

1. **Segurança:** você não expõe campos internos que o cliente não deveria ver/mudar.
2. **Controle:** no `PUT` o usuário só pode editar `titulo`, `descricao`, `categoria` e
   `prioridade` — por isso existe um `ChamadoUpdateRequest` que **nem tem** os campos
   `status`, `usuarioId` ou datas. É impossível editá-los.
3. **Estabilidade:** você pode mudar o banco sem quebrar o formato que os clientes esperam.

Os DTOs são **records** (`public record UsuarioRequest(...)`). Um `record` é uma forma curta
de criar uma classe só de dados, imutável — Java gera construtor, getters e comparações
sozinho. Menos código, mais clareza (Clean Code).

As **validações** ficam nos DTOs de entrada com o **Bean Validation**:

- `@NotBlank` → não pode ser vazio/nulo.
- `@Email` → precisa ter formato de e-mail.
- `@Size(min, max)` → tamanho mínimo e máximo do texto.
- `@NotNull` → não pode ser nulo (usado em enums e no `usuarioId`).

Quando o controller recebe os dados com `@Valid`, essas regras são checadas **antes** de
qualquer lógica. Se falhar, vira `400 Bad Request` automaticamente.

Os DTOs de saída (`UsuarioResponse`, `ChamadoResponse`) têm um método `de(...)` que
**converte a entidade em resposta**. `ChamadoResponse` ainda traz um mini-objeto
`UsuarioResumo` com só `id` e `nome` do usuário — exatamente o que o cliente precisa ver.

### Pasta `service/` — as regras de negócio

O **service** coordena o trabalho. Exemplo do `ChamadoService.abrir`:

```java
public ChamadoResponse abrir(ChamadoRequest request) {
    Usuario usuario = usuarioService.buscarEntidade(request.usuarioId());
    Chamado chamado = new Chamado(request.titulo(), request.descricao(),
                                  request.categoria(), request.prioridade(), usuario);
    return ChamadoResponse.de(chamadoRepository.save(chamado));
}
```

- Primeiro ele **busca o usuário** — se não existir, `buscarEntidade` já lança `404`.
- Cria o `Chamado` (que nasce `ABERTO` por conta própria).
- Salva no banco e devolve a resposta convertida.

Repare: o service **não sabe** as regras de transição de status — ele apenas chama
`chamado.iniciarAtendimento()` ou `chamado.resolver()` e deixa a **entidade** decidir se é
permitido. Essa divisão (service orquestra, entidade guarda a regra) é o espírito do DDD.

A **injeção de dependência** aparece no construtor:

```java
public ChamadoService(ChamadoRepository chamadoRepository, UsuarioService usuarioService) {
    this.chamadoRepository = chamadoRepository;
    this.usuarioService = usuarioService;
}
```

Você não dá `new` nessas dependências; o Spring **as entrega prontas** (porque as classes são
marcadas com `@Service`, `@Repository` etc.). Isso deixa o código desacoplado e **fácil de
testar** (nos testes trocamos o repositório de verdade por um "dublê").

### Pasta `controller/` — a porta HTTP

```java
@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    @PostMapping
    public ResponseEntity<ChamadoResponse> abrir(@Valid @RequestBody ChamadoRequest request) {
        ...
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

- `@RestController` → esta classe responde requisições HTTP devolvendo JSON.
- `@RequestMapping("/chamados")` → todas as rotas desta classe começam com `/chamados`.
- `@PostMapping`, `@GetMapping`, `@PutMapping`, `@PatchMapping` → ligam cada método a um
  verbo HTTP.
- `@RequestBody` → pega o JSON do corpo e vira um objeto Java.
- `@Valid` → dispara as validações do DTO.
- `@PathVariable` → captura o `{id}` da URL (ex.: `/chamados/5`).
- `@RequestParam(required = false)` → captura os filtros opcionais da URL
  (`?status=ABERTO`).
- `ResponseEntity` → permite escolher o código HTTP da resposta (ex.: `201 Created`).

O controller é **fino de propósito**: ele só recebe, chama o service e devolve. Nenhuma regra
de negócio mora aqui.

### Pasta `exception/` — tratando erros com elegância

- `RecursoNaoEncontradoException`, `EmailJaCadastradoException` e
  `TransicaoStatusInvalidaException` são **exceções personalizadas**. "Lançar uma exceção" é
  a forma de o Java dizer "parou, deu problema aqui".
- `GlobalExceptionHandler` é o **tradutor central**: com `@RestControllerAdvice` ele captura
  essas exceções em qualquer lugar da aplicação e transforma cada uma no código HTTP correto:

| Exceção | Vira |
|---|---|
| `RecursoNaoEncontradoException` | `404 Not Found` |
| `EmailJaCadastradoException` | `409 Conflict` |
| `TransicaoStatusInvalidaException` | `409 Conflict` |
| `MethodArgumentNotValidException` (validação do `@Valid`) | `400 Bad Request` |

Assim, em vez de repetir tratamento de erro em cada controller, tudo fica **num só lugar**
(princípio DRY — "não se repita"). A resposta de erro sempre segue o formato
`{ "mensagem": "..." }`, graças ao record `ErroResponse`.

---

## 9. As regras de negócio

**Usuários**

1. Nome: obrigatório, de 3 a 100 caracteres.
2. E-mail: obrigatório, válido e **único** (duplicado → `409`).

**Chamados**

1. `titulo`, `descricao`, `categoria`, `prioridade` e `usuarioId` são obrigatórios.
2. Não dá para abrir chamado para usuário inexistente (`404`).
3. Todo chamado nasce `ABERTO`, com `abertoEm` preenchido e `resolvidoEm = null`.
4. `ABERTO` pode ir para `EM_ANDAMENTO` (ação *iniciar*).
5. `EM_ANDAMENTO` pode ir para `RESOLVIDO` (ação *resolver*).
6. Resolver um chamado ainda `ABERTO` → `409`.
7. Iniciar ou resolver um chamado já `RESOLVIDO` → `409`.
8. Ao resolver, `resolvidoEm` recebe a data/hora do servidor.
9. Filtro sem resultados retorna uma lista vazia `[]`.
10. O `PUT` **não** deixa mudar `status`, `abertoEm`, `resolvidoEm` nem `usuarioId`.

Fluxo de status (não há reabertura no MVP, por simplicidade intencional):

```text
ABERTO ── iniciar ──> EM_ANDAMENTO ── resolver ──> RESOLVIDO
```

---

## 10. Os testes automatizados

Testes automatizados são pequenos programas que **verificam sozinhos** se o código funciona —
você roda e eles dizem "passou" ou "falhou", sem precisar testar tudo na mão.

Usamos **JUnit 5** (o framework de testes) e **Mockito** (para criar "dublês" de dependências).

- `ChamadoTest` testa a **entidade** diretamente: um chamado nasce `ABERTO`; iniciar muda
  para `EM_ANDAMENTO`; resolver preenche a data; resolver um `ABERTO` dá erro; iniciar um
  `RESOLVIDO` dá erro. São as regras do coração do sistema.
- `ChamadoServiceTest` testa o **service** usando mocks: abrir chamado para usuário existente
  retorna status `ABERTO`; abrir para usuário inexistente lança `RecursoNaoEncontradoException`.
  Como o repositório é "dublado" (`@Mock`), o teste roda rápido e sem banco de dados.

Para rodar no IntelliJ: clique com o botão direito na pasta `src/test` → **Run 'All Tests'**.
Pela linha de comando: `mvn test`.

---

## 11. Glossário rápido

| Termo | Em uma frase |
|---|---|
| **API** | A porta de entrada do sistema, acessada por URLs. |
| **REST** | Estilo de API que usa os verbos HTTP (GET, POST...). |
| **Endpoint / rota** | Um endereço específico da API (ex.: `/chamados`). |
| **JSON** | Formato de texto para trocar dados. |
| **Spring Boot** | Framework Java que monta o servidor e a infraestrutura. |
| **JPA / Hibernate** | Ponte entre classes Java e tabelas do banco. |
| **Entidade** | Classe Java que representa uma tabela (ex.: `Chamado`). |
| **Enum** | Lista fechada de valores possíveis (ex.: status). |
| **DTO** | Objeto que define o formato de entrada/saída da API. |
| **Record** | Classe curta e imutável só para guardar dados. |
| **Repository** | Componente que lê e grava no banco. |
| **Service** | Camada que coordena as regras de negócio. |
| **Controller** | Camada que recebe as requisições HTTP. |
| **Injeção de dependência** | O Spring entrega os objetos prontos para você usar. |
| **Anotação (`@`)** | Etiqueta que adiciona comportamento a uma classe/método. |
| **Bean Validation** | As regras `@NotBlank`, `@Email`, `@Size` que validam a entrada. |
| **Exceção** | A forma do Java sinalizar um erro. |
| **Chave estrangeira** | Coluna que liga um registro a outro (ex.: `usuario_id`). |
| **DDD** | Colocar as regras de negócio dentro das entidades do domínio. |
| **Clean Code** | Código claro, com bons nomes e responsabilidades únicas. |

---

Projeto para portfólio: uma API pequena, coerente, com relacionamento entre entidades, regras
de status, validações e testes. Pequena e bem feita — do jeito que uma boa base deve ser.
