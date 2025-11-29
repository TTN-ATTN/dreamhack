package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/ejb/config/JndiLookupBeanDefinitionParser.class */
class JndiLookupBeanDefinitionParser extends AbstractJndiLocatingBeanDefinitionParser {
    public static final String DEFAULT_VALUE = "default-value";
    public static final String DEFAULT_REF = "default-ref";
    public static final String DEFAULT_OBJECT = "defaultObject";

    JndiLookupBeanDefinitionParser() {
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected Class<?> getBeanClass(Element element) {
        return JndiObjectFactoryBean.class;
    }

    @Override // org.springframework.ejb.config.AbstractJndiLocatingBeanDefinitionParser, org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
    protected boolean isEligibleAttribute(String attributeName) {
        return (!super.isEligibleAttribute(attributeName) || DEFAULT_VALUE.equals(attributeName) || DEFAULT_REF.equals(attributeName)) ? false : true;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser, org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        String defaultValue = element.getAttribute(DEFAULT_VALUE);
        String defaultRef = element.getAttribute(DEFAULT_REF);
        if (StringUtils.hasLength(defaultValue)) {
            if (StringUtils.hasLength(defaultRef)) {
                parserContext.getReaderContext().error("<jndi-lookup> element is only allowed to contain either 'default-value' attribute OR 'default-ref' attribute, not both", element);
            }
            builder.addPropertyValue(DEFAULT_OBJECT, defaultValue);
        } else if (StringUtils.hasLength(defaultRef)) {
            builder.addPropertyValue(DEFAULT_OBJECT, new RuntimeBeanReference(defaultRef));
        }
    }
}
