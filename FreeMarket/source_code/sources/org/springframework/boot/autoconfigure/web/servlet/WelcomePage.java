package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/WelcomePage.class */
final class WelcomePage {
    static final WelcomePage UNRESOLVED = new WelcomePage(null, false);
    private final String viewName;
    private final boolean templated;

    private WelcomePage(String viewName, boolean templated) {
        this.viewName = viewName;
        this.templated = templated;
    }

    String getViewName() {
        return this.viewName;
    }

    boolean isTemplated() {
        return this.templated;
    }

    static WelcomePage resolve(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext, Resource indexHtmlResource, String staticPathPattern) {
        if (indexHtmlResource != null && "/**".equals(staticPathPattern)) {
            return new WelcomePage("forward:index.html", false);
        }
        if (templateAvailabilityProviders.getProvider(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, applicationContext) != null) {
            return new WelcomePage(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, true);
        }
        return UNRESOLVED;
    }
}
