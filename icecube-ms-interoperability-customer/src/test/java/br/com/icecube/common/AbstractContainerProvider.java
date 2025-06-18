package br.com.icecube.common;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import static br.com.icecube.common.constants.TestConstants.BASE_URI;
import static br.com.icecube.common.constants.TestConstants.POSTGRES_IMAGE;

public abstract class AbstractContainerProvider {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void setUp() {
        Startables.deepStart(postgres).join();
    }

    @BeforeEach
    void initRestAssured() {
        RestAssured.baseURI = String.format(BASE_URI, port);
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}