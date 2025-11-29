package freemarker.cache;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/NotMatcher.class */
public class NotMatcher extends TemplateSourceMatcher {
    private final TemplateSourceMatcher matcher;

    public NotMatcher(TemplateSourceMatcher matcher) {
        this.matcher = matcher;
    }

    @Override // freemarker.cache.TemplateSourceMatcher
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        return !this.matcher.matches(sourceName, templateSource);
    }
}
