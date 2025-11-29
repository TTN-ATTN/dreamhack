package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/pattern/LiteralPathElement.class */
class LiteralPathElement extends PathElement {
    private final char[] text;
    private final int len;
    private final boolean caseSensitive;

    public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator) {
        super(pos, separator);
        this.len = literalText.length;
        this.caseSensitive = caseSensitive;
        if (caseSensitive) {
            this.text = literalText;
            return;
        }
        this.text = new char[literalText.length];
        for (int i = 0; i < this.len; i++) {
            this.text[i] = Character.toLowerCase(literalText[i]);
        }
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex >= matchingContext.pathLength) {
            return false;
        }
        PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
        if (!(element instanceof PathContainer.PathSegment)) {
            return false;
        }
        String value = ((PathContainer.PathSegment) element).valueToMatch();
        if (value.length() != this.len) {
            return false;
        }
        if (this.caseSensitive) {
            for (int i = 0; i < this.len; i++) {
                if (value.charAt(i) != this.text[i]) {
                    return false;
                }
            }
        } else {
            for (int i2 = 0; i2 < this.len; i2++) {
                if (Character.toLowerCase(value.charAt(i2)) != this.text[i2]) {
                    return false;
                }
            }
        }
        int pathIndex2 = pathIndex + 1;
        if (!isNoMorePattern()) {
            return this.next != null && this.next.matches(pathIndex2, matchingContext);
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = pathIndex2;
            return true;
        }
        if (pathIndex2 == matchingContext.pathLength) {
            return true;
        }
        return matchingContext.isMatchOptionalTrailingSeparator() && pathIndex2 + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex2);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return this.len;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return this.text;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean isLiteral() {
        return true;
    }

    public String toString() {
        return "Literal(" + String.valueOf(this.text) + ")";
    }
}
