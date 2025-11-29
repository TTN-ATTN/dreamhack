package org.springframework.web.context;

import javax.servlet.ServletConfig;
import org.springframework.beans.factory.Aware;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/ServletConfigAware.class */
public interface ServletConfigAware extends Aware {
    void setServletConfig(ServletConfig servletConfig);
}
