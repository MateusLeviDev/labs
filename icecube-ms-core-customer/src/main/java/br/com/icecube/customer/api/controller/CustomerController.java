package br.com.icecube.customer.api.controller;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.service.CustomerService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody CustomerDTO customerDTO) throws BadRequestException {
        return new ResponseEntity<>(customerService.save(customerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<Customer> updateCustomerAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId,
            @RequestBody AddressDTO updatedAddressDTO) {
        Customer updatedCustomer = customerService.updateCustomerAddress(customerId, addressId, updatedAddressDTO);
        return ResponseEntity.ok(updatedCustomer);
    }
}