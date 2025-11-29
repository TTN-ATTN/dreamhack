package org.springframework.boot.autoconfigure.influx;

import org.influxdb.InfluxDB;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/influx/InfluxDbCustomizer.class */
public interface InfluxDbCustomizer {
    void customize(InfluxDB influxDb);
}
