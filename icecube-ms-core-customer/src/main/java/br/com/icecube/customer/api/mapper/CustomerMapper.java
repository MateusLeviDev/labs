package br.com.icecube.customer.api.mapper;

import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.Document;
import br.com.icecube.customer.domain.model.EmailAddress;
import br.com.icecube.customer.domain.model.LegalName;

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

}
