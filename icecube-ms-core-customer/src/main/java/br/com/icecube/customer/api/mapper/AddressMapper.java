package br.com.icecube.customer.api.mapper;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Address;
import br.com.icecube.customer.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toModel(AddressDTO addressDTO);

    AddressDTO toDTO(Address address);

}
