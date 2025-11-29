package org.apache.catalina.filters;

import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/filters/FilterBase.class */
public abstract class FilterBase implements Filter {
    protected static final StringManager sm = StringManager.getManager((Class<?>) FilterBase.class);

    protected abstract Log getLogger();

    @Override // javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> paramNames = filterConfig.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!IntrospectionUtils.setProperty(this, paramName, filterConfig.getInitParameter(paramName))) {
                String msg = sm.getString("filterbase.noSuchProperty", paramName, getClass().getName());
                if (isConfigProblemFatal()) {
                    throw new ServletException(msg);
                }
                getLogger().warn(msg);
            }
        }
    }

    protected boolean isConfigProblemFatal() {
        return false;
    }
}
