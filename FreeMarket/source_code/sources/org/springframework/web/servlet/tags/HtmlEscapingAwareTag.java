package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.util.HtmlUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/tags/HtmlEscapingAwareTag.class */
public abstract class HtmlEscapingAwareTag extends RequestContextAwareTag {

    @Nullable
    private Boolean htmlEscape;

    public void setHtmlEscape(boolean htmlEscape) throws JspException {
        this.htmlEscape = Boolean.valueOf(htmlEscape);
    }

    protected boolean isHtmlEscape() {
        if (this.htmlEscape != null) {
            return this.htmlEscape.booleanValue();
        }
        return isDefaultHtmlEscape();
    }

    protected boolean isDefaultHtmlEscape() {
        return getRequestContext().isDefaultHtmlEscape();
    }

    protected boolean isResponseEncodedHtmlEscape() {
        return getRequestContext().isResponseEncodedHtmlEscape();
    }

    protected String htmlEscape(String content) {
        String out = content;
        if (isHtmlEscape()) {
            if (isResponseEncodedHtmlEscape()) {
                out = HtmlUtils.htmlEscape(content, this.pageContext.getResponse().getCharacterEncoding());
            } else {
                out = HtmlUtils.htmlEscape(content);
            }
        }
        return out;
    }
}
