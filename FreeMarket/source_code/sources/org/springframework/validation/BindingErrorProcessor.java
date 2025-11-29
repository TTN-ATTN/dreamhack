package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/validation/BindingErrorProcessor.class */
public interface BindingErrorProcessor {
    void processMissingFieldError(String missingField, BindingResult bindingResult);

    void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult);
}
