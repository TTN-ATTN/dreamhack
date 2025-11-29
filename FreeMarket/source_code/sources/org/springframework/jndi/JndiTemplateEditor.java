package org.springframework.jndi;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jndi/JndiTemplateEditor.class */
public class JndiTemplateEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(@Nullable String text) throws IOException, IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
        }
        if (text.isEmpty()) {
            setValue(new JndiTemplate());
            return;
        }
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new JndiTemplate(props));
    }
}
