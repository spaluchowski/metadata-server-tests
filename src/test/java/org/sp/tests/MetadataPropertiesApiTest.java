package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataPropertiesApiTest extends MetadataServerTest {

    @Test
    @DisplayName("Should fail on unknown property")
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
    @DisplayName("Should fail on unknown subject")
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
    @DisplayName("Should return requested property")
    public void knownSubjectKnownProperty() {
        Response metadata = Steps.getMetadata(existingSubject);

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
                () -> JSONAssert.assertEquals(
                        new JSONObject(extract.response().body().asPrettyString()),
                        new JSONObject(metadata.body().asPrettyString()).getJSONObject("ticker"),
                        true
                )

        );
    }

    @Test
    @DisplayName("Should process request for builtin property")
    public void builtInProperty() {
        Response metadata = Steps.getMetadata(existingSubject);

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
                () -> JSONAssert.assertEquals(
                        new JSONObject(extract.response().body().asPrettyString()),
                        new JSONObject(metadata.body().asPrettyString()).getJSONObject("decimals"),
                        true
                )
        );
    }

    private static Collection<Object[]> getAllowedProperties() {
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

    private static Collection<Object[]> getFoundProperties() {
        List<Object[]> scenarios = new ArrayList<>();
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        allMetadataPaths.forEach(s -> {
            String subject = Steps.getMetadataSubjectFromPath(s);
            Response metadata = Steps.getMetadata(subject);
            Set<Object> keys = metadata.jsonPath().getMap("$").keySet();
            keys.forEach(key -> {
                scenarios.add(new Object[]{subject, key});
            });
        });
        return scenarios;
    }

    @ParameterizedTest
    @MethodSource("getFoundProperties")
    @DisplayName("Should be able to request every property of a metadata")
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
