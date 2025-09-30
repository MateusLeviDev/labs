package br.com.icecube.domain.mapper;

import br.com.icecube.domain.model.SalesInfo;
import br.com.icecube.domain.dto.SalesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.ERROR )
public interface SalesMapper {

    SalesInfo mapToEntity(SalesDTO salesDTO);
}
