package br.com.icecube.customer.domain.service.impl;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.api.mapper.AddressMapper;
import br.com.icecube.customer.api.mapper.CustomerMapper;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.repository.CustomerRepository;
import br.com.icecube.customer.domain.service.CustomerService;
import br.com.icecube.customer.messaging.event.CustomerEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    public static final String DUPLICATED_DOCUMENT = "Customer's document %s already registered";
    public static final String CUSTOMER_NOT_FOUND = "Customer ID %s not found";
    public static final String ADDRESS_NOT_FOUND = "Address ID %s not found.";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final Sinks.Many<CustomerEvent> customerProducer;

    public CustomerServiceImpl(
            CustomerRepository customerRepository, CustomerMapper customerMapper, AddressMapper addressMapper,
            Sinks.Many<CustomerEvent> customerProducer) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
        this.customerProducer = customerProducer;
    }

    @Override
    @Transactional
    public Customer save(CustomerDTO customerDTO) throws BadRequestException {
        checkDuplicateDocument(customerDTO.document());
        final var customer = customerMapper.toModel(customerDTO);
        customer.getAddress().forEach(address -> address.setCustomer(customer));
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved successfully document = {}", savedCustomer.getDocument().getValue());

        var customerCreatedEvent = new CustomerEvent.CustomerCreated(savedCustomer.getId(), Instant.now());
        customerProducer.tryEmitNext(customerCreatedEvent);

        return savedCustomer;
    }

    @Transactional
    public Customer updateCustomerAddress(Long customerId, Long addressId, AddressDTO updatedAddressDTO) {
        final var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(CUSTOMER_NOT_FOUND.formatted(customerId)));
        validateIfAddressExist(addressId, customer);
        customer.updateAddress(addressId, addressMapper.toModel(updatedAddressDTO));

        return customerRepository.save(customer);
    }

    private void validateIfAddressExist(Long addressId, Customer customer) {
        boolean addressExists = customer.getAddress().stream().anyMatch(address -> address.getId().equals(addressId));

        if (!addressExists) {
            throw new EntityNotFoundException(ADDRESS_NOT_FOUND.formatted(addressId));
        }
    }

    private void checkDuplicateDocument(String document) throws BadRequestException {
        if (customerRepository.existsByDocument_Value(document)) {
            throw new BadRequestException(DUPLICATED_DOCUMENT.formatted(document));
        }
    }

}
