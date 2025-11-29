package org.springframework.web.servlet.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/config/TilesConfigurerBeanDefinitionParser.class */
public class TilesConfigurerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcTilesConfigurer";

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.tiles3.TilesConfigurer";
    }

    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<Element> childElements = DomUtils.getChildElementsByTagName(element, "definitions");
        if (!childElements.isEmpty()) {
            List<String> locations = new ArrayList<>(childElements.size());
            for (Element childElement : childElements) {
                locations.add(childElement.getAttribute("location"));
            }
            builder.addPropertyValue("definitions", StringUtils.toStringArray(locations));
        }
        if (element.hasAttribute("check-refresh")) {
            builder.addPropertyValue("checkRefresh", element.getAttribute("check-refresh"));
        }
        if (element.hasAttribute("validate-definitions")) {
            builder.addPropertyValue("validateDefinitions", element.getAttribute("validate-definitions"));
        }
        if (element.hasAttribute("definitions-factory")) {
            builder.addPropertyValue("definitionsFactoryClass", element.getAttribute("definitions-factory"));
        }
        if (element.hasAttribute("preparer-factory")) {
            builder.addPropertyValue("preparerFactoryClass", element.getAttribute("preparer-factory"));
        }
    }
}
