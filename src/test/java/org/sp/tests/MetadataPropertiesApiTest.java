package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.sp.MetadataProperties;
import org.sp.MetadataServerTest;
import org.sp.Services;
import org.sp.Steps;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataPropertiesApiTest extends MetadataServerTest {

    @Test
    public void knownSubjectUnknownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", existingSubject)
                        .pathParam("property", "unknown")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_UNPROCESSABLE_ENTITY, extract.statusCode())
        );
    }

    @Test
    public void unknownSubjectKnownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", nonExistingSubject)
                        .pathParam("property", "description")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_UNPROCESSABLE_ENTITY, extract.statusCode())
        );
    }

    @Test
    public void knownSubjectKnownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", existingSubject)
                        .pathParam("property", "ticker")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertNotNull(extract.jsonPath().get("$")),
                () -> assertNotNull(extract.jsonPath().get("ticker"))
        );
    }

    @Test
    public void unknownSubjectUnknownProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", nonExistingSubject)
                        .pathParam("property", "ticker")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, extract.statusCode())
        );
    }

    @Test
    public void builtInProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", existingSubject)
                        .pathParam("property", "decimals")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertNotNull(extract.jsonPath().get("$")),
                () -> assertNotNull(extract.jsonPath().get("decimals"))
        );
    }

    public static Collection<Object[]> getAllowedProperties() {
        return MetadataProperties.knownProperties.stream().map(p -> new String[]{p}).collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("getAllowedProperties")
    @DisplayName("Should be able to request every known property")
    public void knownProperties(String property) {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", existingSubject)
                        .pathParam("property", property)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertNotNull(extract.jsonPath().get("$"))
        );
    }

    public static Collection<Object[]> getFoundProperties() {
        List<Object[]> scenarios = new ArrayList<>();
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        allMetadataPaths.forEach(s -> {
            String subject = Steps.getMetadataSubjectFromPath(s);
            Response metadata = Steps.getMetadata(subject);
            Set<String> keys = ((Map<String, Object>) metadata.jsonPath().get("$")).keySet();
            keys.forEach(key -> {
                scenarios.add(new Object[]{subject, key});
            });
        });
        return scenarios;
    }

    @ParameterizedTest
    @MethodSource("getFoundProperties")
    @DisplayName("Should be able to request every property of a subject")
    public void readAllFoundProperties(String subject, String property) {
        Response metadata = Steps.getMetadata(subject);
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", subject)
                        .pathParam("property", property)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> JSONAssert.assertEquals(new JSONObject(extract.response().body().asPrettyString()), new JSONObject(metadata.body().asPrettyString()).getJSONObject(property), true)
        );
    }
}
