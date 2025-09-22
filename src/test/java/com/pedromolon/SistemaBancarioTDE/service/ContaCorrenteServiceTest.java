package com.pedromolon.SistemaBancarioTDE.service;

import com.pedromolon.SistemaBancarioTDE.dto.request.ContaCorrenteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ContaCorrenteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import com.pedromolon.SistemaBancarioTDE.entity.ContaCorrente;
import com.pedromolon.SistemaBancarioTDE.mapper.ContaCorrenteMapper;
import com.pedromolon.SistemaBancarioTDE.mapper.ContaCorrenteMapperImpl;
import com.pedromolon.SistemaBancarioTDE.repository.ClienteRepository;
import com.pedromolon.SistemaBancarioTDE.repository.ContaCorrenteRepository;
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
public class ContaCorrenteServiceTest {

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Spy
    private ContaCorrenteMapper contaCorrenteMapper = new ContaCorrenteMapperImpl();

    @InjectMocks
    private ContaCorrenteService contaCorrenteService;

    private ContaCorrenteRequest contaCorrenteRequest;
    private ContaCorrente contaCorrenteSalva;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Pedro Henrique1", 22, "pedro@email.com", true, null);
        contaCorrenteRequest = new ContaCorrenteRequest(1000.0, true, 1L);
        contaCorrenteSalva = new ContaCorrente(1L, 1000.0, true, cliente);
    }

    // casos de sucesso

    @Test
    void deveSalvarContaCorrenteComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaCorrenteRepository.save(any(ContaCorrente.class))).thenAnswer(i -> i.getArgument(0));

        ContaCorrenteResponse contaCorrenteResponse = contaCorrenteService.save(contaCorrenteRequest);

        assertNotNull(contaCorrenteResponse);
        assertEquals(0.0, contaCorrenteResponse.saldo());
        assertTrue(contaCorrenteResponse.ativa());
    }

    @Test
    void deveRetornarTodasContasCorrentesComSucesso() {
        List<ContaCorrente> contasCorrente = Collections.singletonList(contaCorrenteSalva);
        when(contaCorrenteRepository.findAll()).thenReturn(contasCorrente);

        List<ContaCorrenteResponse> contasCorrenteResponse = contaCorrenteService.findAll();

        assertNotNull(contasCorrenteResponse);
        assertFalse(contasCorrenteResponse.isEmpty());
        assertEquals(1, contasCorrenteResponse.size());
        verify(contaCorrenteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverContas() {
        when(contaCorrenteRepository.findAll()).thenReturn(new ArrayList<>());

        List<ContaCorrenteResponse> response = contaCorrenteService.findAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(contaCorrenteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarContaCorrentePorIdComSucesso() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        ContaCorrenteResponse contaCorrenteResponse = contaCorrenteService.findById(1L);

        assertNotNull(contaCorrenteResponse);
        assertEquals(1L, contaCorrenteResponse.id());
        assertEquals(1000.0, contaCorrenteResponse.saldo());
        assertTrue(contaCorrenteResponse.ativa());
        verify(contaCorrenteRepository, times(1)).findById(1L);
    }

    @Test
    void deveDesativarContaCorrenteComSucesso() {
        contaCorrenteSalva.setSaldo(0.0);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.deactivate(1L);

        assertFalse(contaCorrenteSalva.isAtiva());
        verify(contaCorrenteRepository, times(1)).save(contaCorrenteSalva);
    }

    @Test
    void deveAtivarContaCorrenteComSucesso() {
        contaCorrenteSalva.setAtiva(false);
        contaCorrenteSalva.setSaldo(1000.0);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.activate(1L);

        assertTrue(contaCorrenteSalva.isAtiva());
        verify(contaCorrenteRepository, times(1)).save(contaCorrenteSalva);
    }

    @Test
    void deveRetornarVerdadeiroQuandoContaEstiverAtivaComSucesso() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        boolean isAtiva = contaCorrenteService.isActive(1L);

        assertTrue(isAtiva);
        verify(contaCorrenteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarFalsoQuandoContaEstiverInativa() {
        contaCorrenteSalva.setAtiva(false);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        boolean isAtiva = contaCorrenteService.isActive(1L);

        assertFalse(isAtiva);
        verify(contaCorrenteRepository, times(1)).findById(1L);
    }

    @Test
    void deveTransferirComSucesso() {
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, true, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        contaCorrenteService.transfer(1L, 2L, 500.0);

        assertEquals(500.0, contaCorrenteSalva.getSaldo());
        assertEquals(1500.0, contaDestino.getSaldo());
        verify(contaCorrenteRepository, times(2)).save(any(ContaCorrente.class));
    }

    @Test
    void deveDepositarComSucesso() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.deposit(1L, 500.0);

        assertEquals(1500.0, contaCorrenteSalva.getSaldo());
        verify(contaCorrenteRepository, times(1)).save(any(ContaCorrente.class));
    }

    @Test
    void deveSacarComSucesso() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.withdraw(1L, 500.0);

        assertEquals(500.0, contaCorrenteSalva.getSaldo());
        verify(contaCorrenteRepository, times(1)).save(any(ContaCorrente.class));
    }

    @Test
    void deveSacarSaldoTotalComSucesso() {
        contaCorrenteSalva.setSaldo(1000.0);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.withdraw(1L, 1000.0);

        assertEquals(0.0, contaCorrenteSalva.getSaldo());
        verify(contaCorrenteRepository, times(1)).save(any(ContaCorrente.class));
    }

    @Test
    void deveRetornarSaldoTotalComSucesso() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        contaCorrenteService.getSaldo(1L);

        verify(contaCorrenteRepository, times(1)).findById(1L);
    }

    // casos de falha

    @Test
    void deveLancarExcecaoQuandoClienteJaTiverContaAtiva() {
        cliente.setContaCorrente(contaCorrenteSalva);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.save(contaCorrenteRequest));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoQuandoSalvarContaCorrenteComClienteInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.save(contaCorrenteRequest));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarContaParaClienteInativo() {
        cliente.setAtivo(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.save(contaCorrenteRequest));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoQuandoContaCorrenteNaoForEncontradaPorId() {
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.findById(1L));
        verify(contaCorrenteRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoTentarDeletarContaInexistente() {
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.deactivate(99L));
        verify(contaCorrenteRepository, times(1)).findById(99L);
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoQuandoVerificarAtividadeDeContaInexistente() {
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.isActive(99L));
        verify(contaCorrenteRepository, times(1)).findById(99L);
    }

    @Test
    void deveLancarExcecaoAoSacarValorMaiorQueSaldo() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.withdraw(1L, 1500.0));

        assertEquals(1000.0, contaCorrenteSalva.getSaldo());
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoSacarValorZero() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalArgumentException.class, () -> contaCorrenteService.withdraw(1L, 0.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoSacarDeContaInexistente() {
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.withdraw(99L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirComSaldoInsuficiente() {
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, true, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.transfer(1L, 2L, 1500.0));

        assertEquals(1000.0, contaCorrenteSalva.getSaldo());
        assertEquals(1000.0, contaDestino.getSaldo());
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirValorNegativo() {
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, true, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.transfer(1L, 2L, -100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirValorZero() {
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, true, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.transfer(1L, 2L, 0.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirDeContaInexistente() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.transfer(1L, 2L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirParaContaInexistente() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.transfer(1L, 2L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirDeContaInativa() {
        contaCorrenteSalva.setAtiva(false);
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, true, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.transfer(1L, 2L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirParaContaInativa() {
        ContaCorrente contaDestino = new ContaCorrente(2L, 1000.0, false, cliente);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));
        when(contaCorrenteRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.transfer(1L, 2L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirParaAMesmaConta() {
        assertThrows(IllegalArgumentException.class, () -> contaCorrenteService.transfer(1L, 1L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoDepositarValorNegativo() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalArgumentException.class, () -> contaCorrenteService.deposit(1L, -100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoDepositarValorZero() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalArgumentException.class, () -> contaCorrenteService.deposit(1L, 0.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoDepositarEmContaInexistente() {
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contaCorrenteService.deposit(99L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarOperacaoEmContaInativa() {
        contaCorrenteSalva.setAtiva(false);
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.withdraw(1L, 100.0));
        assertThrows(IllegalStateException.class, () -> contaCorrenteService.deposit(1L, 100.0));
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoDesativarContaComSaldoPositivo() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalStateException.class, () -> contaCorrenteService.deactivate(1L));

        assertTrue(contaCorrenteSalva.isAtiva());
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarSacarValorNegativo() {
        when(contaCorrenteRepository.findById(1L)).thenReturn(Optional.of(contaCorrenteSalva));

        assertThrows(IllegalArgumentException.class, () -> contaCorrenteService.withdraw(1L, -100.0));

        assertEquals(1000.0, contaCorrenteSalva.getSaldo());
        verify(contaCorrenteRepository, never()).save(any(ContaCorrente.class));
    }

}
