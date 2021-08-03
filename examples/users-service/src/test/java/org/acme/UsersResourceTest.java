package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
@TestProfile(SharqQuarkusTestProfile.class)
public class UsersResourceTest {

    @Test
    public void testCRUD() {
        User testUser = new User();
        testUser.setUsername("foo");
        testUser.setPassword("baz");
        RestAssured.given()
          .when()
              .contentType(ContentType.JSON)
              .body(testUser)
              .post("/users")
          .then()
             .statusCode(204);

        User getUser = RestAssured.given()
            .when()
                .get("/users/foo")
                .as(User.class);

        assertEquals(testUser.getUsername(), getUser.getUsername());
        assertEquals(testUser.getPassword(), getUser.getPassword());

        RestAssured.given()
            .when()
                .delete("/users/foo")
            .then()
                .statusCode(204);

        RestAssured.given()
        .when()
            .get("/users/foo")
        .then()
            .statusCode(404);
    }

}