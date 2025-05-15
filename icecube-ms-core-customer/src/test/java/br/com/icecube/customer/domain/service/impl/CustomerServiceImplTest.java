package br.com.icecube.customer.domain.service.impl;

import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.Document;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.model.LegalName;
import br.com.icecube.customer.domain.repository.CustomerRepository;
import br.com.icecube.customer.messaging.event.CustomerEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import reactor.core.publisher.Sinks;

import static br.com.icecube.customer.api.contants.Constants.Kafka.HEADER_NAME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private Sinks.Many<Message<?>> customerProducer;

    Customer customerToBeSaved;

    @BeforeEach
    void setUp() {
        customerToBeSaved = Customer.create(
                LegalName.of("John Doe"), Document.of("123456"), EmailAddress.of("johndoe@mail.com"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateCustomerSuccessfully() {
        when(customerRepository.save(customerToBeSaved)).thenReturn(customerToBeSaved);
        ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Customer result = customerService.save(customerToBeSaved);

        Assertions.assertNotNull(result);
        verify(customerRepository).save(customerToBeSaved);
        verify(customerProducer).tryEmitNext(messageCaptor.capture());

        Message<?> emittedMessage = messageCaptor.getValue();
        Assertions.assertEquals("CustomerCreated", emittedMessage.getHeaders().get(HEADER_NAME));
        Assertions.assertInstanceOf(CustomerEvent.CustomerCreated.class, emittedMessage.getPayload());
    }

}