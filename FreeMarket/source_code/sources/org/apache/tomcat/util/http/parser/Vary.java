package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/parser/Vary.class */
public class Vary {
    private Vary() {
    }

    public static void parseVary(StringReader input, Set<String> result) throws IOException {
        TokenList.parseTokenList(input, result);
    }
}
