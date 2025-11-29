package freemarker.cache;

import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/PathGlobMatcher.class */
public class PathGlobMatcher extends TemplateSourceMatcher {
    private final String glob;
    private Pattern pattern;
    private boolean caseInsensitive;

    public PathGlobMatcher(String glob) {
        if (glob.startsWith("/")) {
            throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + glob);
        }
        this.glob = glob;
        buildPattern();
    }

    private void buildPattern() {
        this.pattern = StringUtil.globToRegularExpression(this.glob, this.caseInsensitive);
    }

    @Override // freemarker.cache.TemplateSourceMatcher
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        return this.pattern.matcher(sourceName).matches();
    }

    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        boolean lastCaseInsensitive = this.caseInsensitive;
        this.caseInsensitive = caseInsensitive;
        if (lastCaseInsensitive != caseInsensitive) {
            buildPattern();
        }
    }

    public PathGlobMatcher caseInsensitive(boolean caseInsensitive) {
        setCaseInsensitive(caseInsensitive);
        return this;
    }
}
