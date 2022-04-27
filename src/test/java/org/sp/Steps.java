package org.sp;

import io.restassured.path.xml.XmlPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.sp.MetadataRestAssured.given;

@Slf4j
public class Steps {

    public static List<String> getAllMetadataPaths() {
        log.info("Get root");
        XmlPath xmlPath = given()
                .basePath(Services.ROOT)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .xmlPath(XmlPath.CompatibilityMode.HTML);
        return xmlPath.getList("html.body.ul.li");
    }

    public static String getMetadataSubjectFromPath(String path) {
        return path.split("/")[1];
    }

    public static Response getMetadata(String subject) {
        log.info("Get metadata for subject='{}'", subject);
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_SUBJECT)
                        .pathParam("subject", subject)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract();
        return extract.response();
    }

    public static Response getMetadata(String subject, String property) {
        log.info("Get metadata property={} for subject='{}'", property, subject);
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTIES)
                        .pathParam("subject", subject)
                        .pathParam("property", property)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract();
        return extract.response();
    }
}
