# 💰 Gerenciador de Gastos Pessoais

Sistema desktop para controle financeiro pessoal desenvolvido em Java,
com persistência em banco de dados PostgreSQL.

## 📋 Sobre o Projeto

Aplicação de linha de comando que permite gerenciar categorias de gastos,
registrar despesas, efetuar pagamentos e acompanhar relatórios financeiros
mensais com alertas de orçamento.

Projeto desenvolvido como atividade acadêmica na disciplina de
Programação Orientada a Objetos — UNIPAR Cascavel, 2026.

## ✨ Funcionalidades

- 📁 Cadastro de categorias com orçamento mensal
- 💸 Registro de gastos vinculados a categorias
- ✅ Registro e estorno de pagamentos com operação transacional (BEGIN/COMMIT/ROLLBACK)
- 📊 Relatório mensal detalhado com totais
- 📈 Comparativo de gastos por categoria vs orçamento
- 🔔 Alertas quando o orçamento mensal é excedido
- 🔍 Filtragem de gastos por status (PENDENTE, PAGO, CANCELADO)

## 🏗️ Arquitetura

O projeto segue arquitetura em camadas:

- **connection** — Conexão com o banco de dados (padrão Singleton)
- **enums** — StatusGasto e FormaPagamento
- **model** — Entidades: Categoria, Gasto, Pagamento (herdam de BaseModel)
- **repository** — CRUD completo com PostgreSQL via JDBC
- **service** — Regras de negócio e operações transacionais
- **relatorio** — Geração de relatórios financeiros
- **ui** — Interface interativa via terminal (MenuPrincipal)

## 🗄️ Banco de Dados

Três tabelas relacionadas:

- `categorias` — categorias com orçamento mensal
- `gastos` — despesas vinculadas a uma categoria
- `pagamentos` — pagamentos vinculados a um gasto

Relacionamento: `categorias` → `gastos` → `pagamentos`

## 🛠️ Tecnologias

- Java 21
- PostgreSQL
- Maven
- JDBC (sem ORM)
- java-dotenv (proteção de credenciais)

## ⚙️ Como rodar

### Pré-requisitos

- Java 21+
- PostgreSQL instalado e rodando
- Maven

### Passo a passo

**1. Clone o repositório**
git clone https://github.com/seu-usuario/gerenciador-de-gastos.git

**2. Crie o banco de dados e execute o schema**

Abra o pgAdmin, crie o banco gerenciador_de_gastos e execute o arquivo:
src/main/resources/schema.sql

**3. Configure as variáveis de ambiente**

Crie o arquivo .env em src/main/resources/ baseado no .env.example:
DB_URL=jdbc:postgresql://localhost:5432/gerenciador_de_gastos
DB_USER=postgres
DB_PASSWORD=sua_senha_aqui

**4. Rode o projeto**

Pelo IntelliJ: clique com botão direito em Main.java → Run
