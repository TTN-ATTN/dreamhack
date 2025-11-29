package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/SetCharacterEncodingFilter.class */
public class SetCharacterEncodingFilter extends FilterBase {
    private final Log log = LogFactory.getLog((Class<?>) SetCharacterEncodingFilter.class);
    private String encoding = null;
    private boolean ignore = false;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isIgnore() {
        return this.ignore;
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        String characterEncoding;
        if ((this.ignore || request.getCharacterEncoding() == null) && (characterEncoding = selectEncoding(request)) != null) {
            request.setCharacterEncoding(characterEncoding);
        }
        chain.doFilter(request, response);
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected Log getLogger() {
        return this.log;
    }

    protected String selectEncoding(ServletRequest request) {
        return this.encoding;
    }
}
