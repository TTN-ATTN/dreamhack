package org.springframework.web.servlet.theme;

import org.springframework.web.servlet.ThemeResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/theme/AbstractThemeResolver.class */
public abstract class AbstractThemeResolver implements ThemeResolver {
    public static final String ORIGINAL_DEFAULT_THEME_NAME = "theme";
    private String defaultThemeName = "theme";

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    public String getDefaultThemeName() {
        return this.defaultThemeName;
    }
}
