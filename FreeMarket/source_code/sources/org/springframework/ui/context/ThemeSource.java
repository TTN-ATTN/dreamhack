package org.springframework.ui.context;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/ui/context/ThemeSource.class */
public interface ThemeSource {
    @Nullable
    Theme getTheme(String themeName);
}
