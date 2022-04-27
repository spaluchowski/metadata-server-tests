package org.sp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.util.List;

@Slf4j
public class MetadataServerTest {

    protected static String existingSubject;
    protected static final String nonExistingSubject = "nonExistingSubject";

    @BeforeAll
    public static void setup() {
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        existingSubject = Steps.getMetadataSubjectFromPath(allMetadataPaths.get(0));
    }

    @BeforeEach
    public void beforeTest(TestInfo testInfo){
        log.info("Running test={}, name={}", testInfo.getTestMethod(), testInfo.getDisplayName());
    }
}
