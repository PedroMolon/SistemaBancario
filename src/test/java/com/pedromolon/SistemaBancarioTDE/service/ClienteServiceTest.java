package com.pedromolon.SistemaBancarioTDE.service;

import com.pedromolon.SistemaBancarioTDE.dto.request.ClienteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ClienteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import com.pedromolon.SistemaBancarioTDE.entity.ContaCorrente;
import com.pedromolon.SistemaBancarioTDE.mapper.ClienteMapper;
import com.pedromolon.SistemaBancarioTDE.mapper.ClienteMapperImpl;
import com.pedromolon.SistemaBancarioTDE.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    /*
    Testes de sucesso:
    - salvar:
        salvar com sucesso;
        salvar com 18 anos de idade;
        salvar com 65 anos de idade;
    - buscar todos:
        buscar todos os clientes;
        buscar lista vazia;
    - buscar por id:
        buscar cliente por id com sucesso;
    - atualizar:
        atualiza cliente com sucesso;
    - ativar cliente:
        ativa cliente com sucesso;
        ativa conta quando cliente for ativo e conta estiver inativa;
    - desativar cliente:
        desativa cliente com sucesso;
        desativa conta ao deletar cliente;
        desativa todas contas ativas ao desativar todos clientes;
        não desativa conta ao deletar cliente inativa;
    - desativar todos os clientes:
        desativa todos os clientes com sucesso;
    - verificar idade do cliente:
        verifica se idade esta entre 18 e 65 anos;
        verificar idade igual a 18 anos;
        verificar idade igual a 65 anos;
    - verifica se cliente esta ativo:
        verifica se o cliente esta ativo;
        verifica se o cliente esta inativo;

    Testes de falha:
    - salvar:
        salvar cliente com idade menor que 18 anos;
        salvar cliente com idade maior que 65 anos;
    - buscar por id:
        buscar cliente inexistente;
    - atualizar:
        atualizar cliente inexistente;
        atualizar cliente com idade menor que 18 anos;
        atualizar cliente com idade maior que 65 anos;
    - desativar cliente:
        desativar cliente inexistente
    - verificar idade do cliente:
        verificar idade do cliente inexistente;
    - verifica se cliente esta ativo:
        verificar atividade do cliente inexistente;
     */

    @Mock
    private ClienteRepository clienteRepository;

    @Spy
    private ClienteMapper clienteMapper = new ClienteMapperImpl();

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequest clienteRequest;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        clienteRequest = new ClienteRequest("Pedro Henrique", 22, "pedro@email.com", true);
        clienteSalvo = new Cliente(1L, "Pedro Henrique", 22, "pedro@email.com", true, null);
    }

    // casos de sucesso

    @Test
    void deveSalvarClienteComSucesso() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponse clienteResponse = clienteService.save(clienteRequest);

        assertNotNull(clienteResponse);
        assertNotNull(clienteResponse.id());
        assertEquals("Pedro Henrique", clienteResponse.nome());
        assertEquals(22L, clienteResponse.idade());
        assertEquals("pedro@email.com", clienteResponse.email());
        assertTrue(clienteResponse.ativo());
    }

    @Test
    void deveForcarAtivoAoSalvarMesmoSeRequestVierComoFalso() {
        ClienteRequest request = new ClienteRequest("Pedro Henrique", 22, "pedro@email.com", false);
        clienteSalvo.setAtivo(true);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponse response = clienteService.save(request);

        assertNotNull(response);
        assertTrue(response.ativo());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveSalvarClienteComIdadeDe18AnosComSucesso() {
        clienteRequest = new ClienteRequest("Pedro Henrique", 18, "pedro@email.com", true);
        clienteSalvo.setIdade(18);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponse clienteResponse = clienteService.save(clienteRequest);

        assertNotNull(clienteResponse);
        assertEquals(18, clienteResponse.idade());
    }

    @Test
    void deveSalvarClienteComIdadeDe65AnosComSucesso() {
        clienteRequest = new ClienteRequest("Pedro Henrique", 65, "pedro@email.com", true);
        clienteSalvo.setIdade(65);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponse clienteResponse = clienteService.save(clienteRequest);

        assertNotNull(clienteResponse);
        assertEquals(65, clienteResponse.idade());
    }

    @Test
    void deveRetornarTodosOsClientesComSucesso() {
        List<Cliente> clientes = Collections.singletonList(clienteSalvo);
        when(clienteRepository.findAll()).thenReturn(clientes);

        List<ClienteResponse> response = clienteService.findAll();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());

        List<ClienteResponse> response = clienteService.findAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarClientePorIdComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        ClienteResponse response = clienteService.findById(1L);

        assertNotNull(response);
        assertEquals(clienteSalvo.getId(), response.id());
        assertEquals("Pedro Henrique", response.nome());
        assertEquals(22, response.idade());
        assertEquals("pedro@email.com", response.email());
        assertTrue(response.ativo());
        assertEquals(clienteSalvo.getId(), response.id());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        ClienteRequest requestAtualizado = new ClienteRequest("Pedro", 23, "pedro@email.com", true);
        Cliente clienteAtualizado = new Cliente(1L, "Pedro", 23, "pedro@email.com", true, null);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        ClienteResponse response = clienteService.update(1L, requestAtualizado);

        assertNotNull(response);
        assertEquals("Pedro", response.nome());
        assertEquals(23, response.idade());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveAtivarClienteComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        boolean ativado = clienteService.activate(1L);

        assertTrue(ativado);
        assertTrue(clienteSalvo.isAtivo());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveAtivarContaCorrenteQuandoClienteForAtivadoEContaEstiverInativa() {
        ContaCorrente conta = new ContaCorrente(1L, 500.0, false, clienteSalvo);
        clienteSalvo.setContaCorrente(conta);
        clienteSalvo.setAtivo(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        boolean ativado = clienteService.activate(1L);

        assertTrue(ativado);
        assertTrue(clienteSalvo.isAtivo());
        assertTrue(conta.isAtiva());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveDesativarClienteComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.deactivate(1L);

        assertFalse(clienteSalvo.isAtivo());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveDesativarContaCorrenteAoDeletarCliente() {
        ContaCorrente contaCorrente = new ContaCorrente(1L, 1000.0, true, clienteSalvo);
        clienteSalvo.setContaCorrente(contaCorrente);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.deactivate(1L);

        assertFalse(clienteSalvo.isAtivo());
        assertFalse(contaCorrente.isAtiva());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveDesativarTodasContasAtivasAoDesativarClientes() {
        ContaCorrente conta = new ContaCorrente(1L, 500.0, true, clienteSalvo);
        clienteSalvo.setContaCorrente(conta);
        clienteSalvo.setAtivo(true);
        List<Cliente> clientes = Collections.singletonList(clienteSalvo);

        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteRepository.saveAll(any(List.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.deactivateAll();

        assertFalse(clienteSalvo.isAtivo());
        assertFalse(conta.isAtiva());
        verify(clienteRepository, times(1)).findAll();
        verify(clienteRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void naoDeveAlterarContaCorrenteInativaAoDeletarCliente() {
        ContaCorrente contaCorrente = new ContaCorrente(1L, 0.0, false, clienteSalvo);
        clienteSalvo.setContaCorrente(contaCorrente);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.deactivate(1L);

        assertFalse(clienteSalvo.isAtivo());
        assertFalse(contaCorrente.isAtiva());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(clienteSalvo);
    }

    @Test
    void deveDesativarTodosOsClientesComSucesso() {
        List<Cliente> clientes = Collections.singletonList(clienteSalvo);
        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteRepository.saveAll(any(List.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.deactivateAll();

        clientes.forEach(cliente -> assertFalse(cliente.isAtivo()));
        verify(clienteRepository, times(1)).findAll();
        verify(clienteRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void deveRetornarValidoQuandoIdadeEstiverEntre18E65AnosComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        boolean idadeValida = clienteService.isAgeValid(1L);

        assertTrue(idadeValida);
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarVerdadeiroParaIdadeIgualA18() {
        clienteSalvo.setIdade(18);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        boolean idadeValida = clienteService.isAgeValid(1L);

        assertTrue(idadeValida);
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarVerdadeiroParaIdadeIgualA65() {
        clienteSalvo.setIdade(65);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        boolean idadeValida = clienteService.isAgeValid(1L);

        assertTrue(idadeValida);
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarVerdadeiroQuandoClienteEstiverAtivoComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        boolean ativo = clienteService.isActive(1L);

        assertTrue(ativo);
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarFalsoQuandoClienteEstiverInativo() {
        clienteSalvo.setAtivo(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        boolean ativo = clienteService.isActive(1L);

        assertFalse(ativo);
        verify(clienteRepository, times(1)).findById(1L);
    }

    // casos de falha

    @Test
    void deveLancarExcecaoAoSalvarClienteComIdadeMenorQue18() {
        clienteRequest = new ClienteRequest("Pedro Henrique", 17, "pedro@email.com", true);
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(clienteRequest));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarClienteComIdadeMaiorQue65() {
        clienteRequest = new ClienteRequest("Pedro Henrique", 66, "pedro@email.com", true);
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(clienteRequest));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoForEncontradoComId() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.findById(99L));

        verify(clienteRepository, times(1)).findById(99L);
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.update(99L, clienteRequest));

        verify(clienteRepository, times(1)).findById(99L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteComIdadeMenorQue18() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        ClienteRequest requestComIdadeInvalida = new ClienteRequest("Pedro", 17, "pedro@email.com", true);

        assertThrows(IllegalArgumentException.class, () -> clienteService.update(1L, requestComIdadeInvalida));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteComIdadeMaiorQue65() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        ClienteRequest requestComIdadeInvalida = new ClienteRequest("Pedro", 66, "pedro@email.com", true);

        assertThrows(IllegalArgumentException.class, () -> clienteService.update(1L, requestComIdadeInvalida));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarDesativarClienteInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.deactivate(99L));

        verify(clienteRepository, times(1)).findById(99L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoVerificarIdadeDeClienteInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.isAgeValid(99L));

        verify(clienteRepository, times(1)).findById(99L);
    }

    @Test
    void deveLancarExcecaoQuandoVerificarAtividadeDeClienteInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.isActive(99L));

        verify(clienteRepository, times(1)).findById(99L);
    }

}
