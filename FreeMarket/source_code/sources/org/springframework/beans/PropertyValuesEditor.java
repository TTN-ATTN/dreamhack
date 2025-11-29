package org.springframework.beans;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/PropertyValuesEditor.class */
public class PropertyValuesEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(String text) throws IOException, IllegalArgumentException {
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new MutablePropertyValues(props));
    }
}
