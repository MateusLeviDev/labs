package br.com.icecube.common.constants;

public class TestConstants {
    public static final Long CUSTOMER_ID = 1L;
    public static final Long CUSTOMER_NON_EXISTENT_ID = 999L;
    public static final String BASE_URI = "http://localhost:%s";
    public static final String POSTGRES_IMAGE = "postgres:16-alpine";
    public static final String CUSTOMER_CREATED_EVENT = "CustomerCreated";
    public static final String EMAIL_UPDATED_EVENT = "EmailUpdated";
    public static final String CUSTOMER_RETRY_TOPIC = "decision.customer-retry-topic";

}
