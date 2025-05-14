package br.com.icecube.customer.domain.service;

import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.EmailAddress;

public interface CustomerService {

    Customer save(Customer customer);

    void updateEmail(Long customerId, EmailAddress emailAddress);
}
