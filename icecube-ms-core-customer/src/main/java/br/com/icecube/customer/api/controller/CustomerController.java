package br.com.icecube.customer.api.controller;

import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.api.dto.EmailDTO;
import br.com.icecube.customer.api.mapper.CustomerMapper;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.service.CustomerService;
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
    public ResponseEntity<Customer> create(@RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.save(CustomerMapper.mapToCustomer(customerDTO)), HttpStatus.CREATED);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<Void> updateEmail(
            @PathVariable final Long customerId,
            @RequestBody EmailDTO emailDTO) {

        customerService.updateEmail(customerId, EmailAddress.of(emailDTO.emailAddress()));
        return ResponseEntity.noContent().build();
    }
}