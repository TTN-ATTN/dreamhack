package freemarker.core;

import freemarker.template.utility.StringUtil;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Comment.class */
public final class Comment extends TemplateElement {
    private final String text;

    Comment(String text) {
        this.text = text;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) {
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<#--" + this.text + "-->";
        }
        return "comment " + StringUtil.jQuote(this.text.trim());
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#--...--";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.text;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    public String getText() {
        return this.text;
    }

    @Override // freemarker.core.TemplateElement
    boolean isOutputCacheable() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
