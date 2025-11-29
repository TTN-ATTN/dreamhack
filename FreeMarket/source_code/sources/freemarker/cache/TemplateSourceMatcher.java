package freemarker.cache;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateSourceMatcher.class */
public abstract class TemplateSourceMatcher {
    abstract boolean matches(String str, Object obj) throws IOException;
}
