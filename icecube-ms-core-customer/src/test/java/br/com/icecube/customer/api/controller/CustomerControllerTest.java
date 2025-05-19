package br.com.icecube.customer.api.controller;

import br.com.icecube.customer.common.AbstractContainerProvider;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import static br.com.icecube.customer.common.constants.TestConstants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest extends AbstractContainerProvider {

    @Test
    void shouldCreateCustomerSuccessfully() {
        var requestBody = """
                    {
                       "legalName": "John Doe Enterprises",
                       "document": "42082042040",
                       "emailAddress": "john@mail.com"
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CUSTOMER_URI)
                .then()
                .statusCode(201)
                .body("legalName.value", equalTo("John Doe Enterprises"))
                .body("document.value", equalTo("42082042040"))
                .body("emailAddress.value", equalTo("john@mail.com"));
    }

    @Test
    @Sql(scripts = "/scripts/customer/created_customer_test.sql")
    void shouldUpdateCustomerSuccessfully() {
        var requestBody = """
                    {
                       "emailAddress": "new@mail.com"
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(CUSTOMER_URI + "/" + CUSTOMER_ID)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenCustomerDoesNotExist() {
        var requestBody = """
                    {
                       "emailAddress": "new@mail.com"
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(CUSTOMER_URI + "/" + CUSTOMER_NON_EXISTENT_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Entity not found"))
                .body("details", equalTo("Unable to find Customer with id 999"));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() {
        var requestBody = """
                    {
                       "emailAddress": "invalid-email"
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(CUSTOMER_URI + "/" + CUSTOMER_ID)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().body();
    }

}