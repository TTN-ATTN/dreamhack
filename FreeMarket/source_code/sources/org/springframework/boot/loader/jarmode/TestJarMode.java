package org.springframework.boot.loader.jarmode;

import java.util.Arrays;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jarmode/TestJarMode.class */
class TestJarMode implements JarMode {
    TestJarMode() {
    }

    @Override // org.springframework.boot.loader.jarmode.JarMode
    public boolean accepts(String mode) {
        return "test".equals(mode);
    }

    @Override // org.springframework.boot.loader.jarmode.JarMode
    public void run(String mode, String[] args) {
        System.out.println("running in " + mode + " jar mode " + Arrays.asList(args));
    }
}
