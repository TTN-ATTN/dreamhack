package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/parser/EntityTag.class */
public class EntityTag {
    public static Boolean compareEntityTag(StringReader input, boolean compareWeak, String resourceETag) throws IOException {
        String comparisonETag;
        if (compareWeak && resourceETag.startsWith("W/")) {
            comparisonETag = resourceETag.substring(2);
        } else {
            comparisonETag = resourceETag;
        }
        Boolean result = Boolean.FALSE;
        while (true) {
            boolean strong = false;
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, "W/")) {
                case EOF:
                    return null;
                case NOT_FOUND:
                    strong = true;
                    break;
                case FOUND:
                    strong = false;
                    break;
            }
            String value = HttpParser.readQuotedString(input, true);
            if (value == null) {
                return null;
            }
            if ((strong || compareWeak) && comparisonETag.equals(value)) {
                result = Boolean.TRUE;
            }
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, ",")) {
                case EOF:
                    return result;
                case NOT_FOUND:
                    return null;
            }
        }
    }
}
