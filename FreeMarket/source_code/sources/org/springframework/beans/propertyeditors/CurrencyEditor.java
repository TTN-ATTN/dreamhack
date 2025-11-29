package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Currency;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/propertyeditors/CurrencyEditor.class */
public class CurrencyEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            text = text.trim();
        }
        setValue(Currency.getInstance(text));
    }

    public String getAsText() {
        Currency value = (Currency) getValue();
        return value != null ? value.getCurrencyCode() : "";
    }
}
