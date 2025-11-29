package org.springframework.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/AbstractPropertyAccessor.class */
public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor {
    private boolean extractOldValueForEditor = false;
    private boolean autoGrowNestedPaths = false;
    boolean suppressNotWritablePropertyException = false;

    @Nullable
    public abstract Object getPropertyValue(String str) throws BeansException;

    public abstract void setPropertyValue(String str, @Nullable Object obj) throws BeansException;

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void setPropertyValue(PropertyValue pv) throws BeansException {
        setPropertyValue(pv.getName(), pv.getValue());
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(Map<?, ?> map) throws BeansException {
        setPropertyValues(new MutablePropertyValues(map));
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        setPropertyValues(pvs, false, false);
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
        setPropertyValues(pvs, ignoreUnknown, false);
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
        List<PropertyAccessException> propertyAccessExceptions = null;
        List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues) pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        if (ignoreUnknown) {
            this.suppressNotWritablePropertyException = true;
        }
        try {
            for (PropertyValue pv : propertyValues) {
                try {
                    try {
                        try {
                            setPropertyValue(pv);
                        } catch (NullValueInNestedPathException ex) {
                            if (!ignoreInvalid) {
                                throw ex;
                            }
                        }
                    } catch (NotWritablePropertyException ex2) {
                        if (!ignoreUnknown) {
                            throw ex2;
                        }
                    }
                } catch (PropertyAccessException ex3) {
                    if (propertyAccessExceptions == null) {
                        propertyAccessExceptions = new ArrayList<>();
                    }
                    propertyAccessExceptions.add(ex3);
                }
            }
            if (propertyAccessExceptions != null) {
                PropertyAccessException[] paeArray = (PropertyAccessException[]) propertyAccessExceptions.toArray(new PropertyAccessException[0]);
                throw new PropertyBatchUpdateException(paeArray);
            }
        } finally {
            if (ignoreUnknown) {
                this.suppressNotWritablePropertyException = false;
            }
        }
    }

    @Override // org.springframework.beans.PropertyEditorRegistrySupport, org.springframework.beans.PropertyAccessor
    @Nullable
    public Class<?> getPropertyType(String propertyPath) {
        return null;
    }
}
