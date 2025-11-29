package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.CollectionFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionException;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder.class */
class ValueObjectBinder implements DataObjectBinder {
    private final BindConstructorProvider constructorProvider;

    ValueObjectBinder(BindConstructorProvider constructorProvider) {
        this.constructorProvider = constructorProvider;
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Binder.Context context, DataObjectPropertyBinder propertyBinder) {
        ValueObject<T> valueObject = ValueObject.get(target, this.constructorProvider, context);
        if (valueObject == null) {
            return null;
        }
        context.pushConstructorBoundTypes(target.getType().resolve());
        List<ConstructorParameter> parameters = valueObject.getConstructorParameters();
        List<Object> args = new ArrayList<>(parameters.size());
        boolean bound = false;
        for (ConstructorParameter parameter : parameters) {
            Object arg = parameter.bind(propertyBinder);
            bound = bound || arg != null;
            args.add(arg != null ? arg : getDefaultValue(context, parameter));
        }
        context.clearConfigurationProperty();
        context.popConstructorBoundTypes();
        if (bound) {
            return valueObject.instantiate(args);
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T create(Bindable<T> target, Binder.Context context) {
        ValueObject<T> valueObject = ValueObject.get(target, this.constructorProvider, context);
        if (valueObject == null) {
            return null;
        }
        List<ConstructorParameter> parameters = valueObject.getConstructorParameters();
        List<Object> args = new ArrayList<>(parameters.size());
        for (ConstructorParameter parameter : parameters) {
            args.add(getDefaultValue(context, parameter));
        }
        return valueObject.instantiate(args);
    }

    private <T> T getDefaultValue(Binder.Context context, ConstructorParameter constructorParameter) {
        ResolvableType type = constructorParameter.getType();
        Annotation[] annotations = constructorParameter.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof DefaultValue) {
                String[] strArrValue = ((DefaultValue) annotation).value();
                if (strArrValue.length == 0) {
                    return (T) getNewDefaultValueInstanceIfPossible(context, type);
                }
                return (T) convertDefaultValue(context.getConverter(), strArrValue, type, annotations);
            }
        }
        return null;
    }

    private <T> T convertDefaultValue(BindConverter bindConverter, String[] strArr, ResolvableType resolvableType, Annotation[] annotationArr) {
        try {
            return (T) bindConverter.convert(strArr, resolvableType, annotationArr);
        } catch (ConversionException e) {
            if (strArr.length == 1) {
                return (T) bindConverter.convert(strArr[0], resolvableType, annotationArr);
            }
            throw e;
        }
    }

    private <T> T getNewDefaultValueInstanceIfPossible(Binder.Context context, ResolvableType resolvableType) {
        Class<?> clsResolve = resolvableType.resolve();
        Assert.state(clsResolve == null || isEmptyDefaultValueAllowed(clsResolve), (Supplier<String>) () -> {
            return "Parameter of type " + resolvableType + " must have a non-empty default value.";
        });
        if (clsResolve != null) {
            if (Optional.class == clsResolve) {
                return (T) Optional.empty();
            }
            if (Collection.class.isAssignableFrom(clsResolve)) {
                return (T) CollectionFactory.createCollection(clsResolve, 0);
            }
            if (Map.class.isAssignableFrom(clsResolve)) {
                return (T) CollectionFactory.createMap(clsResolve, 0);
            }
            if (clsResolve.isArray()) {
                return (T) Array.newInstance(clsResolve.getComponentType(), 0);
            }
        }
        T t = (T) create(Bindable.of(resolvableType), context);
        if (t != null) {
            return t;
        }
        if (clsResolve != null) {
            return (T) BeanUtils.instantiateClass(clsResolve);
        }
        return null;
    }

    private boolean isEmptyDefaultValueAllowed(Class<?> type) {
        return Optional.class == type || isAggregate(type) || !(type.isPrimitive() || type.isEnum() || type.getName().startsWith("java.lang"));
    }

