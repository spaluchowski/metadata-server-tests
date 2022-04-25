package org.sp;

import com.github.dzieciou.testing.curl.CurlRestAssuredConfigFactory;
import com.github.dzieciou.testing.curl.Options;
import com.github.dzieciou.testing.curl.Platform;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

@Slf4j
public class MetadataRestAssured {
    private static final String APP_URL = "http://metadata-server-mock.herokuapp.com";

    public static RequestSpecification given() {
        log.info("Sending request");

        return RestAssured.given()
                .config(
                        CurlRestAssuredConfigFactory.createConfig(
                                Options.builder()
                                        .useLogLevel(Level.INFO)
                                        .targetPlatform(Platform.UNIX)
                                        .build()
                        )
                )
                .log().all()
                .filter(new ResponseLoggingFilter())
                .baseUri(APP_URL);
    }
}
