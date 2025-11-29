package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.UUID;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/propertyeditors/UUIDEditor.class */
public class UUIDEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(UUID.fromString(text.trim()));
        } else {
            setValue(null);
        }
    }

    public String getAsText() {
        UUID value = (UUID) getValue();
        return value != null ? value.toString() : "";
    }
}
