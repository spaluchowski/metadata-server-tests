package org.sp;

import org.junit.jupiter.api.BeforeAll;

import java.util.List;

public class MetadataServerTest {

    protected static String existingSubject;
    protected static final String nonExistingSubject = "nonExistingSubject";

    @BeforeAll
    public static void setup() {
        List<String> allMetadataPaths = Steps.getAllMetadataPaths();
        existingSubject = Steps.getMetadataSubjectFromPath(allMetadataPaths.get(0));
    }
}
