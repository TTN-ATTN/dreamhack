package org.springframework.expression.spel.ast;

import java.util.List;
import java.util.StringJoiner;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/spel/ast/FormatHelper.class */
abstract class FormatHelper {
    FormatHelper() {
    }

    public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (TypeDescriptor typeDescriptor : argumentTypes) {
            if (typeDescriptor != null) {
                sj.add(formatClassNameForMessage(typeDescriptor.getType()));
            } else {
                sj.add(formatClassNameForMessage(null));
            }
        }
        return name + sj.toString();
    }

    public static String formatClassNameForMessage(@Nullable Class<?> clazz) {
        return clazz != null ? ClassUtils.getQualifiedName(clazz) : BeanDefinitionParserDelegate.NULL_ELEMENT;
    }
}
