package org.springframework.core.type.classreading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/type/classreading/AnnotationReadingVisitorUtils.class */
abstract class AnnotationReadingVisitorUtils {
    AnnotationReadingVisitorUtils() {
    }

    public static AnnotationAttributes convertClassValues(Object annotatedElement, @Nullable ClassLoader classLoader, AnnotationAttributes original, boolean classValuesAsString) {
        AnnotationAttributes result = new AnnotationAttributes(original);
        AnnotationUtils.postProcessAnnotationAttributes(annotatedElement, result, classValuesAsString);
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            try {
                Object value = entry.getValue();
                if (value instanceof AnnotationAttributes) {
                    value = convertClassValues(annotatedElement, classLoader, (AnnotationAttributes) value, classValuesAsString);
                } else if (value instanceof AnnotationAttributes[]) {
                    AnnotationAttributes[] values = (AnnotationAttributes[]) value;
                    for (int i = 0; i < values.length; i++) {
                        values[i] = convertClassValues(annotatedElement, classLoader, values[i], classValuesAsString);
                    }
                    value = values;
                } else if (value instanceof Type) {
                    value = classValuesAsString ? ((Type) value).getClassName() : ClassUtils.forName(((Type) value).getClassName(), classLoader);
                } else if (value instanceof Type[]) {
                    Type[] array = (Type[]) value;
                    Object[] convArray = classValuesAsString ? new String[array.length] : new Class[array.length];
                    for (int i2 = 0; i2 < array.length; i2++) {
                        convArray[i2] = classValuesAsString ? array[i2].getClassName() : ClassUtils.forName(array[i2].getClassName(), classLoader);
                    }
                    value = convArray;
                } else if (classValuesAsString) {
                    if (value instanceof Class) {
                        value = ((Class) value).getName();
                    } else if (value instanceof Class[]) {
                        Class<?>[] clazzArray = (Class[]) value;
                        String[] newValue = new String[clazzArray.length];
                        for (int i3 = 0; i3 < clazzArray.length; i3++) {
                            newValue[i3] = clazzArray[i3].getName();
                        }
                        value = newValue;
                    }
                }
                entry.setValue(value);
            } catch (Throwable ex) {
                result.put(entry.getKey(), ex);
            }
        }
        return result;
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(LinkedMultiValueMap<String, AnnotationAttributes> attributesMap, Map<String, Set<String>> metaAnnotationMap, String annotationName) {
        Set<String> metaAnns;
        List<AnnotationAttributes> attributesList = attributesMap.get((Object) annotationName);
        if (CollectionUtils.isEmpty(attributesList)) {
            return null;
        }
        AnnotationAttributes result = new AnnotationAttributes(attributesList.get(0));
        Set<String> overridableAttributeNames = new HashSet<>(result.keySet());
        overridableAttributeNames.remove("value");
        List<String> annotationTypes = new ArrayList<>(attributesMap.keySet());
        Collections.reverse(annotationTypes);
        annotationTypes.remove(annotationName);
        for (String currentAnnotationType : annotationTypes) {
            List<AnnotationAttributes> currentAttributesList = attributesMap.get((Object) currentAnnotationType);
            if (!ObjectUtils.isEmpty(currentAttributesList) && (metaAnns = metaAnnotationMap.get(currentAnnotationType)) != null && metaAnns.contains(annotationName)) {
                AnnotationAttributes currentAttributes = currentAttributesList.get(0);
                for (String overridableAttributeName : overridableAttributeNames) {
                    Object value = currentAttributes.get(overridableAttributeName);
                    if (value != null) {
                        result.put(overridableAttributeName, value);
                    }
                }
            }
        }
        return result;
    }
}
