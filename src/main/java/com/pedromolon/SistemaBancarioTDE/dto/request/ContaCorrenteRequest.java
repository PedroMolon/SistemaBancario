package com.pedromolon.SistemaBancarioTDE.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ContaCorrenteRequest(

        @PositiveOrZero(message = "Saldo deve ser maior ou igual a zero")
        double saldo,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        boolean ativa,

        @NotNull(message = "Cliente é obrigatório")
        Long clienteId
) {}
