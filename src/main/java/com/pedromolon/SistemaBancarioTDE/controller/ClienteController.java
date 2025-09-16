package com.pedromolon.SistemaBancarioTDE.controller;

import com.pedromolon.SistemaBancarioTDE.dto.request.ClienteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ClienteResponse;
import com.pedromolon.SistemaBancarioTDE.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClienteResponse> findAll() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClienteResponse findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse save(@RequestBody @Valid ClienteRequest request) {
        return clienteService.save(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClienteResponse update(@PathVariable Long id, @RequestBody @Valid ClienteRequest request) {
        return clienteService.update(id, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        clienteService.deleteAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clienteService.delete(id);
    }

    @GetMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.OK)
    public boolean isActive(@PathVariable Long id) {
        return clienteService.isActive(id);
    }

    @GetMapping("/{id}/idade-valida")
    @ResponseStatus(HttpStatus.OK)
    public boolean isAgeValid(@PathVariable Long id) {
        return clienteService.isAgeValid(id);
    }

}
