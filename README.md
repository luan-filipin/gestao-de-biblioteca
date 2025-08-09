## 📚 Gestão de Biblioteca
Este projeto é uma API REST desenvolvida em Java com Spring Boot, que simula o gerenciamento de uma biblioteca. É possível cadastrar usuários, livros, realizar empréstimos, devoluções e obter recomendações personalizadas de livros com base em categorias.

## ✅ Funcionalidades
📖 Livros: CRUD completo.

👤 Usuários: CRUD completo.

📦 Empréstimo: Cria e Atualiza.

🔍 Validações de regras de negócio:

Livro só pode ser emprestado se não estiver ativo em outro empréstimo

Datas de devolução não podem ser retroativas

💡 Recomendação de livros com base nas categorias que o usuário já leu

❌ Tratamento global de exceções com mensagens claras e padronizadas

🧪 Testes unitários e de integração com JUnit 5 e Mockito

## 🛠️ Tecnologias utilizadas
Java 21

Spring Boot

Spring Web

Spring Data JPA

Hibernate Validator (Bean Validation)

PostgreSQL

Lombok

JUnit 5

Mockito

Maven

## 📌 Requisitos
Java 17+

Maven

PostgreSQL (com banco de dados previamente criado)

## 📡Endopoints Usuario:
Cadastrar usuario.
- `POST /api/usuarios`
```
{
    "nome": "usuario",
    "email": "email@teste.com",
    "telefone": "(11) 91284-4678"
}
```

Pesquisa usuario.
- `GET /api/usuarios?email=email@teste.com`
  
Deleta usuario.
- `DELETE /api/usuarios?email=email@teste.com`

Atualiza usuario.
- `PUT /api/usuarios?email=email@teste.com`
```
{
    "nome": "usuario",
    "telefone": "(11) 91284-4678"
}
```
## 📡Endopoints Livros:
Cadastrar Livro.
- `POST /api/livros`
```
{
  "titulo": "Estruturas de Dados e Algoritmos em Java",
  "autor": "Michael T. Goodrich e Roberto Tamassia",
  "isbn": "9788536513725",
  "categoria": "Algoritmos",
  "dataPublicacao": "2014-09-15"
}
```
Pesquisa Livro.
- `GET /api/livros?isbn=9780132350884`

Deleta Livro.
- `DELETE /api/livros?isbn=9780132350884`

Atualizar Livro.
- `PUT /api/livros?isbn=9780132350884`
```
{
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "categoria": "Programação",
  "dataPublicacao": "2025-08-07"
}
```
Recomendações Livro.
- `POST /api/livros/recomendacao?email=email@teste.com`


## 📡Endopoints Livros:
Cadastrar Emprestimo.
- `POST /api/emprestimos`
```
{
  "livroId": 1,
  "usuarioId": 2,
  "dataDevolucao": "2025-08-08"
}
```

Atualizar Emprestimo.
- `PUT /api/emprestimos?id=2`
```
{
  "dataDevolucao": "2025-08-08",
  "status": false
}
```
## 🗃️ Estrutura do Banco de Dados
Abaixo está a estrutura atual das tabelas utilizadas na aplicação:
```
-- Tabela de usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de livros
CREATE TABLE livros (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    data_publicacao DATE NOT NULL,
    categoria VARCHAR(100) NOT NULL
);

-- Tabela de empréstimos
CREATE TABLE emprestimos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    livro_id BIGINT NOT NULL,
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_devolucao DATE NOT NULL,
    status BOOLEAN NOT NULL,
    
);

```

## ⚙️ Como executar o projeto
1. Clone o repositorio.
```
git clone https://github.com/luan-filipin/gestao-de-biblioteca.git
```

2. Configure o banco de dados
No application.properties ou application.yml, ajuste com as suas credenciais:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. Execute a aplicação
