package org.apache.catalina.valves.rewrite;

import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/valves/rewrite/RewriteMap.class */
public interface RewriteMap {
    String setParameters(String str);

    String lookup(String str);

    default void setParameters(String... params) {
        if (params == null) {
            return;
        }
        if (params.length > 1) {
            throw new IllegalArgumentException(StringManager.getManager((Class<?>) RewriteMap.class).getString("rewriteMap.tooManyParameters"));
        }
        setParameters(params[0]);
    }
}
