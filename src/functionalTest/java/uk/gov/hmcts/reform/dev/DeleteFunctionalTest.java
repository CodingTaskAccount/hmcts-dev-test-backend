package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DeleteFunctionalTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void delete_validShouldReturnOk() {

        // Create task and validate it exists before deletion
        String requestJSON = """
        {
            "title": "title",
            "caseNumber": 100,
            "description": "description",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
        """;

        Response created = given()
            .contentType(ContentType.JSON)
            .body(requestJSON)
            .when()
            .post("/create")
            .then()
            .statusCode(200)
            .extract().response();

        long id = created.jsonPath().getLong("id");

        given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200);

        // Delete task, should return OK with deleted task ID
        Long deletedResponse = given()
            .pathParam("id", id)
            .when()
            .delete("delete/{id}")
            .then()
            .statusCode(200)
            .extract()
            .as(Long.class);

        Assertions.assertEquals(id, deletedResponse);

        // Ensure task is deleted using /retrieveTask, should return Not Found
        given()
            .pathParam("id", id)
            .when()
            .get("retrieveTask/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    void delete_InvalidIdShouldReturnNotFound() {

        // Create task and validate it before testing delete to ensure there is at least one task in the database
        String requestJSON = """
        {
            "title": "title",
            "caseNumber": 100,
            "description": "description",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
        """;

        Response created = given()
            .contentType(ContentType.JSON)
            .body(requestJSON)
            .when()
            .post("/create")
            .then()
            .statusCode(200)
            .extract().response();

        long id = created.jsonPath().getLong("id");

        given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200);

        // Call /delete with an invalid ID, should return not found
        given()
            .pathParam("id", -1)
            .when()
            .delete("delete/{id}")
            .then()
            .statusCode(404);
    }
}
