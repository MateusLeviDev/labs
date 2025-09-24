package br.com.icecube.mapper;

import br.com.icecube.domain.SalesInfo;
import br.com.icecube.dto.SalesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.ERROR )
public interface SalesMapper {

    SalesInfo mapToEntity(SalesDTO salesDTO);
}
