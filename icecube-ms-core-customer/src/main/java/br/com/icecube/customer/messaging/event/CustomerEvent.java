package br.com.icecube.customer.messaging.event;

import java.io.Serializable;
import java.time.Instant;

public sealed interface CustomerEvent extends Serializable {

    /**
     * Each event is a fact, it describes a state change that occurred to the entity (past tense!)
     *
     * @param customerId the customer id used in order to provide the delivery order semantic
     * @param createdAt  describes when this event occurred
     */
    record CustomerCreated(Long customerId, Instant createdAt) implements CustomerEvent {

    }
}