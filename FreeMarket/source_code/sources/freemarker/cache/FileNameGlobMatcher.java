package freemarker.cache;

import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/FileNameGlobMatcher.class */
public class FileNameGlobMatcher extends TemplateSourceMatcher {
    private final String glob;
    private Pattern pattern;
    private boolean caseInsensitive;

    public FileNameGlobMatcher(String glob) {
        if (glob.indexOf(47) != -1) {
            throw new IllegalArgumentException("A file name glob can't contain \"/\": " + glob);
        }
        this.glob = glob;
        buildPattern();
    }

    private void buildPattern() {
        this.pattern = StringUtil.globToRegularExpression("**/" + this.glob, this.caseInsensitive);
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

    public FileNameGlobMatcher caseInsensitive(boolean caseInsensitive) {
        setCaseInsensitive(caseInsensitive);
        return this;
    }
}
