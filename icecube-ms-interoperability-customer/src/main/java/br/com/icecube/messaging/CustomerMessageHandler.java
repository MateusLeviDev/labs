package br.com.icecube.messaging;

import br.com.icecube.domain.Decision;
import br.com.icecube.messaging.event.CustomerDTO;
import br.com.icecube.messaging.event.CustomerEvent;
import br.com.icecube.service.DecisionMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerMessageHandler {

    private final DecisionMakerService decisionMakerService;

    @Bean
    public Function<CustomerEvent.CustomerCreated, Decision> processCustomerCreated() {
        return customerCreated -> {
            log.info("Processing event 'CustomerCreated': {}", customerCreated);
            CustomerDTO customer = customerCreated.customer();
            Decision decision = decisionMakerService.decide(customer.ssn(), customer.birthDate());
            log.info("Processed request for customer SSN [{}]. Result: {}", customer.ssn(), decision);
            return decision;
        };
    }
}
