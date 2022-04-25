package org.sp.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sp.AllowedProperties;
import org.sp.MetadataServerTest;
import org.sp.Services;
import org.sp.Steps;
import org.sp.model.QueryBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.List.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class MetadataQueryApiTest extends MetadataServerTest {

    @Test
    public void onlyKnownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject))
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType()),
                () -> assertThat(extract.response().jsonPath().getList("subjects"))
                        .hasSize(1),
                () -> assertThat(extract.response().jsonPath().getString("subjects[0].subject"))
                        .isEqualTo(existingSubject),
                () -> assertThat(((Map) extract.response().jsonPath().get("subjects[0]")).keySet())
                        .allMatch(AllowedProperties.properties::contains)
        );
    }

    @Test
    public void threePropertiesForKnownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject))
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
    public void oneExistingSubjectAndOneMissing() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject, nonExistingSubject))
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType()),
                () -> assertThat(extract.response().jsonPath().getList("subjects"))
                        .hasSize(1),
                () -> assertThat(extract.response().jsonPath().getString("subjects[0].subject"))
                        .isEqualTo(existingSubject)
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
    public void veryLongSubjectList() {
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < 16 * 1024; i++) {
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
    public void unknownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON.toString())
                        .body(QueryBody.builder()
                                .subjects(of(existingSubject))
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
}
