package freemarker.cache;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/FileExtensionMatcher.class */
public class FileExtensionMatcher extends TemplateSourceMatcher {
    private final String extension;
    private boolean caseInsensitive = true;

    public FileExtensionMatcher(String extension) {
        if (extension.indexOf(47) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"/\": " + extension);
        }
        if (extension.indexOf(42) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
        }
        if (extension.indexOf(63) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
        }
        if (extension.startsWith(".")) {
            throw new IllegalArgumentException("A file extension can't start with \".\": " + extension);
        }
        this.extension = extension;
    }

    @Override // freemarker.cache.TemplateSourceMatcher
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        int ln = sourceName.length();
        int extLn = this.extension.length();
        if (ln < extLn + 1 || sourceName.charAt((ln - extLn) - 1) != '.') {
            return false;
        }
        return sourceName.regionMatches(this.caseInsensitive, ln - extLn, this.extension, 0, extLn);
    }

    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public FileExtensionMatcher caseInsensitive(boolean caseInsensitive) {
        setCaseInsensitive(caseInsensitive);
        return this;
    }
}
