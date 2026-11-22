# Sistema de Empréstimo de Equipamentos

API REST em Java com Spring Boot para controlar o empréstimo de equipamentos (notebooks, monitores, mouses etc.) a colaboradores, com front end em HTML, CSS e JavaScript.

**Aplicação no ar:** https://emprestimos-w8qy.onrender.com

> O Render pode "hibernar" a aplicação quando ela fica um tempo sem uso. A primeira requisição pode levar cerca de um minuto para responder. Depois disso, ela funciona normalmente.

## Tecnologias

- Java e Spring Boot (Web, Data JPA, Validation)
- Banco de dados H2
- JUnit 5 (testes unitários)
- Front end em HTML, CSS e JavaScript puro, servido pelo próprio Spring (`src/main/resources/static/index.html`)

## Como rodar

### Pré-requisitos

- JDK 25

### Passo a passo

1. Clone o repositório e entre na pasta do projeto.
2. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

   No Windows, use `mvnw.cmd spring-boot:run`. Também é possível abrir o projeto no IntelliJ e executar a classe principal (a que tem `@SpringBootApplication`).
3. Acesse **http://localhost:8080** para usar a interface.
4. A API responde em **http://localhost:8080/equipamentos**, **/colaboradores** e **/emprestimos**.

### Rodar os testes

```bash
./mvnw test
```

Ou, no IntelliJ: botão direito em `src/test/java` e `Run 'All Tests'`.

## Rotas da API

### Equipamentos

| Método | Rota | O que faz | Status |
|--------|------|-----------|--------|
| GET | `/equipamentos` | Lista equipamentos. Filtro opcional: `?tipo=NOTEBOOK` | 200 |
| GET | `/equipamentos/{id}` | Busca um equipamento | 200 |
| POST | `/equipamentos` | Cadastra um equipamento | 201 |
| PUT | `/equipamentos/{id}` | Atualiza nome e tipo | 200 |
| DELETE | `/equipamentos/{id}` | Remove (só se nunca teve empréstimo) | 204 |

Exemplo de corpo para `POST /equipamentos`:

```json
{
  "patrimonio": "PAT-001",
  "nome": "Notebook Dell",
  "tipo": "NOTEBOOK"
}
```

Tipos aceitos: `NOTEBOOK`, `MONITOR`, `MOUSE`, `TECLADO`, `HEADSET`, `OUTRO`.

### Colaboradores

| Método | Rota | O que faz | Status |
|--------|------|-----------|--------|
| POST | `/colaboradores` | Cadastra um colaborador | 201 |
| GET | `/colaboradores` | Lista colaboradores | 200 |
| GET | `/colaboradores/{id}` | Busca um colaborador | 200 |
| GET | `/colaboradores/{id}/emprestimos` | Histórico de empréstimos da pessoa | 200 |

Exemplo de corpo para `POST /colaboradores`:

```json
{
  "nome": "Ana Souza",
  "email": "ana@empresa.com",
  "cargo": "Analista"
}
```

### Empréstimos

| Método | Rota | O que faz | Status |
|--------|------|-----------|--------|
| POST | `/emprestimos` | Registra um empréstimo (ids enviados no corpo JSON) | 201 |
| GET | `/emprestimos/emprestimos/atrasados` | Lista os empréstimos. Com `?atrasados=true`, só os atrasados | 200 |
| GET | `/emprestimos/{id}` | Busca um empréstimo | 200 |
| PUT | `/emprestimos/{id}/devolucao` | Registra a devolução | 200 |

Exemplo de corpo para `POST /emprestimos`:

```json
{
  "equipamentoId": 1,
  "colaboradorId": 2
}
```

As datas não são enviadas pelo cliente: o sistema define a data do empréstimo como hoje e a devolução prevista para 14 dias depois.

## Regras de negócio

| Regra | Descrição |
|-------|-----------|
| R1 | Não existem dois equipamentos com o mesmo patrimônio |
| R2 | Todo equipamento novo nasce disponível |
| R3 | Não existem dois colaboradores com o mesmo e-mail |
| R4 | Só se empresta equipamento disponível |
| R5 | Ao emprestar, o equipamento fica indisponível e a devolução é prevista para 14 dias depois |
| R6 | Um empréstimo já devolvido não pode ser devolvido de novo |
| R7 | Ao devolver, o equipamento volta a ficar disponível |
| R8 | O empréstimo está atrasado quando não foi devolvido e a data prevista já passou |
| R9 | Equipamento que já teve empréstimo não pode ser removido |
| R10 | Quem tem empréstimo atrasado não pode pegar outro equipamento |

Quando uma regra é quebrada, a API responde com erro e a mensagem explicando o motivo.

## Estrutura do projeto

```
src/main/java/com/projeto/emprestimo/
├── model/        entidades (Equipamento, Colaborador, Emprestimo) e enum TipoEquipamento
├── repository/   acesso ao banco (Spring Data JPA)
├── service/      regras de negócio
├── controller/   rotas REST
└── dto/          objetos de requisição
src/main/resources/static/   front end (index.html)
src/test/java/               testes unitários (EquipamentoTest, EmprestimoTest)
```
