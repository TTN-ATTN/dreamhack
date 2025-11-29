package org.springframework.boot.autoconfigure.netty;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.netty")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/netty/NettyProperties.class */
public class NettyProperties {
    private LeakDetection leakDetection;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/netty/NettyProperties$LeakDetection.class */
    public enum LeakDetection {
        DISABLED,
        SIMPLE,
        ADVANCED,
        PARANOID
    }

    public LeakDetection getLeakDetection() {
        return this.leakDetection;
    }

    public void setLeakDetection(LeakDetection leakDetection) {
        this.leakDetection = leakDetection;
    }
}
