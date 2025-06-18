package br.com.icecube.messaging;

import br.com.icecube.common.AbstractContainerProvider;
import br.com.icecube.messaging.event.CustomerDTO;
import br.com.icecube.messaging.event.CustomerEvent;
import br.com.icecube.repository.DecisionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static br.com.icecube.common.constants.TestConstants.*;
import static br.com.icecube.config.MessageRoutingConfig.HEADER_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@EnableTestBinder
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerMessageHandlerTest extends AbstractContainerProvider {

    @Autowired
    private InputDestination input;

    @Autowired
    private OutputDestination output;

    @Autowired
    private DecisionRepository decisionRepository;

    @AfterEach
    public void clean() {
        decisionRepository.deleteAll();
    }

    @Test
    void shouldConsumeAndHandlerCustomerCreated() throws JsonProcessingException {
        var customerDTO = buildCustomerEventDTO();
        var event = BuildCustomerCreatedEvent(customerDTO);
        byte[] payload = buildEventPayload(event);

        Message<byte[]> message = MessageBuilder
                .withPayload(payload)
                .setHeader(HEADER_EVENT_TYPE, CUSTOMER_CREATED_EVENT)
                .build();

        input.send(message);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    var decisions = decisionRepository.findAll();
                    assertThat(decisions).hasSize(1);
                    assertThat(decisions.getFirst().getDocument().getValue()).isEqualTo("123456789");
                });
    }

    @Test
    void shouldConsumeAndHandlerEmailUpdated() throws JsonProcessingException {
        var customerDTO = buildCustomerEventDTO();
        var event = BuildEmailUpdatedEvent(customerDTO);
        byte[] payload = buildEventPayload(event);

        Message<byte[]> message = MessageBuilder
                .withPayload(payload)
                .setHeader(HEADER_EVENT_TYPE, EMAIL_UPDATED_EVENT)
                .build();

        input.send(message);
    }

    @Test
    void shouldConsumeAndRetryCustomerCreatedEvent() throws Exception {
        var customerDTO = buildFailureCustomerEventDTO();
        var event = BuildCustomerCreatedEvent(customerDTO);
        byte[] payload = buildEventPayload(event);

        Message<byte[]> message = MessageBuilder
                .withPayload(payload)
                .setHeader(HEADER_EVENT_TYPE, CUSTOMER_CREATED_EVENT)
                .build();

        input.send(message, CUSTOMER_RETRY_TOPIC);

        var decisions = decisionRepository.findAll();
        assertThat(decisions).isEmpty();
    }

    private static byte[] buildEventPayload(CustomerEvent event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsBytes(event);
    }

    private static CustomerEvent.CustomerCreated BuildCustomerCreatedEvent(CustomerDTO customerDTO) {
        return new CustomerEvent.CustomerCreated(CUSTOMER_ID, Instant.now(), customerDTO);
    }

    private static CustomerEvent.EmailUpdated BuildEmailUpdatedEvent(CustomerDTO customerDTO) {
        return new CustomerEvent.EmailUpdated(CUSTOMER_ID, Instant.now(), customerDTO);
    }

    private static CustomerDTO buildCustomerEventDTO() {
        return new CustomerDTO("TESTE 1", "123456789", "teste@mail.com");
    }

    private static CustomerDTO buildFailureCustomerEventDTO() {
        return new CustomerDTO("TESTE 1", "912345678", "teste@mail.com");
    }
}