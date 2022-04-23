package org.sp;

import com.github.dzieciou.testing.curl.CurlRestAssuredConfigFactory;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

public class MetadataRestAssured {

    private static final String APP_URL = "http://metadata-server-mock.herokuapp.com";
    public static RequestSpecification given() {
        RestAssuredConfig config = CurlRestAssuredConfigFactory.createConfig();

        return RestAssured.given()
                .config(config)
                .log().all()
                .filter(new ResponseLoggingFilter())
                .baseUri(APP_URL);
    }
}
