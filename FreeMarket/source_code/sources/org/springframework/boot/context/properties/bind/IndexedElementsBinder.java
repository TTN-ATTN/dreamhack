package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.context.properties.bind.AggregateBinder;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/IndexedElementsBinder.class */
abstract class IndexedElementsBinder<T> extends AggregateBinder<T> {
    private static final String INDEX_ZERO = "[0]";

    IndexedElementsBinder(Binder.Context context) {
        super(context);
    }

    @Override // org.springframework.boot.context.properties.bind.AggregateBinder
    protected boolean isAllowRecursiveBinding(ConfigurationPropertySource source) {
        return source == null || (source instanceof IterableConfigurationPropertySource);
    }

    protected final void bindIndexed(ConfigurationPropertyName name, Bindable<?> target, AggregateElementBinder elementBinder, ResolvableType aggregateType, ResolvableType elementType, IndexedCollectionSupplier result) {
        for (ConfigurationPropertySource source : getContext().getSources()) {
            bindIndexed(source, name, target, elementBinder, result, aggregateType, elementType);
            if (result.wasSupplied() && result.get() != null) {
                return;
            }
        }
    }

    private void bindIndexed(ConfigurationPropertySource source, ConfigurationPropertyName root, Bindable<?> target, AggregateElementBinder elementBinder, IndexedCollectionSupplier collection, ResolvableType aggregateType, ResolvableType elementType) {
        ConfigurationProperty property = source.getConfigurationProperty(root);
        if (property != null) {
            getContext().setConfigurationProperty(property);
            bindValue(target, collection.get(), aggregateType, elementType, property.getValue());
        } else {
            bindIndexed(source, root, elementBinder, collection, elementType);
        }
    }

    private void bindValue(Bindable<?> target, Collection<Object> collection, ResolvableType aggregateType, ResolvableType elementType, Object value) {
        if (value != null) {
            if ((value instanceof CharSequence) && ((CharSequence) value).length() == 0) {
                return;
            }
            Object aggregate = convert(value, aggregateType, target.getAnnotations());
            ResolvableType collectionType = ResolvableType.forClassWithGenerics(collection.getClass(), elementType);
            Collection<Object> elements = (Collection) convert(aggregate, collectionType, new Annotation[0]);
            collection.addAll(elements);
        }
    }

    private void bindIndexed(ConfigurationPropertySource source, ConfigurationPropertyName root, AggregateElementBinder elementBinder, IndexedCollectionSupplier collection, ResolvableType elementType) {
        MultiValueMap<String, ConfigurationPropertyName> knownIndexedChildren = getKnownIndexedChildren(source, root);
        int i = 0;
        while (i < Integer.MAX_VALUE) {
            ConfigurationPropertyName name = root.append(i != 0 ? PropertyAccessor.PROPERTY_KEY_PREFIX + i + "]" : INDEX_ZERO);
            Object value = elementBinder.bind(name, Bindable.of(elementType), source);
            if (value == null) {
                break;
            }
            knownIndexedChildren.remove(name.getLastElement(ConfigurationPropertyName.Form.UNIFORM));
            collection.get().add(value);
            i++;
        }
        assertNoUnboundChildren(source, knownIndexedChildren);
    }

    private MultiValueMap<String, ConfigurationPropertyName> getKnownIndexedChildren(ConfigurationPropertySource source, ConfigurationPropertyName root) {
        MultiValueMap<String, ConfigurationPropertyName> children = new LinkedMultiValueMap<>();
        if (!(source instanceof IterableConfigurationPropertySource)) {
            return children;
        }
        root.getClass();
        for (ConfigurationPropertyName name : (IterableConfigurationPropertySource) source.filter(root::isAncestorOf)) {
            ConfigurationPropertyName choppedName = name.chop(root.getNumberOfElements() + 1);
            if (choppedName.isLastElementIndexed()) {
                String key = choppedName.getLastElement(ConfigurationPropertyName.Form.UNIFORM);
                children.add(key, name);
            }
        }
        return children;
    }

    private void assertNoUnboundChildren(ConfigurationPropertySource source, MultiValueMap<String, ConfigurationPropertyName> children) {
        if (!children.isEmpty()) {
            Stream<R> streamFlatMap = children.values().stream().flatMap((v0) -> {
                return v0.stream();
            });
            source.getClass();
            throw new UnboundConfigurationPropertiesException((Set) streamFlatMap.map(source::getConfigurationProperty).collect(Collectors.toCollection(TreeSet::new)));
        }
    }

    private <C> C convert(Object obj, ResolvableType resolvableType, Annotation... annotationArr) {
        return (C) getContext().getConverter().convert(getContext().getPlaceholdersResolver().resolvePlaceholders(obj), resolvableType, annotationArr);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/IndexedElementsBinder$IndexedCollectionSupplier.class */
    protected static class IndexedCollectionSupplier extends AggregateBinder.AggregateSupplier<Collection<Object>> {
        public IndexedCollectionSupplier(Supplier<Collection<Object>> supplier) {
            super(supplier);
        }
    }
}
