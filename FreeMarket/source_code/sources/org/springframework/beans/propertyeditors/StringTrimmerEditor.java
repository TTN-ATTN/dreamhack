package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/propertyeditors/StringTrimmerEditor.class */
public class StringTrimmerEditor extends PropertyEditorSupport {

    @Nullable
    private final String charsToDelete;
    private final boolean emptyAsNull;

    public StringTrimmerEditor(boolean emptyAsNull) {
        this.charsToDelete = null;
        this.emptyAsNull = emptyAsNull;
    }

    public StringTrimmerEditor(String charsToDelete, boolean emptyAsNull) {
        this.charsToDelete = charsToDelete;
        this.emptyAsNull = emptyAsNull;
    }

    public void setAsText(@Nullable String text) {
        if (text == null) {
            setValue(null);
            return;
        }
        String value = text.trim();
        if (this.charsToDelete != null) {
            value = StringUtils.deleteAny(value, this.charsToDelete);
        }
        if (this.emptyAsNull && value.isEmpty()) {
            setValue(null);
        } else {
            setValue(value);
        }
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }
}
