package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Map;
import javax.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/method/annotation/ExtendedServletRequestDataBinder.class */
public class ExtendedServletRequestDataBinder extends ServletRequestDataBinder {
    public ExtendedServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ExtendedServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    @Override // org.springframework.web.bind.ServletRequestDataBinder
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map) request.getAttribute(attr);
        if (uriVars != null) {
            uriVars.forEach((name, value) -> {
                if (mpvs.contains(name)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("URI variable '" + name + "' overridden by request bind value.");
                        return;
                    }
                    return;
                }
                mpvs.addPropertyValue(name, value);
            });
        }
    }
}
