package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/util/pattern/PathPatternParser.class */
public class PathPatternParser {
    private boolean matchOptionalTrailingSeparator = true;
    private boolean caseSensitive = true;
    private PathContainer.Options pathOptions = PathContainer.Options.HTTP_PATH;
    public static final PathPatternParser defaultInstance = new PathPatternParser() { // from class: org.springframework.web.util.pattern.PathPatternParser.1
        @Override // org.springframework.web.util.pattern.PathPatternParser
        public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
            raiseError();
        }

        @Override // org.springframework.web.util.pattern.PathPatternParser
        public void setCaseSensitive(boolean caseSensitive) {
            raiseError();
        }

        @Override // org.springframework.web.util.pattern.PathPatternParser
        public void setPathOptions(PathContainer.Options pathOptions) {
            raiseError();
        }

        private void raiseError() {
            throw new UnsupportedOperationException("This is a read-only, shared instance that cannot be modified");
        }
    };

    public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
        this.matchOptionalTrailingSeparator = matchOptionalTrailingSeparator;
    }

    public boolean isMatchOptionalTrailingSeparator() {
        return this.matchOptionalTrailingSeparator;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setPathOptions(PathContainer.Options pathOptions) {
        this.pathOptions = pathOptions;
    }

    public PathContainer.Options getPathOptions() {
        return this.pathOptions;
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        return new InternalPathPatternParser(this).parse(pathPattern);
    }
}
