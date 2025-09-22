package com.pedromolon.SistemaBancarioTDE.controller;

import com.pedromolon.SistemaBancarioTDE.dto.request.ContaCorrenteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.request.TransacaoRequest;
import com.pedromolon.SistemaBancarioTDE.dto.request.TransferenciaRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ContaCorrenteResponse;
import com.pedromolon.SistemaBancarioTDE.service.ContaCorrenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContaCorrenteResponse> findAll() {
        return contaCorrenteService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContaCorrenteResponse findById(@PathVariable Long id) {
        return contaCorrenteService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaCorrenteResponse save(@RequestBody @Valid ContaCorrenteRequest request) {
        return contaCorrenteService.save(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        contaCorrenteService.deactivate(id);
    }

    @PostMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.OK)
    public boolean active(@PathVariable Long id) {
        return contaCorrenteService.activate(id);
    }

    @GetMapping("/{id}/ativa")
    @ResponseStatus(HttpStatus.OK)
    public boolean isActive(@PathVariable Long id) {
        return contaCorrenteService.isActive(id);
    }

    @GetMapping("/{id}/saldo")
    @ResponseStatus(HttpStatus.OK)
    public double getSaldo(@PathVariable Long id) {
        return contaCorrenteService.getSaldo(id);
    }

    @PostMapping("/{idOrigem}/transferir")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@PathVariable Long idOrigem, @RequestBody @Valid TransferenciaRequest request) {
        contaCorrenteService.transfer(idOrigem, request.contaDestino(), request.valor());
    }

    @PostMapping("/{id}/depositar")
    @ResponseStatus(HttpStatus.OK)
    public void deposit(@PathVariable Long id, @RequestBody @Valid TransacaoRequest request) {
        contaCorrenteService.deposit(id, request.valor());
    }

    @PostMapping("/{id}/sacar")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@PathVariable Long id, @RequestBody @Valid TransacaoRequest request) {
        contaCorrenteService.withdraw(id, request.valor());
    }

}
