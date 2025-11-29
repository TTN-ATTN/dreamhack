package org.springframework.web.server.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/i18n/LocaleContextResolver.class */
public interface LocaleContextResolver {
    LocaleContext resolveLocaleContext(ServerWebExchange exchange);

    void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext localeContext);
}
