package br.com.icecube.customer.common.constants;

public class TestConstants {
    public static final Long CUSTOMER_ID = 1L;
    public static final Long CUSTOMER_NON_EXISTENT_ID = 999L;
    public static final String BASE_URI = "http://localhost:%s";
    public static final String KAFKA_IMAGE = "confluentinc/cp-kafka:7.4.0";
    public static final String POSTGRES_IMAGE = "postgres:16-alpine";
    public static final String CUSTOMER_URI = "/v1/customers";
    public static final String TRUNCATE = "TRUNCATE TABLE customer RESTART IDENTITY CASCADE";
    public static final String DOCKER_API_VERSION = "api.version";
    public static final String DOCKER_API_VERSION_VALUE = "1.44";

}
