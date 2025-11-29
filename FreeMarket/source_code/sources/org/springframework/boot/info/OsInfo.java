package org.springframework.boot.info;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/info/OsInfo.class */
public class OsInfo {
    private final String name = System.getProperty("os.name");
    private final String version = System.getProperty("os.version");
    private final String arch = System.getProperty("os.arch");

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getArch() {
        return this.arch;
    }
}
