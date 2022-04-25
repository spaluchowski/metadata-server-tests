package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sp.AllowedProperties;
import org.sp.Services;
import org.sp.Steps;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataPropertiesApiTest {

    private static String existingSubject;
    private static final String nonExistingSubject = "nonExistingSubject";

    @BeforeAll
    public static void setup() {
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        existingSubject = Steps.getMetadataSubjectFromPath(allMetadataPaths.get(0));
    }

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

    @Test
    public void testAll() {
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        allMetadataPaths.forEach(s -> {
            String subject = Steps.getMetadataSubjectFromPath(s);
            Response metadata = Steps.getMetadata(subject);
        });
    }

    @Test
    public void readFoundProperties() {
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        allMetadataPaths.forEach(s -> {
            String subject = Steps.getMetadataSubjectFromPath(s);
            Response metadata = Steps.getMetadata(subject);
            Object o = metadata.jsonPath().get("$");
            System.out.println(o.toString());
        });
    }
}
