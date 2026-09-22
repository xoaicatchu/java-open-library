package com.example.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTests {

    ApplicationModules modules = ApplicationModules.of(SpringModulithApplication.class);

    @Test
    void verifyModules() {
        // Module boundary verification (no cyclic dependencies)
        modules.verify();
    }

    @Test
    void createDocumentation() {
        // DocumentationSnippet generation
        new Documenter(modules).writeModulesAsPlantUml();
    }
}
