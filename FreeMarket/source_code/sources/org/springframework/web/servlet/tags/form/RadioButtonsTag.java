package org.springframework.web.servlet.tags.form;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/form/RadioButtonsTag.class */
public class RadioButtonsTag extends AbstractMultiCheckedElementTag {
    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag
    protected String getInputType() {
        return "radio";
    }
}
