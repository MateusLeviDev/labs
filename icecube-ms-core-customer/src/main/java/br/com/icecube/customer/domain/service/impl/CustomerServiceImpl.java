package br.com.icecube.customer.domain.service.impl;

import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.repository.CustomerRepository;
import br.com.icecube.customer.domain.service.CustomerService;
import br.com.icecube.customer.messaging.event.CustomerEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.time.Instant;

import static br.com.icecube.customer.api.contants.Constants.Kafka.*;
import static br.com.icecube.customer.api.mapper.CustomerMapper.mapToCustomerDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Message<?>> customerProducer;

    private static CustomerEvent.CustomerCreated mapToCreateCustomerEvent(Customer customerCreated) {
        return new CustomerEvent.CustomerCreated(
                customerCreated.getId(), Instant.now(), mapToCustomerDTO(customerCreated));
    }

    private static CustomerEvent.EmailUpdated mapToEmailChangedEvent(Customer customer) {
        return new CustomerEvent.EmailUpdated(
                customer.getId(), Instant.now(), mapToCustomerDTO(customer));
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        final Customer savedCustomer = customerRepository.save(customer);

        CustomerEvent.CustomerCreated customerCreatedEvent = mapToCreateCustomerEvent(savedCustomer);
        final var customerCreatedMessage = MessageBuilder.withPayload(customerCreatedEvent)
                .setHeader(HEADER_NAME, CUSTOMER_CREATED)
                .setHeader(KafkaHeaders.KEY, String.valueOf(customerCreatedEvent.customerId()).getBytes())
                .build();

        customerProducer.tryEmitNext(customerCreatedMessage);

        log.info("Customer saved successfully document = {}", savedCustomer.getDocument().getValue());
        return savedCustomer;
    }

    @Override
    public void updateEmail(Long customerId, EmailAddress emailAddress) {
        final var customer = customerRepository.getReferenceById(customerId);
        customer.changeEmail(emailAddress);
        customerRepository.save(customer);

        CustomerEvent.EmailUpdated emailUpdatedMessage = mapToEmailChangedEvent(customer);
        final var customerCreatedMessage = MessageBuilder.withPayload(emailUpdatedMessage)
                .setHeader(HEADER_NAME, EMAIL_UPDATED)
                .setHeader(KafkaHeaders.KEY, String.valueOf(emailUpdatedMessage.customerId()).getBytes())
                .build();
        customerProducer.tryEmitNext(customerCreatedMessage);

        log.info("Customer Id {} email updated successfully", customer.getId());

    }

    private Customer findById(final Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Unable to find active customer"));
    }

}
