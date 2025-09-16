package com.pedromolon.SistemaBancarioTDE.dto.response;

public record ContaCorrenteResponse(
        Long id,
        double saldo,
        boolean ativa,
        Long clienteId
) {}
