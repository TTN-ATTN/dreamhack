package org.springframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/WebApplicationInitializer.class */
public interface WebApplicationInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
