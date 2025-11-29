package org.springframework.boot.context.properties.bind;

import java.util.function.Supplier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/AggregateBinder.class */
abstract class AggregateBinder<T> {
    private final Binder.Context context;

    protected abstract boolean isAllowRecursiveBinding(ConfigurationPropertySource source);

    protected abstract Object bindAggregate(ConfigurationPropertyName name, Bindable<?> target, AggregateElementBinder elementBinder);

    protected abstract T merge(Supplier<T> existing, T additional);

    AggregateBinder(Binder.Context context) {
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public final Object bind(ConfigurationPropertyName name, Bindable<?> target, AggregateElementBinder elementBinder) {
        Object result = bindAggregate(name, target, elementBinder);
        Supplier<?> value = target.getValue();
        if (result == null || value == null) {
            return result;
        }
        return merge(value, result);
    }

    protected final Binder.Context getContext() {
        return this.context;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/AggregateBinder$AggregateSupplier.class */
    protected static class AggregateSupplier<T> {
        private final Supplier<T> supplier;
        private T supplied;

        public AggregateSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public T get() {
            if (this.supplied == null) {
                this.supplied = this.supplier.get();
            }
            return this.supplied;
        }

        public boolean wasSupplied() {
            return this.supplied != null;
        }
    }
}
