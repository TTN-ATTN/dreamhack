package org.springframework.boot.autoconfigure.influx;

import java.util.function.Supplier;
import okhttp3.OkHttpClient;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/influx/InfluxDbOkHttpClientBuilderProvider.class */
public interface InfluxDbOkHttpClientBuilderProvider extends Supplier<OkHttpClient.Builder> {
}
