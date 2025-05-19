package br.com.icecube.customer.messaging.event;

import br.com.icecube.customer.api.dto.CustomerDTO;

import java.io.Serializable;
import java.time.Instant;

public sealed interface CustomerEvent extends Serializable {

    Long customerId();

    /**
     * Each event is a fact, it describes a state change that occurred to the entity (past tense!)
     *
     * @param customerId the customer id used in order to provide the delivery order semantic
     * @param createdAt  describes when this event occurred
     */
    record CustomerCreated(Long customerId, Instant createdAt, CustomerDTO customer) implements CustomerEvent {

    }

    record EmailUpdated(Long customerId, Instant UpdatedAt, CustomerDTO customer) implements CustomerEvent {

    }
}