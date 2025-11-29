package freemarker.ext.jsp;

import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import java.beans.IntrospectionException;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/TagTransformModel.class */
class TagTransformModel extends JspTagModelBase implements TemplateTransformModel {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private final boolean isBodyTag;
    private final boolean isIterationTag;
    private final boolean isTryCatchFinally;

    public TagTransformModel(String tagName, Class tagClass) throws IntrospectionException {
        super(tagName, tagClass);
        this.isIterationTag = IterationTag.class.isAssignableFrom(tagClass);
        this.isBodyTag = this.isIterationTag && BodyTag.class.isAssignableFrom(tagClass);
        this.isTryCatchFinally = TryCatchFinally.class.isAssignableFrom(tagClass);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14, types: [freemarker.ext.jsp.JspWriterAdapter] */
    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(Writer out, Map args) throws TemplateModelException {
        boolean usesAdapter;
        try {
            Tag tag = (Tag) getTagInstance();
            FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
            Tag parentTag = (Tag) pageContext.peekTopTag(Tag.class);
            tag.setParent(parentTag);
            tag.setPageContext(pageContext);
            setupTag(tag, args, pageContext.getObjectWrapper());
            if (out instanceof JspWriter) {
                if (out != pageContext.getOut()) {
                    throw new TemplateModelException("out != pageContext.getOut(). Out is " + out + " pageContext.getOut() is " + pageContext.getOut());
                }
                usesAdapter = false;
            } else {
                out = new JspWriterAdapter(out);
                pageContext.pushWriter((JspWriter) out);
                usesAdapter = true;
            }
            JspWriter w = new TagWriter(out, tag, pageContext, usesAdapter);
            pageContext.pushTopTag(tag);
            pageContext.pushWriter(w);
            return w;
        } catch (Exception e) {
            throw toTemplateModelExceptionOrRethrow(e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/TagTransformModel$BodyContentImpl.class */
    static class BodyContentImpl extends BodyContent {
        private CharArrayWriter buf;

        BodyContentImpl(JspWriter out, boolean buffer) {
            super(out);
            if (buffer) {
                initBuffer();
            }
        }

        void initBuffer() {
            this.buf = new CharArrayWriter();
        }

        public void flush() throws IOException {
            if (this.buf == null) {
                getEnclosingWriter().flush();
            }
        }

        public void clear() throws IOException {
            if (this.buf != null) {
                this.buf = new CharArrayWriter();
                return;
            }
            throw new IOException("Can't clear");
        }

        public void clearBuffer() throws IOException {
            if (this.buf != null) {
                this.buf = new CharArrayWriter();
                return;
            }
            throw new IOException("Can't clear");
        }

        public int getRemaining() {
            return Integer.MAX_VALUE;
        }

        public void newLine() throws IOException {
            write(JspWriterAdapter.NEWLINE);
        }

        public void close() throws IOException {
        }

        public void print(boolean arg0) throws IOException {
            write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        }

        public void print(char arg0) throws IOException {
            write(arg0);
        }

        public void print(char[] arg0) throws IOException {
            write(arg0);
        }

        public void print(double arg0) throws IOException {
            write(Double.toString(arg0));
        }

        public void print(float arg0) throws IOException {
            write(Float.toString(arg0));
        }

        public void print(int arg0) throws IOException {
            write(Integer.toString(arg0));
        }

        public void print(long arg0) throws IOException {
            write(Long.toString(arg0));
        }

        public void print(Object arg0) throws IOException {
            write(arg0 == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : arg0.toString());
        }

        public void print(String arg0) throws IOException {
            write(arg0);
        }

        public void println() throws IOException {
            newLine();
        }

        public void println(boolean arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(char arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(char[] arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(double arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(float arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(int arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(long arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(Object arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void println(String arg0) throws IOException {
            print(arg0);
            newLine();
        }

        public void write(int c) throws IOException {
            if (this.buf != null) {
                this.buf.write(c);
            } else {
                getEnclosingWriter().write(c);
            }
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            if (this.buf != null) {
                this.buf.write(cbuf, off, len);
            } else {
                getEnclosingWriter().write(cbuf, off, len);
            }
        }

        public String getString() {
            return this.buf.toString();
        }

        public Reader getReader() {
            return new CharArrayReader(this.buf.toCharArray());
        }

        public void writeOut(Writer out) throws IOException {
            this.buf.writeTo(out);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/TagTransformModel$TagWriter.class */
    class TagWriter extends BodyContentImpl implements TransformControl {
        private final Tag tag;
        private final FreeMarkerPageContext pageContext;
        private boolean needPop;
        private final boolean needDoublePop;
        private boolean closed;

        TagWriter(Writer out, Tag tag, FreeMarkerPageContext pageContext, boolean needDoublePop) {
            super((JspWriter) out, false);
            this.needPop = true;
            this.closed = false;
            this.needDoublePop = needDoublePop;
            this.tag = tag;
            this.pageContext = pageContext;
        }

        public String toString() {
            return "TagWriter for " + this.tag.getClass().getName() + " wrapping a " + getEnclosingWriter().toString();
        }

        Tag getTag() {
            return this.tag;
        }

        FreeMarkerPageContext getPageContext() {
            return this.pageContext;
        }

        @Override // freemarker.template.TransformControl
        public int onStart() throws TemplateModelException, JspException {
            try {
                int dst = this.tag.doStartTag();
                switch (dst) {
                    case 0:
                    case 6:
                        endEvaluation();
                        return 0;
                    case 1:
                        return 1;
                    case 2:
                        if (TagTransformModel.this.isBodyTag) {
                            initBuffer();
                            BodyTag btag = this.tag;
                            btag.setBodyContent(this);
                            btag.doInitBody();
                            return 1;
                        }
                        throw new TemplateModelException("Can't buffer body since " + this.tag.getClass().getName() + " does not implement BodyTag.");
                    case 3:
                    case 4:
                    case 5:
                    default:
                        throw new RuntimeException("Illegal return value " + dst + " from " + this.tag.getClass().getName() + ".doStartTag()");
                }
            } catch (Exception e) {
                throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
            }
        }

        @Override // freemarker.template.TransformControl
        public int afterBody() throws TemplateModelException, JspException {
            try {
                if (TagTransformModel.this.isIterationTag) {
                    int dab = this.tag.doAfterBody();
                    switch (dab) {
                        case 0:
                            endEvaluation();
                            return 1;
                        case 2:
                            return 0;
                        default:
                            throw new TemplateModelException("Unexpected return value " + dab + "from " + this.tag.getClass().getName() + ".doAfterBody()");
                    }
                }
                endEvaluation();
                return 1;
            } catch (Exception e) {
                throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
            }
        }

        private void endEvaluation() throws JspException {
            if (this.needPop) {
                this.pageContext.popWriter();
                this.needPop = false;
            }
            if (this.tag.doEndTag() == 5) {
                TagTransformModel.LOG.warn("Tag.SKIP_PAGE was ignored from a " + this.tag.getClass().getName() + " tag.");
            }
        }

        @Override // freemarker.template.TransformControl
        public void onError(Throwable t) throws Throwable {
            if (TagTransformModel.this.isTryCatchFinally) {
                this.tag.doCatch(t);
                return;
            }
            throw t;
        }

        @Override // freemarker.ext.jsp.TagTransformModel.BodyContentImpl
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.needPop) {
                this.pageContext.popWriter();
            }
            this.pageContext.popTopTag();
            try {
                if (TagTransformModel.this.isTryCatchFinally) {
                    this.tag.doFinally();
                }
                this.tag.release();
            } finally {
                if (this.needDoublePop) {
                    this.pageContext.popWriter();
                }
            }
        }
    }
}
