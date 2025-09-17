package com.pedromolon.SistemaBancarioTDE.service;

import com.pedromolon.SistemaBancarioTDE.dto.request.ContaCorrenteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ContaCorrenteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import com.pedromolon.SistemaBancarioTDE.entity.ContaCorrente;
import com.pedromolon.SistemaBancarioTDE.mapper.ContaCorrenteMapper;
import com.pedromolon.SistemaBancarioTDE.repository.ClienteRepository;
import com.pedromolon.SistemaBancarioTDE.repository.ContaCorrenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;
    private final ContaCorrenteMapper contaCorrenteMapper;
    private final ClienteRepository clienteRepository;

    public List<ContaCorrenteResponse> findAll() {
        return contaCorrenteRepository.findAll()
                .stream()
                .map(contaCorrenteMapper::toResponse)
                .toList();
    }

    public ContaCorrenteResponse findById(Long id) {
        return contaCorrenteRepository.findById(id)
                .map(contaCorrenteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente não encontrada com o id: " + id));
    }

    public ContaCorrenteResponse save(ContaCorrenteRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + request.clienteId()));

        if (!cliente.isAtivo()) {
            throw new IllegalStateException("Não é possível criar conta para um cliente inativo.");
        }

        if (cliente.getContaCorrente() != null) {
            throw new IllegalStateException("O cliente já possui uma conta corrente.");
        }

        ContaCorrente conta = contaCorrenteMapper.toEntity(request);
        conta.setCliente(cliente);
        conta.setAtiva(true);
        conta.setSaldo(0.0);

        return contaCorrenteMapper.toResponse(contaCorrenteRepository.save(conta));
    }

    public void delete(Long id) {
        ContaCorrente conta = contaCorrenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente não encontrada com o id: " + id));

        if (conta.getSaldo() == 0) {
            conta.setAtiva(false);
            contaCorrenteRepository.save(conta);
        } else {
            throw new IllegalStateException("Conta corrente não deve possuir saldo para ser desativada!");
        }
    }

    public boolean isActive(Long id) {
        ContaCorrente conta = contaCorrenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta corrente não encontrada com o id: " + id));
        return conta.isAtiva();
    }

    public Double getSaldo(Long id) {
        ContaCorrente conta = contaCorrenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta corrente não encontrada com o id: " + id));
        return conta.getSaldo();
    }

    @Transactional
    public void transferir(Long idOrigem, Long idDestino, Double valor) {
        if (idOrigem.equals(idDestino)) {
            throw new IllegalArgumentException("Conta de origem e destino não podem ser as mesmas.");
        }

        ContaCorrente contaOrigem = contaCorrenteRepository.findById(idOrigem)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente de origem não encontrada com o id: " + idOrigem));

        ContaCorrente contaDestino = contaCorrenteRepository.findById(idDestino)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente de destino não encontrada com o id: " + idDestino));

        if (!contaOrigem.isAtiva()) {
            throw new IllegalStateException("A conta de origem não está ativa.");
        }

        if (!contaDestino.isAtiva()) {
            throw new IllegalStateException("A conta de destino não está ativa.");
        }

        if (valor <= 0 || contaOrigem.getSaldo() < valor) {
            throw new IllegalStateException("Saldo insuficiente ou valor inválido para transferência.");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo() - valor);
        contaDestino.setSaldo(contaDestino.getSaldo() + valor);

        contaCorrenteRepository.save(contaOrigem);
        contaCorrenteRepository.save(contaDestino);
    }

    @Transactional
    public void depositar(Long contaId, Double valor) {
        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente não encontrada com o id: " + contaId));

        if (!conta.isAtiva()) {
            throw new IllegalStateException("A conta de destino não está ativa.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }

        conta.setSaldo(conta.getSaldo() + valor);
        contaCorrenteRepository.save(conta);
    }

    @Transactional
    public void sacar(Long contaId, Double valor) {
        ContaCorrente conta = contaCorrenteRepository.findById(contaId)
                .orElseThrow(() -> new EntityNotFoundException("Conta Corrente não encontrada com o id: " + contaId));

        if (!conta.isAtiva()) {
            throw new IllegalStateException("A conta de origem não está ativa.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }

        if (conta.getSaldo() < valor) {
            throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
        }

        conta.setSaldo(conta.getSaldo() - valor);
        contaCorrenteRepository.save(conta);
    }

}
