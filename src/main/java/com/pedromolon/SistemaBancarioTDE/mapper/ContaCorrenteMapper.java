package com.pedromolon.SistemaBancarioTDE.mapper;

import com.pedromolon.SistemaBancarioTDE.dto.request.ContaCorrenteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ContaCorrenteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.ContaCorrente;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContaCorrenteMapper {

    ContaCorrente toEntity(ContaCorrenteRequest request);

    @Mapping(source = "cliente.id", target = "clienteId")
    ContaCorrenteResponse toResponse(ContaCorrente contaCorrente);

}
