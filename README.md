# Sistema Bancário TDE

Este é um projeto de um sistema bancário simples, desenvolvido para a disciplina de Teste de Software.
O projeto foi desenvolvido em Java e utiliza o framework Spring Boot para a construção da API RESTful.
O banco de dados utilizado foi o PostgreSQL via Docker, com migrações gerenciadas pelo Flyway.

## Tecnologias Utilizadas

*   Java 21
*   Spring Boot 3
*   Maven
*   PostgreSQL
*   Flyway
*   JUnit 5
*   Mockito
*   MockMvc

## Funcionalidades

O sistema oferece as seguintes operações bancárias:

* Cadastro e gerenciamento de clientes
* Criação e gerenciamento de contas correntes
* Operações financeiras:
    * Depósito
    * Saque
    * Transferência entre contas
    * Consulta de saldo

## Como Executar

### Pré-requisitos

*   Java 21 ou superior
*   Maven 3.6 ou superior
*   Docker e Docker Compose (para o banco de dados)

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone <url-do-repositorio>
    cd SistemaBancarioTDE
    ```

2.  **Inicie o banco de dados com Docker Compose:**
    ```bash
    docker-compose up -d
    ```

3.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8080`.

### Executando os Testes

Para executar todos os testes, incluindo o cenário de teste de integração, utilize o seguinte comando:

```bash
mvn test
```

## Cenário de Teste (`CenarioTest.java`)

A classe `CenarioTest` contém um teste de integração que simula um fluxo completo de operações no sistema bancário. O objetivo deste teste é garantir que as principais funcionalidades do sistema estão funcionando corretamente em conjunto.

O fluxo de teste é o seguinte:

1.  **Criação de Clientes:** São criados dois clientes, "Cliente A" e "Cliente B".
2.  **Criação de Contas:** São criadas duas contas correntes, uma para cada cliente.
3.  **Depósito:** É depositado o valor de R$ 1000.0 na conta do Cliente A.
4.  **Saque:** É sacado o valor de R$ 200.0 da conta do Cliente A.
5.  **Transferência:** É transferido o valor de R$ 300.0 da conta do Cliente A para a conta do Cliente B.
6.  **Consulta de Saldo:**
    *   O saldo da conta do Cliente A é verificado, e o valor esperado é de R$ 500.0.
    *   O saldo da conta do Cliente B é verificado, e o valor esperado é de R$ 300.0.

Para garantir o isolamento dos testes e a consistência dos dados, a base de dados é limpa antes de cada execução de teste, utilizando `TRUNCATE TABLE` nas tabelas `contas_corrente` e `clientes`.

Durante a execução do teste, são exibidas mensagens no terminal informando o passo a passo do que está acontecendo, facilitando o acompanhamento e a depuração do fluxo.
