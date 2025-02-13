package br.com.icecube.customer.api.controller;

import br.com.icecube.customer.common.AbstractContainerProvider;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest extends AbstractContainerProvider {

    @Test
    void shouldCreateCustomerSuccessfully() {
        String requestBody = """
                    {
                         "legalName": "John Doe Enterprises",
                         "document": "43082242847",
                         "address": [
                             {
                                 "street": "123 Main St",
                                 "number": "101",
                                 "city": "Somewhere",
                                 "zipcode": "12345"
                             },
                             {
                                 "street": "456 Oak St",
                                 "number": "202",
                                 "city": "Anywhere",
                                 "zipcode": "67890"
                             }
                         ]
                     }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/customers")
                .then()
                .statusCode(201);
    }
}