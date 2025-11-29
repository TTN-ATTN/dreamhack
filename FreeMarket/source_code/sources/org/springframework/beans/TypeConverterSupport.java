package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/TypeConverterSupport.class */
public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

    @Nullable
    TypeConverterDelegate typeConverterDelegate;

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls) throws TypeMismatchException {
        return (T) convertIfNecessary(obj, cls, TypeDescriptor.valueOf(cls));
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable MethodParameter methodParameter) throws TypeMismatchException {
        return (T) convertIfNecessary(obj, cls, methodParameter != null ? new TypeDescriptor(methodParameter) : TypeDescriptor.valueOf(cls));
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable Field field) throws TypeMismatchException {
        return (T) convertIfNecessary(obj, cls, field != null ? new TypeDescriptor(field) : TypeDescriptor.valueOf(cls));
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
        Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
        try {
            return (T) this.typeConverterDelegate.convertIfNecessary(null, null, obj, cls, typeDescriptor);
        } catch (IllegalArgumentException | ConversionException e) {
            throw new TypeMismatchException(obj, (Class<?>) cls, e);
        } catch (IllegalStateException | ConverterNotFoundException e2) {
            throw new ConversionNotSupportedException(obj, (Class<?>) cls, e2);
        }
    }
}
