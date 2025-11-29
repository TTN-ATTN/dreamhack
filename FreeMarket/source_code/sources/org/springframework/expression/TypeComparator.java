package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/TypeComparator.class */
public interface TypeComparator {
    boolean canCompare(@Nullable Object firstObject, @Nullable Object secondObject);

    int compare(@Nullable Object firstObject, @Nullable Object secondObject) throws EvaluationException;
}
