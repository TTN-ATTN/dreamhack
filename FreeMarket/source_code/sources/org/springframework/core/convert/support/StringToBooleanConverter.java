package org.springframework.core.convert.support;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/convert/support/StringToBooleanConverter.class */
final class StringToBooleanConverter implements Converter<String, Boolean> {
    private static final Set<String> trueValues = new HashSet(8);
    private static final Set<String> falseValues = new HashSet(8);

    StringToBooleanConverter() {
    }

    static {
        trueValues.add("true");
        trueValues.add(CustomBooleanEditor.VALUE_ON);
        trueValues.add(CustomBooleanEditor.VALUE_YES);
        trueValues.add(CustomBooleanEditor.VALUE_1);
        falseValues.add("false");
        falseValues.add(CustomBooleanEditor.VALUE_OFF);
        falseValues.add("no");
        falseValues.add(CustomBooleanEditor.VALUE_0);
    }

    @Override // org.springframework.core.convert.converter.Converter
    @Nullable
    public Boolean convert(String source) {
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        String value2 = value.toLowerCase();
        if (trueValues.contains(value2)) {
            return Boolean.TRUE;
        }
        if (falseValues.contains(value2)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }
}
