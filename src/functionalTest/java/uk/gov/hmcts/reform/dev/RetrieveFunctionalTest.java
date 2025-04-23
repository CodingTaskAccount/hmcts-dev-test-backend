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
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RetrieveFunctionalTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void retrieve_validIdShouldReturnOkWithTaskResponse() {

        String requestJSON = """
        {
            "title": "title",
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

        Response retrieved = given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals("title", retrieved.jsonPath().getString("title"));
        Assertions.assertEquals("description", retrieved.jsonPath().getString("description"));
        Assertions.assertEquals("status", retrieved.jsonPath().getString("status"));
        Assertions.assertEquals("2025-04-30T12:00:00", retrieved.jsonPath().getString("dueDateTime"));
    }

    @Test
    void retrieve_InvalidIdShouldReturnNotFound() {

        String requestJSON = """
        {
            "title": "title",
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

        // Test with a valid ID first to ensure the endpoint is working
        long id = created.jsonPath().getLong("id");

        Response retrieved = given()
            .pathParam("id", id)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(200)
            .extract().response();

        // Test with an invalid ID, should return not found
        given()
            .pathParam("id", -1)
            .when()
            .get("/retrieveTask/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    void retrieveAll_ShouldReturnOkWithTaskResponseList() {

        // Ensure there is at least two tasks in the database
        String requestJSONOne = """
        {
            "title": " title one",
            "description": "description",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
    """;

        String requestJSONTwo = """
        {
            "title": " title two",
            "description": "description",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
    """;

        given()
            .contentType(ContentType.JSON)
            .body(requestJSONOne)
            .when()
            .post("/create")
            .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
            .body(requestJSONOne)
            .when()
            .post("/create")
            .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/retrieveAllTasks")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(1));
    }
}
