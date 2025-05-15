package br.com.icecube.customer.api.mapper;

import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.Document;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.model.LegalName;
import br.com.icecube.customer.messaging.event.CustomerEvent;

import java.time.Instant;

public interface CustomerMapper {

    static Customer mapToCustomer(final CustomerDTO customerDTO) {
        Document document = Document.of(customerDTO.document());
        LegalName lastName = LegalName.of(customerDTO.legalName());
        EmailAddress emailAddress = EmailAddress.of(customerDTO.emailAddress());
        return Customer.create(lastName, document, emailAddress);
    }

    static CustomerDTO mapToCustomerDTO(final Customer customerCreated) {
        return new CustomerDTO(customerCreated.getLegalName().getValue(),
                customerCreated.getEmailAddress().getValue(),
                customerCreated.getDocument().getValue());
    }

    static CustomerEvent.CustomerCreated mapToCreateCustomerEvent(Customer customerCreated) {
        return new CustomerEvent.CustomerCreated(
                customerCreated.getId(), Instant.now(), mapToCustomerDTO(customerCreated));
    }

    static CustomerEvent.EmailUpdated mapToEmailChangedEvent(Customer customer) {
        return new CustomerEvent.EmailUpdated(
                customer.getId(), Instant.now(), mapToCustomerDTO(customer));
    }

}
