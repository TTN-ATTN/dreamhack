package org.springframework.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/NativeDetector.class */
public abstract class NativeDetector {
    private static final boolean imageCode;

    static {
        imageCode = System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }

    public static boolean inNativeImage() {
        return imageCode;
    }
}
