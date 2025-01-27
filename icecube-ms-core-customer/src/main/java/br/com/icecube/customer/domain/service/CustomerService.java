package br.com.icecube.customer.domain.service;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Customer;
import org.apache.coyote.BadRequestException;

public interface CustomerService {

    Customer save(CustomerDTO customerDTO) throws BadRequestException;
    Customer updateCustomerAddress(Long customerId, Long addressId, AddressDTO updatedAddressDTO);
}
