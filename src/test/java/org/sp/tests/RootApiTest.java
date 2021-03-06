package org.sp.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sp.Services;

import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sp.MetadataRestAssured.given;

public class RootApiTest {

    @Test
    @DisplayName("Should process root element")
    public void shouldReceiveRoot() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.ROOT)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.HTML.withCharset("utf-8").replaceAll("\\s", ""), extract.contentType()),
                () -> assertEquals("Metadata Server Mock:", extract.xmlPath(HTML).getString("html.body.h3")),
                () -> assertThat(extract.xmlPath(HTML).getList("html.body.ul.li.a"))
                        .as("Metadata list should contain subjects")
                        .isNotEmpty()
                        .allMatch(e -> ((String) e).startsWith("metadata/")),
                () -> assertThat(extract.htmlPath().getList("html.body.ul.li.a['@href']"))
                        .as("Metadata list should contain subjects")
                        .isNotEmpty()
                        .allMatch(e -> ((String) e).startsWith("metadata/")),
                () -> assertThat(extract.htmlPath().getList("html.body.ul.li.a['@href']"))
                        .containsExactlyElementsOf(extract.htmlPath().getList("html.body.ul.li.a"))
        );

    }

    @Test
    @DisplayName("Should process root element")
    public void shouldReceiveRootAlt() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath("")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.HTML.withCharset("utf-8").replaceAll("\\s", ""), extract.contentType()),
                () -> assertEquals("Metadata Server Mock:", extract.xmlPath(HTML).getString("html.body.h3")),
                () -> assertThat(extract.htmlPath().getList("html.body.ul.li.a"))
                        .as("Metadata list should contain subjects")
                        .isNotEmpty()
                        .allMatch(e -> ((String) e).startsWith("metadata/")),
                () -> assertThat(extract.htmlPath().getList("html.body.ul.li.a['@href']"))
                        .as("Metadata list should contain subjects")
                        .isNotEmpty()
                        .allMatch(e -> ((String) e).startsWith("metadata/")),
                () -> assertThat(extract.htmlPath().getList("html.body.ul.li.a['@href']"))
                        .containsExactlyElementsOf(extract.htmlPath().getList("html.body.ul.li.a"))
        );

    }

    @Test
    @DisplayName("Should fail if unknown endpoint response is valid")
    public void shouldReceive404() {
        //TODO: nice to check XSS here as well
        ExtractableResponse<Response> extract =
                given()
                        .basePath("/unknownPath")
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, extract.statusCode()),
                () -> assertEquals(ContentType.HTML.withCharset("utf-8").replaceAll("\\s", ""), extract.contentType()),
                () -> assertEquals("GET /unknownPath", extract.xmlPath(HTML).getString("html.body"))
        );
    }
}