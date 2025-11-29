package org.springframework.boot.loader.jarmode;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jarmode/JarMode.class */
public interface JarMode {
    boolean accepts(String mode);

    void run(String mode, String[] args);
}
