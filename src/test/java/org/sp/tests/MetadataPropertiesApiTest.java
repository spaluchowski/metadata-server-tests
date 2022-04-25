package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.sp.AllowedProperties;
import org.sp.MetadataServerTest;
import org.sp.Services;
import org.sp.Steps;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataPropertiesApiTest extends MetadataServerTest {

    @Test
    public void unknownProperty() {
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
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void knownPropertyUnknownSubject() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", nonExistingSubject)
                        .pathParam("property", "unknown")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void knownProperty() {
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
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void forbiddenProperty() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", existingSubject)
                        .pathParam("property", "policy")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    public static Collection<Object[]> getAllowedProperties() {
        return AllowedProperties.properties.stream().map(p -> new String[]{p}).collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("getAllowedProperties")
    @DisplayName("Should be able to request every allowed property")
    public void allowedProperties(String property) {
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
    public void readFoundProperties(String subject, String property) {
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
