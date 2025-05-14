package br.com.icecube.customer.api.contants;

public final class Constants {

    private Constants() {
    }

    public static final class ErrorMessages {
        public static final String CUSTOMER_NOT_FOUND = "Customer ID %s not found";
        public static final String ADDRESS_NOT_FOUND = "Address ID %s not found.";
    }

    public static final class Kafka {
        public static final String HEADER_NAME = "X-EVENT-TYPE";
        public static final String CUSTOMER_CREATED = "CustomerCreated";
        public static final String EMAIL_UPDATED = "EmailUpdated";
    }
}
