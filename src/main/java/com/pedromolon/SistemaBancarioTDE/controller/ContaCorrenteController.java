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
    public void delete(@PathVariable Long id) {
        contaCorrenteService.delete(id);
    }

    @GetMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.OK)
    public boolean isActive(@PathVariable Long id) {
        return contaCorrenteService.isActive(id);
    }

    @PostMapping("/{idOrigem}/transferir")
    @ResponseStatus(HttpStatus.OK)
    public void transferir(@PathVariable Long idOrigem, @RequestBody @Valid TransferenciaRequest request) {
        contaCorrenteService.transferir(idOrigem, request.contaDestino(), request.valor());
    }

    @PostMapping("/{id}/depositar")
    @ResponseStatus(HttpStatus.OK)
    public void depositar(@PathVariable Long id, @RequestBody @Valid TransacaoRequest request) {
        contaCorrenteService.depositar(id, request.valor());
    }

    @PostMapping("/{id}/sacar")
    @ResponseStatus(HttpStatus.OK)
    public void sacar(@PathVariable Long id, @RequestBody @Valid TransacaoRequest request) {
        contaCorrenteService.sacar(id, request.valor());
    }

}
