package br.com.icecube.customer.common;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import static br.com.icecube.customer.common.constants.TestConstants.*;

public abstract class AbstractContainerProvider {

    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(KAFKA_IMAGE);
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void setUp() {
        System.setProperty(DOCKER_API_VERSION, DOCKER_API_VERSION_VALUE);
        Startables.deepStart(postgres, kafka).join();
    }

    @BeforeEach
    void initRestAssured() {
        RestAssured.baseURI = String.format(BASE_URI, port);
    }

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute(TRUNCATE);
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}