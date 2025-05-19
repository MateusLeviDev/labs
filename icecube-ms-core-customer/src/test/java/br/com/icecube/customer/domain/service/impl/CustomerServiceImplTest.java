package br.com.icecube.customer.domain.service.impl;

import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.Document;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.model.LegalName;
import br.com.icecube.customer.domain.repository.CustomerRepository;
import br.com.icecube.customer.messaging.event.CustomerEvent;
import jakarta.persistence.EntityNotFoundException;
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

import static br.com.icecube.customer.api.contants.Constants.Kafka.*;
import static br.com.icecube.customer.common.constants.TestConstants.CUSTOMER_ID;
import static br.com.icecube.customer.common.constants.TestConstants.CUSTOMER_NON_EXISTENT_ID;
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

    Customer existingCustomer;

    @BeforeEach
    void setUp() {
        customerToBeSaved = Customer.create(
                LegalName.of("John Doe"), Document.of("123456"), EmailAddress.of("john@mail.com"));

        existingCustomer = Customer.create(
                LegalName.of("Mary Doe"), Document.of("654123"), EmailAddress.of("mary@mail.com"));
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
        Assertions.assertEquals(CUSTOMER_CREATED, emittedMessage.getHeaders().get(HEADER_NAME));
        Assertions.assertInstanceOf(CustomerEvent.CustomerCreated.class, emittedMessage.getPayload());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldUpdateCustomerEmailSuccessfully() {
        var newEmail = EmailAddress.of("new@mail.com");
        when(customerRepository.getReferenceById(CUSTOMER_ID)).thenReturn(existingCustomer);
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        customerService.updateEmail(CUSTOMER_ID, newEmail);

        verify(customerRepository).getReferenceById(CUSTOMER_ID);
        verify(customerRepository).save(existingCustomer);
        verify(customerProducer).tryEmitNext(messageCaptor.capture());


        Message<?> message = messageCaptor.getValue();
        Assertions.assertEquals(EMAIL_UPDATED, message.getHeaders().get(HEADER_NAME));
        Assertions.assertInstanceOf(CustomerEvent.EmailUpdated.class, message.getPayload());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFoundForEmailUpdate() {
        var newEmail = EmailAddress.of("new@mail.com");

        when(customerRepository.getReferenceById(CUSTOMER_NON_EXISTENT_ID))
                .thenThrow(new EntityNotFoundException("Unable to find active customer"));

        var ex = Assertions.assertThrows(
                EntityNotFoundException.class, () -> customerService.updateEmail(CUSTOMER_NON_EXISTENT_ID, newEmail));

        Assertions.assertEquals("Unable to find active customer", ex.getMessage());
        verify(customerRepository).getReferenceById(CUSTOMER_NON_EXISTENT_ID);
    }


}