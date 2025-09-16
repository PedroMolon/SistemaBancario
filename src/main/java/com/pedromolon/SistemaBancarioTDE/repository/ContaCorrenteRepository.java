package com.pedromolon.SistemaBancarioTDE.repository;

import com.pedromolon.SistemaBancarioTDE.entity.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long> {
}
