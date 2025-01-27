package br.com.icecube.customer.domain.repository;

import br.com.icecube.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByDocument_Value(String document);
}
