package com.hotel.lostfound;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    static final ApplicationModules MODULES = ApplicationModules.of(LostFoundApplication.class);

    @Test
    void verifiesModularStructure() {
        MODULES.verify();
    }

    @Test
    void writesModuleDocumentation() {
        new Documenter(MODULES).writeDocumentation();
    }
}
