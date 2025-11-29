package org.springframework.validation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/validation/Validator.class */
public interface Validator {
    boolean supports(Class<?> clazz);

    void validate(Object target, Errors errors);
}
