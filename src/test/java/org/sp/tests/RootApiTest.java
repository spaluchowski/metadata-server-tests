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

public class RootApiTest {

    @Test
    public void shouldSomething() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.ROOT)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void shouldSometccching() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath("/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/ticker")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }
}