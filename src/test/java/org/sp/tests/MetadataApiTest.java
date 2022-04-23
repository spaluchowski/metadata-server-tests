package org.sp.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.sp.Services;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class MetadataApiTest {

    @Test
    public void shouldSomething() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTY)
                        .pathParam("property", "789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f1")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode())
                );
    }

    @Test
    public void shouldNotFound() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTY)
                        .pathParam("property", "should_not_be_found")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode())
                );
    }

    @Test
    public void shouldNotFoundUnknownEndpoint() {
        //TODO: try xss
        ExtractableResponse<Response> extract =
                given()
                        .basePath("/metadata")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode()),
                () -> assertEquals(ContentType.HTML.withCharset("utf-8").toString(), extract.contentType())
                );
    }
    @Test
    public void shouldNotFoundUnknEXss() {
        //TODO: try xss
        ExtractableResponse<Response> extract =
                given()
                        .basePath("/<\\body>")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode()),
                () -> assertEquals(ContentType.HTML.withCharset("utf-8").toString(), extract.contentType())
                );
    }
}
