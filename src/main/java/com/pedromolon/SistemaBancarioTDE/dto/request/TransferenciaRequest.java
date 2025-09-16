package com.pedromolon.SistemaBancarioTDE.dto.request;

public record TransferenciaRequest(
        Long contaOrigem,
        Long contaDestino,
        Double valor
) {}
