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

import static br.com.icecube.config.MessageRoutingConfig.HEADER_EVENT_TYPE;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerMessageHandler {

    private final DecisionMakerService decisionMakerService;

    @Bean
    public Consumer<Message<CustomerEvent.CustomerCreated>> processCustomerCreated() {
        return customersCreated -> {
            log.info("[CustomerCreated] Handling event type: ----------> {}",
                    customersCreated.getHeaders().get(HEADER_EVENT_TYPE));
            CustomerEvent.CustomerCreated payload = customersCreated.getPayload();
            processWithRetryHandling(payload, () -> processCustomerCreated(payload));
        };
    }

    @Bean
    public Consumer<Message<CustomerEvent.EmailUpdated>> processEmailUpdated() {
        return emailUpdated -> {
            log.info("[EmailUpdated] Handling event type: ----------> {}",
                    emailUpdated.getHeaders().get(HEADER_EVENT_TYPE));
            log.info("the message is: {}", emailUpdated.getPayload());
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
        if (customer.document().startsWith("9")) {
            throw new TransientFailureException("retry this message");
        }
        Decision decision = decisionMakerService.decide(customer.document());
        log.info("Processed request for customer Document [{}]. Result: {}", customer.document(), decision);
    }

    private void processWithRetryHandling(CustomerEvent.CustomerCreated customerCreated, Runnable process) {
        try {
            process.run();
            log.info("Successfully processed event: {}", customerCreated);
        } catch (TransientFailureException e) {
            log.error("Retryable error occurred, might retry: {}", e.getReason());
            throw e;
        }
    }
}
