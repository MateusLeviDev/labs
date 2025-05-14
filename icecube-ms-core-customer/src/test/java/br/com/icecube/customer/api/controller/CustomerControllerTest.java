//package br.com.icecube.customer.api.controller;
//
//import br.com.icecube.customer.common.AbstractContainerProvider;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static br.com.icecube.customer.common.constants.TestConstants.CUSTOMER_URI;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class CustomerControllerTest extends AbstractContainerProvider {
//
//    @Test
//    void shouldCreateCustomerSuccessfully() {
//        var requestBody = """
//                    {
//                         "legalName": "John Doe Enterprises",
//                         "document": "42082042040",
//                         "address": [
//                             {
//                                 "street": "123 Main St",
//                                 "number": "101",
//                                 "city": "Somewhere",
//                                 "zipcode": "12345"
//                             },
//                             {
//                                 "street": "456 Oak St",
//                                 "number": "202",
//                                 "city": "Anywhere",
//                                 "zipcode": "67890"
//                             }
//                         ]
//                     }
//                """;
//
//        Response response = given()
//                .contentType(ContentType.JSON)
//                .body(requestBody)
//                .when()
//                .post(CUSTOMER_URI)
//                .then()
//                .statusCode(201)
//                .body("legalName.value", equalTo("John Doe Enterprises"))
//                .body("document.value", equalTo("42082042040"))
//                .body("address[1].zipcode", equalTo("67890"))
//                .extract()
//                .response();
//
//        var savedCustomerId = response.jsonPath().getString("id");
//        Assertions.assertNotNull(savedCustomerId);
//
//    }
//}