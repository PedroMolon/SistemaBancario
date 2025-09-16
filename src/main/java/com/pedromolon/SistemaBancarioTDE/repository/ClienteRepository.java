package com.pedromolon.SistemaBancarioTDE.repository;

import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
