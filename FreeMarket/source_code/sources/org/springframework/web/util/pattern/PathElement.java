package org.springframework.web.util.pattern;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/pattern/PathElement.class */
abstract class PathElement {
    protected static final int WILDCARD_WEIGHT = 100;
    protected static final int CAPTURE_VARIABLE_WEIGHT = 1;
    protected static final MultiValueMap<String, String> NO_PARAMETERS = new LinkedMultiValueMap();
    protected final int pos;
    protected final char separator;

    @Nullable
    protected PathElement next;

    @Nullable
    protected PathElement prev;

    public abstract boolean matches(int candidatePos, PathPattern.MatchingContext matchingContext);

    public abstract int getNormalizedLength();

    public abstract char[] getChars();

    PathElement(int pos, char separator) {
        this.pos = pos;
        this.separator = separator;
    }

    public int getCaptureCount() {
        return 0;
    }

    public int getWildcardCount() {
        return 0;
    }

    public int getScore() {
        return 0;
    }

    public boolean isLiteral() {
        return false;
    }

    protected final boolean isNoMorePattern() {
        return this.next == null;
    }
}
