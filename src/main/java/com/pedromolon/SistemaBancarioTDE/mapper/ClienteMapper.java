package com.pedromolon.SistemaBancarioTDE.mapper;

import com.pedromolon.SistemaBancarioTDE.dto.request.ClienteRequest;
import com.pedromolon.SistemaBancarioTDE.dto.response.ClienteResponse;
import com.pedromolon.SistemaBancarioTDE.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

}
