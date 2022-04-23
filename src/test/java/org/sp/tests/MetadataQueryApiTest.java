package org.sp.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.sp.Services;
import org.sp.model.QueryBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.List.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class MetadataQueryApiTest {

    @Test
    public void shouldGetOkResponseSingleSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of("a"))
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void shouldGetOkResponseWithProperties() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of("x"))
                                        .properties(of("name", "description", "url"))
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void emptySubjects() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of())
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void emptySubjectsAndProperties() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of())
                                        .properties(of())
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }


    @Test
    public void noBody() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void emptyBody() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body("{}")
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void contentTypeWrong() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.TEXT.toString())
                        .body("{\"subjects\":[\"s\"]}")
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void contentTypeWrondddg() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON.toString())
                        .body(QueryBody.builder().subjects(of(getString())).build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }

    @Test
    public void contentTypeWrjkljklndddg() {
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < 16*1024; i++) {
            subjects.add(Integer.toString(i));
        }

        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON.toString())
                        .body(QueryBody.builder().subjects(subjects).build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }
    @Test
    public void contentTypeWrjklkjkjkljdddg() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON.toString())
                        .body(QueryBody.builder()
                                .subjects(of("919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e"))
                                .properties(of("unknown")).build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );
    }


    public String getString() {
        StringBuilder s = new StringBuilder();
        for (var i = 0; i <= 255; i++) {
            s.append((char) i);
        }
        return s.toString();
    }
}
