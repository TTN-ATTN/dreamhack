package org.springframework.web.context;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.Aware;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/ServletContextAware.class */
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext servletContext);
}
