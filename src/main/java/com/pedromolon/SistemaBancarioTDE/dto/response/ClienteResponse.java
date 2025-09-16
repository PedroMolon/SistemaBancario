package com.pedromolon.SistemaBancarioTDE.dto.response;

public record ClienteResponse(
        Long id,
        String nome,
        int idade,
        String email,
        boolean ativo,
        Integer contaId
) {}
