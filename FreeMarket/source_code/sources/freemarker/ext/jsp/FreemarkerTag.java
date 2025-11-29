package freemarker.ext.jsp;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import org.springframework.web.util.TagUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/FreemarkerTag.class */
public class FreemarkerTag implements BodyTag {
    private Tag parent;
    private BodyContent bodyContent;
    private PageContext pageContext;
    private SimpleHash root;
    private Template template;
    private boolean caching = true;
    private String name = "";

    public boolean getCaching() {
        return this.caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public Tag getParent() {
        return this.parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    public int doStartTag() {
        return 2;
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
        this.root = null;
    }

    public void doInitBody() {
    }

    public int doAfterBody() {
        return 0;
    }

    public void release() {
        this.root = null;
        this.template = null;
        this.name = "";
    }

    public int doEndTag() throws JspException {
        try {
            if (this.bodyContent == null) {
                return 6;
            }
            try {
                if (this.template == null) {
                    this.template = new Template(this.name, this.bodyContent.getReader());
                }
                if (this.root == null) {
                    this.root = new SimpleHash();
                    this.root.put(TagUtils.SCOPE_PAGE, new JspContextModel(this.pageContext, 1));
                    this.root.put("request", new JspContextModel(this.pageContext, 2));
                    this.root.put("session", new JspContextModel(this.pageContext, 3));
                    this.root.put("application", new JspContextModel(this.pageContext, 4));
                    this.root.put("any", new JspContextModel(this.pageContext, -1));
                }
                this.template.process(this.root, this.pageContext.getOut());
                if (!this.caching) {
                    this.template = null;
                    return 6;
                }
                return 6;
            } catch (Exception e) {
                try {
                    this.pageContext.handlePageException(e);
                    if (!this.caching) {
                        this.template = null;
                        return 6;
                    }
                    return 6;
                } catch (IOException | ServletException e2) {
                    throw new JspException(e2.getMessage());
                }
            }
        } catch (Throwable th) {
            if (!this.caching) {
                this.template = null;
            }
            throw th;
        }
    }
}
