package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.sp.MetadataServerTest;
import org.sp.Services;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class MetadataApiTest extends MetadataServerTest {

    @Test
    public void knownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTY)
                        .pathParam("subject", existingSubject)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertThat(extract.response().jsonPath().getString("subject"))
                        .isEqualTo(existingSubject)
        );
    }

    @Test
    public void unknownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTY)
                        .pathParam("subject", nonExistingSubject)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode())
        );
    }
}
