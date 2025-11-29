package javax.servlet.http;

import java.util.Enumeration;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpSessionContext.class */
public interface HttpSessionContext {
    @Deprecated
    HttpSession getSession(String str);

    @Deprecated
    Enumeration<String> getIds();
}
