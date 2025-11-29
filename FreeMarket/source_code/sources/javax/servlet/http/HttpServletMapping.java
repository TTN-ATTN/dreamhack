package javax.servlet.http;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpServletMapping.class */
public interface HttpServletMapping {
    String getMatchValue();

    String getPattern();

    String getServletName();

    MappingMatch getMappingMatch();
}
