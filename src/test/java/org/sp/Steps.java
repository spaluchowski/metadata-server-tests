package org.sp;

import io.restassured.path.xml.XmlPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

import static org.sp.MetadataRestAssured.given;

public class Steps {

    public static List<String> getAllMetadataPaths() {
        XmlPath xmlPath = given()
                .basePath(Services.ROOT)
                .when()
                .get()
                .then()
                .extract()
                .xmlPath(XmlPath.CompatibilityMode.HTML);
        return xmlPath.getList("html.body.ul.li");
    }

    public static String getMetadataSubjectFromPath(String path) {
        return path.split("/")[1];
    }

    public static Response getMetadata(String subject) {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_PROPERTY)
                        .pathParam("subject", subject)
                        .when()
                        .get()
                        .then()
                        .extract();
        return null;
    }
}
