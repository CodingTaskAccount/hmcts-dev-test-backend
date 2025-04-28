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
class UpdateFunctionalTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void update_validShouldReturnOkWithTaskResponse() {

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

        // Update status
        given()
            .pathParam("id", id)
            .queryParam("status","new status")
            .when()
            .patch("/update/{id}")
            .then()
            .statusCode(200);

        // Verify status is updated
        Response retrieved = given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals("new status", retrieved.jsonPath().getString("status"));
    }

    @Test
    void update_invalidIdShouldReturnNotFound() {

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

        // Testing /update with a valid ID first to ensure the endpoint works
        // before testing with an invalid ID
        given()
            .pathParam("id", id)
            .queryParam("status","new status")
            .when()
            .patch("/update/{id}")
            .then()
            .statusCode(200);


        Response retrieved = given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals("new status", retrieved.jsonPath().getString("status"));

        // Update status with invalid ID, should return Not Found
        given()
            .pathParam("id", -1)
            .queryParam("status","new status")
            .when()
            .patch("/update/{id}")
            .then()
            .statusCode(404);
    }
}
