package freemarker.cache;

import java.io.IOException;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/PathRegexMatcher.class */
public class PathRegexMatcher extends TemplateSourceMatcher {
    private final Pattern pattern;

    public PathRegexMatcher(String regex) {
        if (regex.startsWith("/")) {
            throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + regex);
        }
        this.pattern = Pattern.compile(regex);
    }

    @Override // freemarker.cache.TemplateSourceMatcher
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        return this.pattern.matcher(sourceName).matches();
    }
}
