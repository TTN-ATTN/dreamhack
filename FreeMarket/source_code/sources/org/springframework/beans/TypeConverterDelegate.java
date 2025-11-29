package org.springframework.beans;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/TypeConverterDelegate.class */
class TypeConverterDelegate {
    private static final Log logger = LogFactory.getLog((Class<?>) TypeConverterDelegate.class);
    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    @Nullable
    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, @Nullable Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String str, @Nullable Object obj, Object obj2, @Nullable Class<T> cls) throws IllegalArgumentException {
        return (T) convertIfNecessary(str, obj, obj2, cls, TypeDescriptor.valueOf(cls));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String str, @Nullable Object obj, @Nullable Object obj2, @Nullable Class<T> cls, @Nullable TypeDescriptor typeDescriptor) throws ClassNotFoundException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        TypeDescriptor elementTypeDescriptor;
        Class<?> type;
        PropertyEditor propertyEditorFindCustomEditor = this.propertyEditorRegistry.findCustomEditor(cls, str);
        ConversionFailedException conversionFailedException = null;
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (propertyEditorFindCustomEditor == null && conversionService != null && obj2 != null && typeDescriptor != null) {
            TypeDescriptor typeDescriptorForObject = TypeDescriptor.forObject(obj2);
            if (conversionService.canConvert(typeDescriptorForObject, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(obj2, typeDescriptorForObject, typeDescriptor);
                } catch (ConversionFailedException e) {
                    conversionFailedException = e;
                }
            }
        }
        Object objDoConvertValue = obj2;
        if (propertyEditorFindCustomEditor != null || (cls != null && !ClassUtils.isAssignableValue(cls, objDoConvertValue))) {
            if (typeDescriptor != null && cls != null && Collection.class.isAssignableFrom(cls) && (objDoConvertValue instanceof String) && (elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor()) != null && (Class.class == (type = elementTypeDescriptor.getType()) || Enum.class.isAssignableFrom(type))) {
                objDoConvertValue = StringUtils.commaDelimitedListToStringArray((String) objDoConvertValue);
            }
            if (propertyEditorFindCustomEditor == null) {
                propertyEditorFindCustomEditor = findDefaultEditor(cls);
            }
            objDoConvertValue = doConvertValue(obj, objDoConvertValue, cls, propertyEditorFindCustomEditor);
        }
        boolean z = false;
        if (cls != null) {
            if (objDoConvertValue != null) {
                if (Object.class == cls) {
                    return (T) objDoConvertValue;
                }
                if (cls.isArray()) {
                    if ((objDoConvertValue instanceof String) && Enum.class.isAssignableFrom(cls.getComponentType())) {
                        objDoConvertValue = StringUtils.commaDelimitedListToStringArray((String) objDoConvertValue);
                    }
                    return (T) convertToTypedArray(objDoConvertValue, str, cls.getComponentType());
                }
                if (objDoConvertValue instanceof Collection) {
                    objDoConvertValue = convertToTypedCollection((Collection) objDoConvertValue, str, cls, typeDescriptor);
                    z = true;
                } else if (objDoConvertValue instanceof Map) {
                    objDoConvertValue = convertToTypedMap((Map) objDoConvertValue, str, cls, typeDescriptor);
                    z = true;
                }
                if (objDoConvertValue.getClass().isArray() && Array.getLength(objDoConvertValue) == 1) {
                    objDoConvertValue = Array.get(objDoConvertValue, 0);
                    z = true;
                }
                if (String.class == cls && ClassUtils.isPrimitiveOrWrapper(objDoConvertValue.getClass())) {
                    return (T) objDoConvertValue.toString();
                }
                if ((objDoConvertValue instanceof String) && !cls.isInstance(objDoConvertValue)) {
                    if (conversionFailedException == null && !cls.isInterface() && !cls.isEnum()) {
                        try {
                            return (T) BeanUtils.instantiateClass(cls.getConstructor(String.class), objDoConvertValue);
                        } catch (NoSuchMethodException e2) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No String constructor found on type [" + cls.getName() + "]", e2);
                            }
                        } catch (Exception e3) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Construction via String failed for type [" + cls.getName() + "]", e3);
                            }
                        }
                    }
                    String strTrim = ((String) objDoConvertValue).trim();
                    if (cls.isEnum() && strTrim.isEmpty()) {
                        return null;
                    }
                    objDoConvertValue = attemptToConvertStringToEnum(cls, strTrim, objDoConvertValue);
                    z = true;
                } else if ((objDoConvertValue instanceof Number) && Number.class.isAssignableFrom(cls)) {
                    objDoConvertValue = NumberUtils.convertNumberToTargetClass((Number) objDoConvertValue, cls);
                    z = true;
                }
            } else if (cls == Optional.class) {
                objDoConvertValue = Optional.empty();
            }
            if (!ClassUtils.isAssignableValue(cls, objDoConvertValue)) {
                if (conversionFailedException != null) {
                    throw conversionFailedException;
                }
                if (conversionService != null && typeDescriptor != null) {
                    TypeDescriptor typeDescriptorForObject2 = TypeDescriptor.forObject(obj2);
                    if (conversionService.canConvert(typeDescriptorForObject2, typeDescriptor)) {
                        return (T) conversionService.convert(obj2, typeDescriptorForObject2, typeDescriptor);
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(obj2));
                sb.append("' to required type '").append(ClassUtils.getQualifiedName(cls)).append('\'');
                if (str != null) {
                    sb.append(" for property '").append(str).append('\'');
                }
                if (propertyEditorFindCustomEditor != null) {
                    sb.append(": PropertyEditor [").append(propertyEditorFindCustomEditor.getClass().getName()).append("] returned inappropriate value of type '").append(ClassUtils.getDescriptiveType(objDoConvertValue)).append('\'');
                    throw new IllegalArgumentException(sb.toString());
                }
                sb.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(sb.toString());
            }
        }
        if (conversionFailedException != null) {
            if (propertyEditorFindCustomEditor == null && !z && cls != null && Object.class != cls) {
                throw conversionFailedException;
            }
            logger.debug("Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", conversionFailedException);
        }
        return (T) objDoConvertValue;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        int index;
        Object convertedValue = currentConvertedValue;
        if (Enum.class == requiredType && this.targetObject != null && (index = trimmedValue.lastIndexOf(46)) > -1) {
            String enumType = trimmedValue.substring(0, index);
            String fieldName = trimmedValue.substring(index + 1);
            ClassLoader cl = this.targetObject.getClass().getClassLoader();
            try {
                Class<?> enumValueType = ClassUtils.forName(enumType, cl);
                convertedValue = enumValueType.getField(fieldName).get(null);
            } catch (ClassNotFoundException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Enum class [" + enumType + "] cannot be loaded", ex);
                }
            } catch (Throwable ex2) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex2);
                }
            }
        }
        if (convertedValue == currentConvertedValue) {
            try {
                Field enumField = requiredType.getField(trimmedValue);
                ReflectionUtils.makeAccessible(enumField);
                convertedValue = enumField.get(null);
            } catch (Throwable ex3) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + convertedValue + "] isn't an enum value", ex3);
                }
            }
        }
        return convertedValue;
    }

    @Nullable
    private PropertyEditor findDefaultEditor(@Nullable Class<?> requiredType) throws ClassNotFoundException {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && String.class != requiredType) {
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }
        return editor;
    }

    @Nullable
    private Object doConvertValue(@Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable PropertyEditor editor) {
        Object convertedValue = newValue;
        if (editor != null && !(convertedValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                Object newConvertedValue = editor.getValue();
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    editor = null;
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
            }
        }
        Object returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && (convertedValue instanceof String[])) {
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString((String[]) convertedValue);
        }
        if (convertedValue instanceof String) {
            if (editor != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }
                String newTextValue = (String) convertedValue;
                return doConvertTextValue(oldValue, newTextValue, editor);
            }
            if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    private Object doConvertTextValue(@Nullable Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, @Nullable String propertyName, Class<?> componentType) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException {
        if (input instanceof Collection) {
            Collection<?> coll = (Collection) input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
                i++;
            }
            return result;
        }
        if (input.getClass().isArray()) {
            if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result2 = Array.newInstance(componentType, arrayLength);
            for (int i2 = 0; i2 < arrayLength; i2++) {
                Object value2 = convertIfNecessary(buildIndexedPropertyName(propertyName, i2), null, Array.get(input, i2), componentType);
                Array.set(result2, i2, value2);
            }
            return result2;
        }
        Object result3 = Array.newInstance(componentType, 1);
        Object value3 = convertIfNecessary(buildIndexedPropertyName(propertyName, 0), null, input, componentType);
        Array.set(result3, 0, value3);
        return result3;
    }

    private Collection<?> convertToTypedCollection(Collection<?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        Collection<Object> convertedCopy;
        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Collection type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Collection as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor elementType = typeDescriptor != null ? typeDescriptor.getElementTypeDescriptor() : null;
        if (elementType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            Iterator<?> it = original.iterator();
            try {
                if (approximable) {
                    convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
                } else {
                    convertedCopy = (Collection) ReflectionUtils.accessibleConstructor(requiredType, new Class[0]).newInstance(new Object[0]);
                }
                int i = 0;
                while (it.hasNext()) {
                    Object element = it.next();
                    String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
                    Object convertedElement = convertIfNecessary(indexedPropertyName, null, element, elementType != null ? elementType.getType() : null, elementType);
                    try {
                        convertedCopy.add(convertedElement);
                        originalAllowed = originalAllowed && element == convertedElement;
                        i++;
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Collection type [" + original.getClass().getName() + "] seems to be read-only - injecting original Collection as-is: " + ex);
                        }
                        return original;
                    }
                }
                return originalAllowed ? original : convertedCopy;
            } catch (Throwable ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex2);
                }
                return original;
            }
        } catch (Throwable ex3) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Collection of type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex3);
            }
            return original;
        }
    }

    private Map<?, ?> convertToTypedMap(Map<?, ?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        Map<Object, Object> convertedCopy;
        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Map type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Map as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor keyType = typeDescriptor != null ? typeDescriptor.getMapKeyTypeDescriptor() : null;
        TypeDescriptor valueType = typeDescriptor != null ? typeDescriptor.getMapValueTypeDescriptor() : null;
        if (keyType == null && valueType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            try {
                if (approximable) {
                    convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
                } else {
                    convertedCopy = (Map) ReflectionUtils.accessibleConstructor(requiredType, new Class[0]).newInstance(new Object[0]);
                }
                for (Map.Entry<?, ?> entry : original.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
                    Object convertedKey = convertIfNecessary(keyedPropertyName, null, key, keyType != null ? keyType.getType() : null, keyType);
                    Object convertedValue = convertIfNecessary(keyedPropertyName, null, value, valueType != null ? valueType.getType() : null, valueType);
                    try {
                        convertedCopy.put(convertedKey, convertedValue);
                        originalAllowed = originalAllowed && key == convertedKey && value == convertedValue;
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Map type [" + original.getClass().getName() + "] seems to be read-only - injecting original Map as-is: " + ex);
                        }
                        return original;
                    }
                }
                return originalAllowed ? original : convertedCopy;
            } catch (Throwable ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot create copy of Map type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex2);
                }
                return original;
            }
        } catch (Throwable ex3) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Map of type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex3);
            }
            return original;
        }
    }

    @Nullable
    private String buildIndexedPropertyName(@Nullable String propertyName, int index) {
        if (propertyName != null) {
            return propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + index + "]";
        }
        return null;
    }

    @Nullable
    private String buildKeyedPropertyName(@Nullable String propertyName, Object key) {
        if (propertyName != null) {
            return propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + key + "]";
        }
        return null;
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, new Class[0]);
    }
}
