package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspTagException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/ArgumentAware.class */
public interface ArgumentAware {
    void addArgument(@Nullable Object argument) throws JspTagException;
}
