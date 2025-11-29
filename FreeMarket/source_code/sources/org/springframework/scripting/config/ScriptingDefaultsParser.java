package org.springframework.scripting.config;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scripting/config/ScriptingDefaultsParser.class */
class ScriptingDefaultsParser implements BeanDefinitionParser {
    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";

    ScriptingDefaultsParser() {
    }

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    public BeanDefinition parse(Element element, ParserContext parserContext) throws NoSuchBeanDefinitionException, BeanDefinitionStoreException {
        BeanDefinition bd = LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (StringUtils.hasText(refreshCheckDelay)) {
            bd.getPropertyValues().add("defaultRefreshCheckDelay", Long.valueOf(refreshCheckDelay));
        }
        String proxyTargetClass = element.getAttribute("proxy-target-class");
        if (StringUtils.hasText(proxyTargetClass)) {
            bd.getPropertyValues().add("defaultProxyTargetClass", new TypedStringValue(proxyTargetClass, (Class<?>) Boolean.class));
            return null;
        }
        return null;
    }
}
