package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateFunctionalTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void create_validShouldReturnOkWithTaskResponse() {

        String requestJSON = """
        {
            "title": "title",
            "description": "description",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestJSON)
            .when()
            .post("/create")
            .then()
            .statusCode(200)
            .body("title", equalTo("title"))
            .body("description", equalTo("description"))
            .body("status", equalTo("status"))
            .body("dueDateTime", equalTo("2025-04-30T12:00:00"));
    }

    @Test
    void create_validNoDescriptionShouldReturnOkWithTaskResponse() {

        String requestJSON = """
        {
            "title": "title",
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestJSON)
            .when()
            .post("/create")
            .then()
            .statusCode(200)
            .body("title", equalTo("title"))
            .body("description", equalTo(null))
            .body("status", equalTo("status"))
            .body("dueDateTime", equalTo("2025-04-30T12:00:00"));
    }

    @Test
    void create_invalidShouldReturnBadRequest() {

        String requestJSON = """
        {
            "status": "status",
            "dueDateTime": "2025-04-30T12:00:00"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestJSON)
            .when()
            .post("/create")
            .then()
            .statusCode(400);
    }
}