    private boolean isAggregate(Class<?> type) {
        return type.isArray() || Map.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$ValueObject.class */
    private static abstract class ValueObject<T> {
        private final Constructor<T> constructor;

        abstract List<ConstructorParameter> getConstructorParameters();

        protected ValueObject(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        T instantiate(List<Object> list) {
            return (T) BeanUtils.instantiateClass(this.constructor, list.toArray());
        }

        static <T> ValueObject<T> get(Bindable<T> bindable, BindConstructorProvider constructorProvider, Binder.Context context) {
            Constructor<?> bindConstructor;
            Class<?> clsResolve = bindable.getType().resolve();
            if (clsResolve == null || clsResolve.isEnum() || Modifier.isAbstract(clsResolve.getModifiers()) || (bindConstructor = constructorProvider.getBindConstructor(bindable, context.isNestedConstructorBinding())) == null) {
                return null;
            }
            if (KotlinDetector.isKotlinType(clsResolve)) {
                return KotlinValueObject.get(bindConstructor, bindable.getType());
            }
            return DefaultValueObject.get(bindConstructor, bindable.getType());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$KotlinValueObject.class */
    private static final class KotlinValueObject<T> extends ValueObject<T> {
        private static final Annotation[] ANNOTATION_ARRAY = new Annotation[0];
        private final List<ConstructorParameter> constructorParameters;

        private KotlinValueObject(Constructor<T> primaryConstructor, KFunction<T> kotlinConstructor, ResolvableType type) {
            super(primaryConstructor);
            this.constructorParameters = parseConstructorParameters(kotlinConstructor, type);
        }

        private List<ConstructorParameter> parseConstructorParameters(KFunction<T> kotlinConstructor, ResolvableType type) {
            List<KParameter> parameters = kotlinConstructor.getParameters();
            List<ConstructorParameter> result = new ArrayList<>(parameters.size());
            for (KParameter parameter : parameters) {
                String name = getParameterName(parameter);
                ResolvableType parameterType = ResolvableType.forType(ReflectJvmMapping.getJavaType(parameter.getType()), type);
                Annotation[] annotations = (Annotation[]) parameter.getAnnotations().toArray(ANNOTATION_ARRAY);
                result.add(new ConstructorParameter(name, parameterType, annotations));
            }
            return Collections.unmodifiableList(result);
        }

        private String getParameterName(KParameter kParameter) {
            Optional<T> value = MergedAnnotations.from(kParameter, (Annotation[]) kParameter.getAnnotations().toArray(ANNOTATION_ARRAY)).get(Name.class).getValue("value", String.class);
            kParameter.getClass();
            return (String) value.orElseGet(kParameter::getName);
        }

        @Override // org.springframework.boot.context.properties.bind.ValueObjectBinder.ValueObject
        List<ConstructorParameter> getConstructorParameters() {
            return this.constructorParameters;
        }

        static <T> ValueObject<T> get(Constructor<T> bindConstructor, ResolvableType type) {
            KFunction<T> kotlinConstructor = ReflectJvmMapping.getKotlinFunction(bindConstructor);
            if (kotlinConstructor != null) {
                return new KotlinValueObject(bindConstructor, kotlinConstructor, type);
            }
            return DefaultValueObject.get(bindConstructor, type);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$DefaultValueObject.class */
    private static final class DefaultValueObject<T> extends ValueObject<T> {
        private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
        private final List<ConstructorParameter> constructorParameters;

        private DefaultValueObject(Constructor<T> constructor, ResolvableType type) {
            super(constructor);
            this.constructorParameters = parseConstructorParameters(constructor, type);
        }

        private static List<ConstructorParameter> parseConstructorParameters(Constructor<?> constructor, ResolvableType resolvableType) {
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(constructor);
            Assert.state(parameterNames != null, (Supplier<String>) () -> {
                return "Failed to extract parameter names for " + constructor;
            });
            Parameter[] parameters = constructor.getParameters();
            ArrayList arrayList = new ArrayList(parameters.length);
            for (int i = 0; i < parameters.length; i++) {
                arrayList.add(new ConstructorParameter((String) MergedAnnotations.from(parameters[i]).get(Name.class).getValue("value", String.class).orElse(parameterNames[i]), ResolvableType.forMethodParameter(new MethodParameter(constructor, i), resolvableType), parameters[i].getDeclaredAnnotations()));
            }
            return Collections.unmodifiableList(arrayList);
        }

        @Override // org.springframework.boot.context.properties.bind.ValueObjectBinder.ValueObject
        List<ConstructorParameter> getConstructorParameters() {
            return this.constructorParameters;
        }

        static <T> ValueObject<T> get(Constructor<?> bindConstructor, ResolvableType type) {
            return new DefaultValueObject(bindConstructor, type);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$ConstructorParameter.class */
    private static class ConstructorParameter {
        private final String name;
        private final ResolvableType type;
        private final Annotation[] annotations;

        ConstructorParameter(String name, ResolvableType type, Annotation[] annotations) {
            this.name = DataObjectPropertyName.toDashedForm(name);
            this.type = type;
            this.annotations = annotations;
        }

        Object bind(DataObjectPropertyBinder propertyBinder) {
            return propertyBinder.bindProperty(this.name, Bindable.of(this.type).withAnnotations(this.annotations));
        }

        Annotation[] getAnnotations() {
            return this.annotations;
        }

        ResolvableType getType() {
            return this.type;
        }
    }
}
