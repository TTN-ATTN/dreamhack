package org.springframework.validation.beanvalidation;

import java.io.IOException;
import javax.validation.ValidationException;
import org.apache.commons.logging.LogFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/validation/beanvalidation/OptionalValidatorFactoryBean.class */
public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean {
    @Override // org.springframework.validation.beanvalidation.LocalValidatorFactoryBean, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NoSuchMethodException, IOException, SecurityException {
        try {
            super.afterPropertiesSet();
        } catch (ValidationException e) {
            LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation provider", e);
        }
    }
}
