package org.sp.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sp.MetadataProperties;
import org.sp.MetadataServerTest;
import org.sp.Services;
import org.sp.model.QueryBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.List.of;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataQueryApiTest extends MetadataServerTest {

    @Test
    @DisplayName("Should process the request with known subject and single property")
    public void knownSubjectKnownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject))
                                        .properties(of("name"))
                                        .build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType()),
                () -> assertNotNull(extract.body().jsonPath().get("$")),
                () -> assertThat((extract.jsonPath().getMap("$")).keySet())
                        .containsExactlyInAnyOrder("subject", "decimals", "policy", "name")
        );
    }

    @Test
    @DisplayName("Should fail request with known subject and unknown property")
    public void knownSubjectUnknownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject))
                                        .properties(of("unknown"))
                                        .build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_UNPROCESSABLE_ENTITY, extract.statusCode())
        );
    }

    @Test
    @DisplayName("Should process request with known subject and no properties")
    public void knownSubjectNoProperties() {
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
                () -> assertThat((extract.response().jsonPath().getMap("subjects[0]")).keySet())
                        .allMatch(MetadataProperties.knownProperties::contains),
                () -> assertThat((extract.response().jsonPath().getMap("subjects[0]")).keySet())
                        .allMatch(MetadataProperties.builtInProperties::contains)
        );
    }

    @Test
    @DisplayName("Should process request with known subject and two properties")
    public void KnownSubjectTwoProperties() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject))
                                        .properties(of("name", "description"))
                                        .build()
                        )
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType()),
                () -> assertThat((extract.jsonPath().getMap("$")).keySet())
                        .containsExactlyInAnyOrder("subject", "name", "description")
        );
    }

    @Test
    @DisplayName("Should fail for empty subjects list")
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
                () -> assertEquals(SC_BAD_REQUEST, extract.statusCode())
        );
    }

    @Test
    @DisplayName("Should fail for empty subjects and properties lists")
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
                () -> assertEquals(SC_BAD_REQUEST, extract.statusCode())
        );
    }

    @Test
    @DisplayName("Should fail for non existing subject")
    public void oneKnownSubjectAndOneUnknown() {
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
                () -> assertEquals(SC_BAD_REQUEST, extract.statusCode())
        );
    }

    @Test
    public void twoSameSubjects() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(
                                QueryBody.builder()
                                        .subjects(of(existingSubject, existingSubject))
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
    public void allKnownProperties() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_QUERY)
                        .contentType(ContentType.JSON)
                        .body(QueryBody.builder()
                                .subjects(of(existingSubject))
                                .properties(of("name", "description", "ticker", "logo", "url", "unit")).build())
                        .when()
                        .post()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType()),
                () -> assertThat((extract.jsonPath().getMap("$")).keySet())
                        .containsExactlyInAnyOrder(
                                "subject",
                                "decimals",
                                "policy",
                                "name",
                                "description",
                                "ticker",
                                "logo",
                                "url",
                                "unit"
                        )
        );
    }

    @Test
    public void veryLongSubjectList() {
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
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
}
