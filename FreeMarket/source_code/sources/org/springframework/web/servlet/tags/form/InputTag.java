package org.springframework.web.servlet.tags.form;

import java.io.IOException;
import java.util.Map;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/form/InputTag.class */
public class InputTag extends AbstractHtmlInputElementTag {
    public static final String SIZE_ATTRIBUTE = "size";
    public static final String MAXLENGTH_ATTRIBUTE = "maxlength";
    public static final String ALT_ATTRIBUTE = "alt";
    public static final String ONSELECT_ATTRIBUTE = "onselect";
    public static final String AUTOCOMPLETE_ATTRIBUTE = "autocomplete";

    @Nullable
    private String size;

    @Nullable
    private String maxlength;

    @Nullable
    private String alt;

    @Nullable
    private String onselect;

    @Nullable
    private String autocomplete;

    public void setSize(String size) {
        this.size = size;
    }

    @Nullable
    protected String getSize() {
        return this.size;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    @Nullable
    protected String getMaxlength() {
        return this.maxlength;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Nullable
    protected String getAlt() {
        return this.alt;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @Nullable
    protected String getOnselect() {
        return this.onselect;
    }

    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    @Nullable
    protected String getAutocomplete() {
        return this.autocomplete;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException, IOException {
        tagWriter.startTag("input");
        writeDefaultAttributes(tagWriter);
        Map<String, Object> attributes = getDynamicAttributes();
        if (attributes == null || !attributes.containsKey("type")) {
            tagWriter.writeAttribute("type", getType());
        }
        writeValue(tagWriter);
        writeOptionalAttribute(tagWriter, SIZE_ATTRIBUTE, getSize());
        writeOptionalAttribute(tagWriter, MAXLENGTH_ATTRIBUTE, getMaxlength());
        writeOptionalAttribute(tagWriter, ALT_ATTRIBUTE, getAlt());
        writeOptionalAttribute(tagWriter, "onselect", getOnselect());
        writeOptionalAttribute(tagWriter, AUTOCOMPLETE_ATTRIBUTE, getAutocomplete());
        tagWriter.endTag();
        return 0;
    }

    protected void writeValue(TagWriter tagWriter) throws JspException, IOException {
        String value = getDisplayString(getBoundValue(), getPropertyEditor());
        String type = null;
        Map<String, Object> attributes = getDynamicAttributes();
        if (attributes != null) {
            type = (String) attributes.get("type");
        }
        if (type == null) {
            type = getType();
        }
        tagWriter.writeAttribute("value", processFieldValue(getName(), value, type));
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementTag
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return ("type".equals(localName) && ("checkbox".equals(value) || "radio".equals(value))) ? false : true;
    }

    protected String getType() {
        return "text";
    }
}
