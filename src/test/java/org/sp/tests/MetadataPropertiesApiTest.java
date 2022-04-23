package org.sp.tests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.sp.AllowedProperties;
import org.sp.Services;
import org.sp.Steps;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

public class MetadataPropertiesApiTest {

    @Test
    public void shouldSomething2() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
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
    public void shouldSomething32() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", "919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e")
                        .pathParam("property", "")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void shouldSosssmething32() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", "919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e")
                        .pathParam("property", "policy")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode())
        );
    }

    @Test
    public void shouldSomething3x2() {
        AllowedProperties.properties.forEach(p -> {
                    ExtractableResponse<Response> extract =
                            given()
                                    .basePath(Services.METADATA_PROPERTIES)
                                    .pathParam("property", p)
                                    .pathParam("subject", "919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e")
                                    .when()
                                    .get()
                                    .then()
                                    .extract();

                    assertAll(
                            () -> assertEquals(SC_OK, extract.statusCode())
                    );
                }
        );
    }

    @Test
    public void testAll(){
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        allMetadataPaths.forEach(s -> {
            String subject = Steps.getMetadataSubjectFromPath(s);
            Response metadata = Steps.getMetadata(subject);
        });
    }
}
