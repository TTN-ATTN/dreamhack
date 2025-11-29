package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/form/AbstractFormTag.class */
public abstract class AbstractFormTag extends HtmlEscapingAwareTag {
    protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

    @Nullable
    protected Object evaluate(String attributeName, @Nullable Object value) throws JspException {
        return value;
    }

    protected final void writeOptionalAttribute(TagWriter tagWriter, String attributeName, @Nullable String value) throws JspException, IOException {
        if (value != null) {
            tagWriter.writeOptionalAttributeValue(attributeName, getDisplayString(evaluate(attributeName, value)));
        }
    }

    protected TagWriter createTagWriter() {
        return new TagWriter(this.pageContext);
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws Exception {
        return writeTagContent(createTagWriter());
    }

    protected String getDisplayString(@Nullable Object value) {
        return ValueFormatter.getDisplayString(value, isHtmlEscape());
    }

    protected String getDisplayString(@Nullable Object value, @Nullable PropertyEditor propertyEditor) {
        return ValueFormatter.getDisplayString(value, propertyEditor, isHtmlEscape());
    }

    @Override // org.springframework.web.servlet.tags.HtmlEscapingAwareTag
    protected boolean isDefaultHtmlEscape() {
        Boolean defaultHtmlEscape = getRequestContext().getDefaultHtmlEscape();
        return defaultHtmlEscape == null || defaultHtmlEscape.booleanValue();
    }
}
