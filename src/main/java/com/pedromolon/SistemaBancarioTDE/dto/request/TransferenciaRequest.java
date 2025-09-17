package com.pedromolon.SistemaBancarioTDE.dto.request;

public record TransferenciaRequest(
        Long contaDestino,
        Double valor
) {}
