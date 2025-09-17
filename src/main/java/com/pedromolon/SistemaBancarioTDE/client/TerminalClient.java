package com.pedromolon.SistemaBancarioTDE.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class TerminalClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {

        while(true) {
            mostrarMenu();
            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    cadastrarCliente();
                    break;
                case 2:
                    buscarTodosClientes();
                    break;
                case 3:
                    buscarClientePorId();
                    break;
                case 4:
                    atualizarCliente();
                    break;
                case 5:
                    desativarCliente();
                    break;
                case 6:
                    desativarTodosClientes();
                    break;
                case 7:
                    verificarSeClienteEstaAtivo();
                    break;
                case 8:
                    verificarSeIdadeEValida();
                    break;
                case 9:
                    criarConta();
                    break;
                case 10:
                    buscarTodasContas();
                    break;
                case 11:
                    buscarContaPorId();
                    break;
                case 12:
                    desativarConta();
                    break;
                case 13:
                    verificarSeContaEstaAtiva();
                    break;
                case 14:
                    verificarSaldoConta();
                    break;
                case 15:
                    transferirEntreContas();
                    break;
                case 16:
                    sacarDaConta();
                    break;
                case 17:
                    depositarNaConta();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

    }

    private static void mostrarMenu() {
        System.out.println("=== Sistema Bancário ===");
        System.out.println("1. Cadastrar Cliente");
        System.out.println("2. Buscar Todos os Clientes");
        System.out.println("3. Buscar Cliente por ID");
        System.out.println("4. Atualizar Cliente");
        System.out.println("5. Desativar Cliente");
        System.out.println("6. Desativar Todos os Clientes");
        System.out.println("7. Verificar se Cliente está Ativo");
        System.out.println("8. Verificar se Idade é Válida");
        System.out.println("9. Criar Conta");
        System.out.println("10. Buscar Todas as Contas");
        System.out.println("11. Buscar Conta por ID");
        System.out.println("12. Desativar Conta");
        System.out.println("13. Verificar se Conta está Ativa");
        System.out.println("14. Verificar Saldo da Conta");
        System.out.println("15. Transferir entre Contas");
        System.out.println("16. Sacar da Conta");
        System.out.println("17. Depositar na Conta");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // OPÇÕES RELACIONADAS A CLIENTE
    private static void cadastrarCliente() throws IOException, InterruptedException {
        System.out.println("Digite o nome do cliente: ");
        String nome = scanner.nextLine();

        System.out.println("Digite a idade do cliente: ");
        int idade = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Digite o email do cliente: ");
        String email = scanner.nextLine();

        boolean ativo = true;

        String jsonRequest = String.format("""
                {
                    "nome": "%s",
                    "idade": %d,
                    "email": "%s",
                    "ativo": %b
                }
                """, nome, idade, email, ativo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void buscarTodosClientes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void buscarClientePorId() throws IOException, InterruptedException {
        System.out.println("Digite o ID do cliente: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void atualizarCliente() throws IOException, InterruptedException {
        System.out.println("Digite o ID do cliente a ser atualizado: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Digite o novo nome do cliente: ");
        String nome = scanner.nextLine();

        System.out.println("Digite a nova idade do cliente: ");
        int idade = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Digite o novo email do cliente: ");
        String email = scanner.nextLine();

        System.out.println("O cliente está ativo? (true/false): ");
        boolean ativo = scanner.nextBoolean();
        scanner.nextLine();

        String jsonRequest = String.format("""
                {
                    "nome": "%s",
                    "idade": %d,
                    "email": "%s",
                    "ativo": %b
                }
                """, nome, idade, email, ativo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void desativarCliente() throws IOException, InterruptedException {
        System.out.println("Digite o ID do cliente a ser desativado: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void desativarTodosClientes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void verificarSeClienteEstaAtivo() throws IOException, InterruptedException {
        System.out.println("Digite o ID do cliente para verificar se está ativo: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes/" + id + "/ativo"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void verificarSeIdadeEValida() throws IOException, InterruptedException {
        System.out.println("Digite o id para verificar se a idade é válida: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clientes/" + id + "/idade-valida"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    // OPÇÕES RELACIONADAS A CONTA
    private static void criarConta() throws IOException, InterruptedException {
        System.out.println("Digite o ID do cliente para associar à conta: ");
        long clienteId = scanner.nextLong();
        scanner.nextLine();

        String jsonRequest = String.format("""
                {
                    "clienteId": %d
                }
                """, clienteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void buscarTodasContas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void buscarContaPorId() throws IOException, InterruptedException {
        System.out.println("Digite o Id da conta: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void desativarConta() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta a ser desativada: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void verificarSeContaEstaAtiva() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta para verificar se está ativa: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + id + "/ativa"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void verificarSaldoConta() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta para verificar o saldo: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + id + "/saldo"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void transferirEntreContas() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta de origem: ");
        long idOrigem = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Digite o ID da conta de destino: ");
        long idDestino = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Digite o valor a ser transferido: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        String jsonRequest = String.format("""
                {
                    "idOrigem": %d,
                    "idDestino": %d,
                    "valor": %.2f
                }
                """, idOrigem, idDestino, valor);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/transferir"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void sacarDaConta() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta para saque: ");
        long contaId = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Digite o valor a ser sacado: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        String jsonRequest = String.format("""
                {
                    "valor": %.2f
                }
                """, valor);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + contaId + "/sacar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }

    private static void depositarNaConta() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta para depósito: ");
        long contaId = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Digite o valor a ser depositado: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        String jsonRequest = String.format("""
                {
                    "valor": %.2f
                }
                """, valor);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/contas/" + contaId + "/depositar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta do servidor: " + response.statusCode());
        System.out.println("Corpo da resposta: " + response.body());
    }
}
