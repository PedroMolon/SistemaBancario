package com.pedromolon.SistemaBancarioTDE.service;

import com.pedromolon.SistemaBancarioTDE.dto.request.ClienteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ClienteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import com.pedromolon.SistemaBancarioTDE.mapper.ClienteMapper;
import com.pedromolon.SistemaBancarioTDE.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    public ClienteResponse findById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + id));
    }

    public ClienteResponse save(ClienteRequest request) {
        Cliente cliente = clienteMapper.toEntity(request);

        if (cliente.getIdade() < 18 || cliente.getIdade() > 65) {
            throw new IllegalArgumentException("A idade do cliente deve estar entre 18 e 65 anos.");
        }

        cliente.setAtivo(true);

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + id));

        if (request.idade() < 18 || request.idade() > 65) {
            throw new IllegalArgumentException("A idade do cliente deve estar entre 18 e 65 anos.");
        }

        cliente.setNome(request.nome());
        cliente.setIdade(request.idade());
        cliente.setEmail(request.email());
        cliente.setAtivo(request.ativo());

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + id));
        cliente.setAtivo(false);
        if (cliente.getContaCorrente() != null && cliente.getContaCorrente().isAtiva()) {
            cliente.getContaCorrente().setAtiva(false);
        }
        clienteRepository.save(cliente);
    }

    public void deleteAll() {
        clienteRepository.deleteAll();
    }

    public boolean isActive(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + id));
        return cliente.isAtivo();
    }

    public boolean isAgeValid(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o id: " + id));
        return cliente.getIdade() >= 18 && cliente.getIdade() <= 65;
    }

}
