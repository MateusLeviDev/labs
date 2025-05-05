package br.com.icecube.messaging;

import br.com.icecube.domain.Decision;
import br.com.icecube.exception.TransientFailureException;
import br.com.icecube.messaging.event.CustomerDTO;
import br.com.icecube.messaging.event.CustomerEvent;
import br.com.icecube.service.DecisionMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerMessageHandler {

    private final DecisionMakerService decisionMakerService;

    @Bean
    public Consumer<Message<CustomerEvent.CustomerCreated>> processCustomerCreated() {
        return customersCreated -> {
            log.info("-----------> Consuming from Original Topic: {}", customersCreated);
            CustomerEvent.CustomerCreated payload = customersCreated.getPayload();
            processWithRetryHandling(payload, () -> processCustomerCreated(payload));
        };
    }

    @Bean
    public Consumer<Message<CustomerEvent.CustomerCreated>> processCustomerCreatedFromRetryTopic() {
        return customerCreatedMessage -> {
            log.info("-----------> Retrying using retry topic: {}", customerCreatedMessage);
            CustomerEvent.CustomerCreated payload = customerCreatedMessage.getPayload();
            processWithRetryHandling(payload, () -> processCustomerCreated(payload));
        };
    }

    private void processCustomerCreated(CustomerEvent.CustomerCreated customerCreated) {
        log.info("Processing event 'CustomerCreated': {}", customerCreated);
        CustomerDTO customer = customerCreated.customer();
        if (customer.ssn() % 2 == 0) {
            throw new TransientFailureException("retry this message");
        }
        Decision decision = decisionMakerService.decide(customer.ssn(), customer.birthDate());
        log.info("Processed request for customer SSN [{}]. Result: {}", customer.ssn(), decision);
    }

    private void processWithRetryHandling(CustomerEvent.CustomerCreated customerCreated, Runnable process) {
        try {
            process.run();
            log.info("Successfully processed event: {}", customerCreated);
        } catch (TransientFailureException e) {
            log.error("Retryable error occurred, might retry: {}", e.getMessage());
            throw e;
        }
    }
}
