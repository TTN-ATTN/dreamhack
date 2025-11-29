package org.springframework.web.servlet.i18n;

import java.util.Locale;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/i18n/AbstractLocaleResolver.class */
public abstract class AbstractLocaleResolver implements LocaleResolver {

    @Nullable
    private Locale defaultLocale;

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }
}
