package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sp.MetadataProperties;
import org.sp.MetadataServerTest;
import org.sp.Services;

import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class MetadataApiTest extends MetadataServerTest {

    @Test
    @DisplayName("Should process known subject")
    public void knownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_SUBJECT)
                        .pathParam("subject", existingSubject)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertThat(extract.response().jsonPath().getString("subject"))
                        .isEqualTo(existingSubject),
                () -> assertThat(extract.response().jsonPath().getMap("$").keySet())
                        .containsAll(MetadataProperties.builtInProperties)
                );
    }

    @Test
    @DisplayName("Should fail on unknown subject - with known subject string at the end")
    public void knownSubjectAtTheEnd() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_SUBJECT)
                        .pathParam("subject", "test%2F" + existingSubject)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_UNPROCESSABLE_ENTITY, extract.statusCode())
        );
    }

    @Test
    @DisplayName("Should fail on unknown subject")
    public void unknownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_SUBJECT)
                        .pathParam("subject", nonExistingSubject)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, extract.statusCode())
        );
    }
}
