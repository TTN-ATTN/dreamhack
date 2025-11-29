package org.springframework.expression;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/TypeLocator.class */
public interface TypeLocator {
    Class<?> findType(String typeName) throws EvaluationException;
}
