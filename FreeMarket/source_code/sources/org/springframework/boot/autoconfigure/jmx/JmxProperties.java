package org.springframework.boot.autoconfigure.jmx;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jmx")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jmx/JmxProperties.class */
public class JmxProperties {
    private boolean enabled = false;
    private boolean uniqueNames = false;
    private String server = "mbeanServer";
    private String defaultDomain;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUniqueNames() {
        return this.uniqueNames;
    }

    public void setUniqueNames(boolean uniqueNames) {
        this.uniqueNames = uniqueNames;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDefaultDomain() {
        return this.defaultDomain;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }
}
