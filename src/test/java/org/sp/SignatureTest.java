package org.sp;


import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.sp.MetadataRestAssured.given;

@Slf4j
public class SignatureTest extends MetadataServerTest {

    @Test
    @DisplayName("Should have valid signatures")
    public void signaturesCheck() {
        ExtractableResponse<Response> extract =
                given()
                        .basePath(Services.METADATA_SUBJECT)
                        .pathParam("subject", existingSubject)
                        .contentType(ContentType.JSON)
                        .when()
                        .get()
                        .then()
                        .extract();

        assertAll(
                () -> assertEquals(SC_OK, extract.statusCode()),
                () -> assertEquals(ContentType.JSON.toString(), extract.contentType())
        );

        List.of("url", "name", "ticker", "decimals", "logo", "description")
                .forEach(p -> {
                            String value = extract.jsonPath().getString(p + ".value");
                            Map<String, String> sig = extract.jsonPath().getMap(p + ".signatures[0]");
                            assertTrue(checkSignature(value, sig.get("signature"), sig.get("publicKey")));
                        }
                );
    }

    private boolean checkSignature(String message, String signature, String publicKey) {
        byte[] sig = signature.getBytes(StandardCharsets.UTF_8);
        byte[] msg = message.getBytes(StandardCharsets.UTF_8);
        byte[] pubKey = publicKey.getBytes(StandardCharsets.UTF_8);

        Signer verifier = new Ed25519Signer();
        verifier.init(false, new Ed25519PublicKeyParameters(pubKey, 0));
        verifier.update(msg, 0, msg.length);
        return verifier.verifySignature(sig);
    }
}
