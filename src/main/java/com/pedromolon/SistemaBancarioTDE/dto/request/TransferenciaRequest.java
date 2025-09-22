package com.pedromolon.SistemaBancarioTDE.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferenciaRequest(
        @NotNull(message = "A conta de destino é obrigatória.")
        Long contaDestino,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor da transferência deve ser positivo.")
        Double valor
) {}
