package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class PlatformUtilsTest {

    @Test
    @EnabledOnOs(value = OS.MAC)
    void isRunningOnRosetta2() {
        boolean runningOnRosetta2 = PlatformUtils.isRunningOnRosetta2();
    }
}