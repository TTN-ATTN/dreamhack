package org.springframework.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/AbstractUriTemplateHandler.class */
public abstract class AbstractUriTemplateHandler implements UriTemplateHandler {

    @Nullable
    private String baseUrl;
    private final Map<String, Object> defaultUriVariables = new HashMap();

    protected abstract URI expandInternal(String uriTemplate, Map<String, ?> uriVariables);

    protected abstract URI expandInternal(String uriTemplate, Object... uriVariables);

    public void setBaseUrl(@Nullable String baseUrl) {
        if (baseUrl != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(baseUrl).build();
            Assert.hasText(uriComponents.getScheme(), "'baseUrl' must have a scheme");
            Assert.hasText(uriComponents.getHost(), "'baseUrl' must have a host");
            Assert.isNull(uriComponents.getQuery(), "'baseUrl' cannot have a query");
            Assert.isNull(uriComponents.getFragment(), "'baseUrl' cannot have a fragment");
        }
        this.baseUrl = baseUrl;
    }

    @Nullable
    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setDefaultUriVariables(@Nullable Map<String, ?> defaultUriVariables) {
        this.defaultUriVariables.clear();
        if (defaultUriVariables != null) {
            this.defaultUriVariables.putAll(defaultUriVariables);
        }
    }

    public Map<String, ?> getDefaultUriVariables() {
        return Collections.unmodifiableMap(this.defaultUriVariables);
    }

    @Override // org.springframework.web.util.UriTemplateHandler
    public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
        if (!getDefaultUriVariables().isEmpty()) {
            HashMap map = new HashMap();
            map.putAll(getDefaultUriVariables());
            map.putAll(uriVariables);
            uriVariables = map;
        }
        URI url = expandInternal(uriTemplate, uriVariables);
        return insertBaseUrl(url);
    }

    @Override // org.springframework.web.util.UriTemplateHandler
    public URI expand(String uriTemplate, Object... uriVariables) {
        URI url = expandInternal(uriTemplate, uriVariables);
        return insertBaseUrl(url);
    }

    private URI insertBaseUrl(URI url) {
        try {
            String baseUrl = getBaseUrl();
            if (baseUrl != null && url.getHost() == null) {
                url = new URI(baseUrl + url.toString());
            }
            return url;
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL after inserting base URL: " + url, ex);
        }
    }
}
