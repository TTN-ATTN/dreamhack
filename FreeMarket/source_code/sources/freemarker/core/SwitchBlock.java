package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/SwitchBlock.class */
final class SwitchBlock extends TemplateElement {
    private Case defaultCase;
    private final Expression searched;
    private int firstCaseIndex;

    SwitchBlock(Expression searched, MixedContent ignoredSectionBeforeFirstCase) {
        this.searched = searched;
        int ignoredCnt = ignoredSectionBeforeFirstCase != null ? ignoredSectionBeforeFirstCase.getChildCount() : 0;
        setChildBufferCapacity(ignoredCnt + 4);
        for (int i = 0; i < ignoredCnt; i++) {
            addChild(ignoredSectionBeforeFirstCase.getChild(i));
        }
        this.firstCaseIndex = ignoredCnt;
    }

    void addCase(Case cas) {
        if (cas.condition == null) {
            this.defaultCase = cas;
        }
        addChild(cas);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        boolean processedCase = false;
        int ln = getChildCount();
        try {
            for (int i = this.firstCaseIndex; i < ln; i++) {
                Case cas = (Case) getChild(i);
                boolean processCase = false;
                if (processedCase) {
                    processCase = true;
                } else if (cas.condition != null) {
                    processCase = EvalUtil.compare(this.searched, 1, "case==", cas.condition, cas.condition, env);
                }
                if (processCase) {
                    env.visit(cas);
                    processedCase = true;
                }
            }
            if (!processedCase && this.defaultCase != null) {
                env.visit(this.defaultCase);
            }
            return null;
        } catch (BreakOrContinueException e) {
            return null;
        }
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.searched.getCanonicalForm());
        if (canonical) {
            buf.append('>');
            int ln = getChildCount();
            for (int i = 0; i < ln; i++) {
                buf.append(getChild(i).getCanonicalForm());
            }
            buf.append("</").append(getNodeTypeSymbol()).append('>');
        }
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#switch";
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
        return this.searched;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.VALUE;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        TemplateElement result = super.postParseCleanup(stripWhitespace);
        int ln = getChildCount();
        int i = 0;
        while (i < ln && !(getChild(i) instanceof Case)) {
            i++;
        }
        this.firstCaseIndex = i;
        return result;
    }
}
