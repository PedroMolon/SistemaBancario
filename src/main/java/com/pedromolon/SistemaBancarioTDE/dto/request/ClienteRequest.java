package com.pedromolon.SistemaBancarioTDE.dto.request;

import jakarta.validation.constraints.*;

public record ClienteRequest(
        @NotNull(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String nome,

        @NotNull(message = "Idade é obrigatória")
        @Min(value = 18, message = "Idade deve ser no mínimo 18 anos")
        @Max(value = 65, message = "Idade deve ser no máximo 65 anos")
        int idade,

        @Email(message = "E-mail deve ser válido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email,

        boolean ativo
) {}
