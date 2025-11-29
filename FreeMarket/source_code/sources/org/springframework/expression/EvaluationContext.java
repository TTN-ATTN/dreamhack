package org.springframework.expression;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/EvaluationContext.class */
public interface EvaluationContext {
    TypedValue getRootObject();

    List<PropertyAccessor> getPropertyAccessors();

    List<ConstructorResolver> getConstructorResolvers();

    List<MethodResolver> getMethodResolvers();

    @Nullable
    BeanResolver getBeanResolver();

    TypeLocator getTypeLocator();

    TypeConverter getTypeConverter();

    TypeComparator getTypeComparator();

    OperatorOverloader getOperatorOverloader();

    void setVariable(String name, @Nullable Object value);

    @Nullable
    Object lookupVariable(String name);

    default TypedValue assignVariable(String name, Supplier<TypedValue> valueSupplier) {
        TypedValue typedValue = valueSupplier.get();
        setVariable(name, typedValue.getValue());
        return typedValue;
    }
}
