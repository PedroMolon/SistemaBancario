package com.pedromolon.SistemaBancarioTDE.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoRequest(
        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor da transação deve ser positivo.")
        Double valor
) {}

