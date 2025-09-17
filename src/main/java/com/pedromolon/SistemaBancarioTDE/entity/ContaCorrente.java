package com.pedromolon.SistemaBancarioTDE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contas_corrente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaCorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double saldo;

    private boolean ativa;

    @OneToOne
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @PrePersist
    public void prePersist() {
        this.saldo = 0.0;
        this.ativa = true;
    }

    @Override
    public String toString() {
        return "ContaCorrente{" +
                "id=" + id +
                ", saldo=" + saldo +
                ", ativa=" + ativa +
                '}';
    }

}
