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

import static br.com.icecube.customer.api.contants.Constants.Kafka.*;
import static br.com.icecube.customer.api.mapper.CustomerMapper.mapToCreateCustomerEvent;
import static br.com.icecube.customer.api.mapper.CustomerMapper.mapToEmailChangedEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Message<?>> customerProducer;


    @Override
    @Transactional
    public Customer save(Customer customer) {
        final var savedCustomer = customerRepository.save(customer);
        CustomerEvent.CustomerCreated eventPayload = mapToCreateCustomerEvent(savedCustomer);
        final var outboundMessage = buildEventPayload(eventPayload, CUSTOMER_CREATED);
        customerProducer.tryEmitNext(outboundMessage);
        log.info("Customer saved successfully document = {}", savedCustomer.getDocument().getValue());
        return savedCustomer;
    }

    @Override
    public void updateEmail(Long customerId, EmailAddress emailAddress) {
        final var savedCustomer = customerRepository.getReferenceById(customerId);
        savedCustomer.changeEmail(emailAddress);
        customerRepository.save(savedCustomer);
        CustomerEvent.EmailUpdated eventPayload = mapToEmailChangedEvent(savedCustomer);
        final var outboundMessage = buildEventPayload(eventPayload, EMAIL_UPDATED);
        customerProducer.tryEmitNext(outboundMessage);
        log.info("Customer Id {} email updated successfully", savedCustomer.getId());
    }

    private Message<?> buildEventPayload(CustomerEvent payload, String eventType) {
        return MessageBuilder.withPayload(payload)
                .setHeader(HEADER_NAME, eventType)
                .setHeader(KafkaHeaders.KEY, String.valueOf(payload.customerId()).getBytes())
                .build();
    }

    private Customer findById(final Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Unable to find active customer"));
    }

}
