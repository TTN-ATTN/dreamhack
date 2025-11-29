package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/SimpleTagDirectiveModel.class */
class SimpleTagDirectiveModel extends JspTagModelBase implements TemplateDirectiveModel {
    protected SimpleTagDirectiveModel(String tagName, Class tagClass) throws IntrospectionException {
        super(tagName, tagClass);
        if (!SimpleTag.class.isAssignableFrom(tagClass)) {
            throw new IllegalArgumentException(tagClass.getName() + " does not implement either the " + Tag.class.getName() + " interface or the " + SimpleTag.class.getName() + " interface.");
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // freemarker.template.TemplateDirectiveModel
    public void execute(Environment env, Map args, TemplateModel[] outArgs, final TemplateDirectiveBody body) throws TemplateException, IOException {
        try {
            SimpleTag tag = (SimpleTag) getTagInstance();
            final FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
            pageContext.pushWriter(new JspWriterAdapter(env.getOut()));
            try {
                tag.setJspContext(pageContext);
                JspTag parentTag = (JspTag) pageContext.peekTopTag(JspTag.class);
                if (parentTag != null) {
                    tag.setParent(parentTag);
                }
                setupTag(tag, args, pageContext.getObjectWrapper());
                if (body != null) {
                    tag.setJspBody(new JspFragment() { // from class: freemarker.ext.jsp.SimpleTagDirectiveModel.1
                        public JspContext getJspContext() {
                            return pageContext;
                        }

                        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.servlet.jsp.JspException */
                        public void invoke(Writer out) throws JspException, IOException {
                            try {
                                body.render(out == null ? pageContext.getOut() : out);
                            } catch (TemplateException e) {
                                throw new TemplateExceptionWrapperJspException(e);
                            }
                        }
                    });
                    pageContext.pushTopTag(tag);
                    try {
                        tag.doTag();
                        pageContext.popTopTag();
                    } catch (Throwable th) {
                        pageContext.popTopTag();
                        throw th;
                    }
                } else {
                    tag.doTag();
                }
                pageContext.popWriter();
            } catch (Throwable th2) {
                pageContext.popWriter();
                throw th2;
            }
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e2) {
            throw toTemplateModelExceptionOrRethrow(e2);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/SimpleTagDirectiveModel$TemplateExceptionWrapperJspException.class */
    static final class TemplateExceptionWrapperJspException extends JspException {
        public TemplateExceptionWrapperJspException(TemplateException cause) {
            super("Nested content has thrown template exception", cause);
        }

        /* renamed from: getCause, reason: merged with bridge method [inline-methods] */
        public TemplateException m560getCause() {
            return (TemplateException) super.getCause();
        }
    }
}
