package freemarker.cache;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/OrMatcher.class */
public class OrMatcher extends TemplateSourceMatcher {
    private final TemplateSourceMatcher[] matchers;

    public OrMatcher(TemplateSourceMatcher... matchers) {
        if (matchers.length == 0) {
            throw new IllegalArgumentException("Need at least 1 matcher, had 0.");
        }
        this.matchers = matchers;
    }

    @Override // freemarker.cache.TemplateSourceMatcher
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        for (TemplateSourceMatcher matcher : this.matchers) {
            if (matcher.matches(sourceName, templateSource)) {
                return true;
            }
        }
        return false;
    }
}
