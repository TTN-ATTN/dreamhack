package org.springframework.boot.autoconfigure.mustache;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@ConfigurationProperties(prefix = "spring.mustache")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mustache/MustacheProperties.class */
public class MustacheProperties {
    private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".mustache";
    private String[] viewNames;
    private String requestContextAttribute;
    private final Servlet servlet = new Servlet();
    private final Reactive reactive = new Reactive();
    private boolean enabled = true;
    private Charset charset = DEFAULT_CHARSET;
    private boolean checkTemplateLocation = true;
    private String prefix = "classpath:/templates/";
    private String suffix = DEFAULT_SUFFIX;

    public Servlet getServlet() {
        return this.servlet;
    }

    public Reactive getReactive() {
        return this.reactive;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

    public String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setRequestContextAttribute(String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        if (this.charset != null) {
            return this.charset.name();
        }
        return null;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isCheckTemplateLocation() {
        return this.checkTemplateLocation;
    }

    public void setCheckTemplateLocation(boolean checkTemplateLocation) {
        this.checkTemplateLocation = checkTemplateLocation;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.allow-request-override")
    @Deprecated
    public boolean isAllowRequestOverride() {
        return this.servlet.isAllowRequestOverride();
    }

    @Deprecated
    public void setAllowRequestOverride(boolean allowRequestOverride) {
        this.servlet.setAllowRequestOverride(allowRequestOverride);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.allow-session-override")
    @Deprecated
    public boolean isAllowSessionOverride() {
        return this.servlet.isAllowSessionOverride();
    }

    @Deprecated
    public void setAllowSessionOverride(boolean allowSessionOverride) {
        this.servlet.setAllowSessionOverride(allowSessionOverride);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.cache")
    @Deprecated
    public boolean isCache() {
        return this.servlet.isCache();
    }

    @Deprecated
    public void setCache(boolean cache) {
        this.servlet.setCache(cache);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.content-type")
    @Deprecated
    public MimeType getContentType() {
        return this.servlet.getContentType();
    }

    @Deprecated
    public void setContentType(MimeType contentType) {
        this.servlet.setContentType(contentType);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.expose-request-attributes")
    @Deprecated
    public boolean isExposeRequestAttributes() {
        return this.servlet.isExposeRequestAttributes();
    }

    @Deprecated
    public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
        this.servlet.setExposeRequestAttributes(exposeRequestAttributes);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.expose-session-attributes")
    @Deprecated
    public boolean isExposeSessionAttributes() {
        return this.servlet.isExposeSessionAttributes();
    }

    @Deprecated
    public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
        this.servlet.setExposeSessionAttributes(exposeSessionAttributes);
    }

    @DeprecatedConfigurationProperty(replacement = "spring.mustache.servlet.expose-spring-macro-helpers")
    @Deprecated
    public boolean isExposeSpringMacroHelpers() {
        return this.servlet.isExposeSessionAttributes();
    }

    @Deprecated
    public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
        this.servlet.setExposeSpringMacroHelpers(exposeSpringMacroHelpers);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mustache/MustacheProperties$Servlet.class */
    public static class Servlet {
        private boolean cache;
        private boolean allowRequestOverride = false;
        private boolean allowSessionOverride = false;
        private MimeType contentType = MustacheProperties.DEFAULT_CONTENT_TYPE;
        private boolean exposeRequestAttributes = false;
        private boolean exposeSessionAttributes = false;
        private boolean exposeSpringMacroHelpers = true;

        public boolean isAllowRequestOverride() {
            return this.allowRequestOverride;
        }

        public void setAllowRequestOverride(boolean allowRequestOverride) {
            this.allowRequestOverride = allowRequestOverride;
        }

        public boolean isAllowSessionOverride() {
            return this.allowSessionOverride;
        }

        public void setAllowSessionOverride(boolean allowSessionOverride) {
            this.allowSessionOverride = allowSessionOverride;
        }

        public boolean isCache() {
            return this.cache;
        }

        public void setCache(boolean cache) {
            this.cache = cache;
        }

        public MimeType getContentType() {
            return this.contentType;
        }

        public void setContentType(MimeType contentType) {
            this.contentType = contentType;
        }

        public boolean isExposeRequestAttributes() {
            return this.exposeRequestAttributes;
        }

        public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
            this.exposeRequestAttributes = exposeRequestAttributes;
        }

        public boolean isExposeSessionAttributes() {
            return this.exposeSessionAttributes;
        }

        public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
            this.exposeSessionAttributes = exposeSessionAttributes;
        }

        public boolean isExposeSpringMacroHelpers() {
            return this.exposeSpringMacroHelpers;
        }

        public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
            this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mustache/MustacheProperties$Reactive.class */
    public static class Reactive {
        private List<MediaType> mediaTypes;

        public List<MediaType> getMediaTypes() {
            return this.mediaTypes;
        }

        public void setMediaTypes(List<MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
        }
    }
}
