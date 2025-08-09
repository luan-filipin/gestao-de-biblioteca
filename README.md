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

MapStruct

JUnit 5

Mockito

Maven

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
- Informar o email na url para buscar o usuario.
- `GET /api/usuarios?email=email@teste.com`
  
Deleta usuario.
- Informar o email na url para deletar o usuario.
- `DELETE /api/usuarios?email=email@teste.com`

Atualiza usuario.
- Informar o email na url
- Apenas os campos abaixo são permitidos atualizar.
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
- Informar o isbn para buscar o livro.
- `GET /api/livros?isbn=9780132350884`

Deleta Livro.
- Informar o isbn na url para deletar o livro.
- `DELETE /api/livros?isbn=9780132350884`

Atualizar Livro.
- Necessario passar o isbn na url
- Apenas os campos abaixo são permitidos atualizar.
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
- Recomenda livros com base na categoria ja emprestada.
- `POST /api/livros/recomendacao?email=email@teste.com`

Busca livros no Google API Books.
- Mostra uma lista de livros com o titulo pesquisado.
- `GET /api/livros/google/buscar?titulo=Java`

Salvar livros do Google API Books no banco PostgreSQL.
- O pesquisa os livros com base no titulo e salva todos no banco.
- `POST /api/livros/google/salvar?titulo=Java`

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
- Informar o id na url.
- Apenas os campos abaixo são permitidos atualizar.
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

## 📌 Requisitos
Java 21

Maven

PostgreSQL (com banco de dados previamente criado)

## ⚙️ Como executar o projeto
1. Clone o repositorio.
```
git clone https://github.com/luan-filipin/gestao-de-biblioteca.git
```

2. Criar o banco de dados (biblioteca).

3. Configure o banco de dados no application.properties, ajuste com as suas credenciais:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

4. Como o Application.properties ja possui essa configuração abaixo ao executar a aplicação as tabelas seram criadas automaticamente.
```
spring.jpa.hibernate.ddl-auto=update
```
