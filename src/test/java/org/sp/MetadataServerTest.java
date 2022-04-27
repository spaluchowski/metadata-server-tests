package org.sp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.sp.Steps.getAllMetadataPaths;

@Slf4j
public class MetadataServerTest {

    protected static List<String> existingSubjects = new ArrayList<>();
    protected static String existingSubject;
    protected static final String nonExistingSubject = "nonExistingSubject";

    @BeforeAll
    public static void setup() {
        List<String> allMetadataPaths = getAllMetadataPaths();
        existingSubjects = allMetadataPaths.stream()
                .map(Steps::getMetadataSubjectFromPath)
                .collect(Collectors.toList());
        existingSubject = existingSubjects.get(0);
    }

    @BeforeEach
    public void beforeTest(TestInfo testInfo) {
        log.info("Running test={}, name={}", testInfo.getTestMethod(), testInfo.getDisplayName());
    }
}
