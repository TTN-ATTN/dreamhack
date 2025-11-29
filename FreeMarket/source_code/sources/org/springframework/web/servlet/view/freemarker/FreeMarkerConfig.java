package org.springframework.web.servlet.view.freemarker;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerConfig.class */
public interface FreeMarkerConfig {
    Configuration getConfiguration();

    TaglibFactory getTaglibFactory();
}
