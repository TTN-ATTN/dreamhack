package org.springframework.web.servlet.tags.form;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/form/AbstractSingleCheckedElementTag.class */
public abstract class AbstractSingleCheckedElementTag extends AbstractCheckedElementTag {

    @Nullable
    private Object value;

    @Nullable
    private Object label;

    protected abstract void writeTagDetails(TagWriter tagWriter) throws JspException;

    public void setValue(Object value) {
        this.value = value;
    }

    @Nullable
    protected Object getValue() {
        return this.value;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @Nullable
    protected Object getLabel() {
        return this.label;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag, org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException, IOException {
        tagWriter.startTag("input");
        String id = resolveId();
        writeOptionalAttribute(tagWriter, "id", id);
        writeOptionalAttribute(tagWriter, "name", getName());
        writeOptionalAttributes(tagWriter);
        writeTagDetails(tagWriter);
        tagWriter.endTag();
        Object resolvedLabel = evaluate("label", getLabel());
        if (resolvedLabel != null) {
            Assert.state(id != null, "Label id is required");
            tagWriter.startTag("label");
            tagWriter.writeAttribute("for", id);
            tagWriter.appendValue(convertToDisplayString(resolvedLabel));
            tagWriter.endTag();
            return 0;
        }
        return 0;
    }
}
