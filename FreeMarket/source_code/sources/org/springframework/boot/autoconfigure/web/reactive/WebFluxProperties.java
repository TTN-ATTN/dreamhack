package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.webflux")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties.class */
public class WebFluxProperties {
    private String basePath;
    private final Format format = new Format();
    private final Session session = new Session();
    private String staticPathPattern = "/**";

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = cleanBasePath(basePath);
    }

    private String cleanBasePath(String basePath) {
        String candidate = StringUtils.trimWhitespace(basePath);
        if (StringUtils.hasText(candidate)) {
            if (!candidate.startsWith("/")) {
                candidate = "/" + candidate;
            }
            if (candidate.endsWith("/")) {
                candidate = candidate.substring(0, candidate.length() - 1);
            }
        }
        return candidate;
    }

    public Format getFormat() {
        return this.format;
    }

    @DeprecatedConfigurationProperty(replacement = "server.reactive.session")
    public Session getSession() {
        return this.session;
    }

    public String getStaticPathPattern() {
        return this.staticPathPattern;
    }

    public void setStaticPathPattern(String staticPathPattern) {
        this.staticPathPattern = staticPathPattern;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$Format.class */
    public static class Format {
        private String date;
        private String time;
        private String dateTime;

        public String getDate() {
            return this.date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return this.time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDateTime() {
            return this.dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }

    @Deprecated
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$Session.class */
    public static class Session {
        private final Cookie cookie = new Cookie();

        @DeprecatedConfigurationProperty(replacement = "server.reactive.session.cookie")
        public Cookie getCookie() {
            return this.cookie;
        }
    }

    @Deprecated
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$Cookie.class */
    public static class Cookie {
        private SameSite sameSite;

        @DeprecatedConfigurationProperty(replacement = "server.reactive.session.cookie.same-site")
        public SameSite getSameSite() {
            return this.sameSite;
        }

        public void setSameSite(SameSite sameSite) {
            this.sameSite = sameSite;
        }
    }

    @Deprecated
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$SameSite.class */
    public enum SameSite {
        NONE("None"),
        LAX("Lax"),
        STRICT("Strict");

        private final String attribute;

        SameSite(String attribute) {
            this.attribute = attribute;
        }

        public String attribute() {
            return this.attribute;
        }
    }
}
