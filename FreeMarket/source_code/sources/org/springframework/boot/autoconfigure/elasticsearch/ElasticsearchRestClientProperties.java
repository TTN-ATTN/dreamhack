package org.springframework.boot.autoconfigure.elasticsearch;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch.restclient")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientProperties.class */
public class ElasticsearchRestClientProperties {
    private final Sniffer sniffer = new Sniffer();

    public Sniffer getSniffer() {
        return this.sniffer;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientProperties$Sniffer.class */
    public static class Sniffer {
        private Duration interval = Duration.ofMinutes(5);
        private Duration delayAfterFailure = Duration.ofMinutes(1);

        public Duration getInterval() {
            return this.interval;
        }

        public void setInterval(Duration interval) {
            this.interval = interval;
        }

        public Duration getDelayAfterFailure() {
            return this.delayAfterFailure;
        }

        public void setDelayAfterFailure(Duration delayAfterFailure) {
            this.delayAfterFailure = delayAfterFailure;
        }
    }
}
