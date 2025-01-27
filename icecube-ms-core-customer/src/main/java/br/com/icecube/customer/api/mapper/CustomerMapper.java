package br.com.icecube.customer.api.mapper;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Address;
import br.com.icecube.customer.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "legalName", expression = "java(LegalName.of(customerDTO.legalName()))")
    @Mapping(target = "document", expression = "java(Document.of(customerDTO.document()))")
    @Mapping(target = "address", expression = "java(mapAddressDTOs(customerDTO.address()))")
    Customer toModel(CustomerDTO customerDTO);

    default List<Address> mapAddressDTOs(List<AddressDTO> addressDTOs) {
        return addressDTOs.stream()
                .map(addressDTO -> Address.builder()
                        .street(addressDTO.street())
                        .number(addressDTO.number())
                        .city(addressDTO.city())
                        .zipcode(addressDTO.zipcode())
                        .build())
                .toList();
    }

}
