package org.springframework.boot.validation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidationException;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/validation/MessageInterpolatorFactory.class */
public class MessageInterpolatorFactory implements ObjectFactory<MessageInterpolator> {
    private static final Set<String> FALLBACKS;
    private final MessageSource messageSource;

    static {
        Set<String> fallbacks = new LinkedHashSet<>();
        fallbacks.add("org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator");
        FALLBACKS = Collections.unmodifiableSet(fallbacks);
    }

    public MessageInterpolatorFactory() {
        this(null);
    }

    public MessageInterpolatorFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.ObjectFactory
    public MessageInterpolator getObject() throws ValidationException, BeansException {
        MessageInterpolator messageInterpolator = getMessageInterpolator();
        if (this.messageSource != null) {
            return new MessageSourceMessageInterpolator(this.messageSource, messageInterpolator);
        }
        return messageInterpolator;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.validation.ValidationException */
    private MessageInterpolator getMessageInterpolator() throws ValidationException {
        try {
            return Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
        } catch (ValidationException ex) {
            MessageInterpolator fallback = getFallback();
            if (fallback != null) {
                return fallback;
            }
            throw ex;
        }
    }

    private MessageInterpolator getFallback() {
        for (String fallback : FALLBACKS) {
            try {
                return getFallback(fallback);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private MessageInterpolator getFallback(String fallback) throws BeanInstantiationException, IllegalArgumentException {
        Class<?> interpolatorClass = ClassUtils.resolveClassName(fallback, null);
        Object interpolator = BeanUtils.instantiateClass(interpolatorClass);
        return (MessageInterpolator) interpolator;
    }
}
