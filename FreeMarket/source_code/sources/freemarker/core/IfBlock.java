package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/IfBlock.class */
final class IfBlock extends TemplateElement {
    IfBlock(ConditionalBlock block) {
        setChildBufferCapacity(1);
        addBlock(block);
    }

    void addBlock(ConditionalBlock block) {
        addChild(block);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        int ln = getChildCount();
        if (env.getTemplateProcessingTracer() == null) {
            for (int i = 0; i < ln; i++) {
                ConditionalBlock cblock = (ConditionalBlock) getChild(i);
                Expression condition = cblock.condition;
                env.replaceElementStackTop(cblock);
                if (condition == null || condition.evalToBoolean(env)) {
                    return cblock.getChildBuffer();
                }
            }
            return null;
        }
        for (int i2 = 0; i2 < ln; i2++) {
            ConditionalBlock cblock2 = (ConditionalBlock) getChild(i2);
            Expression condition2 = cblock2.condition;
            env.replaceElementStackTop(cblock2);
            if (condition2 == null || condition2.evalToBoolean(env)) {
                env.visit(cblock2);
                return null;
            }
        }
        return null;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        if (getChildCount() == 1) {
            ConditionalBlock conditionalBlock = (ConditionalBlock) getChild(0);
            conditionalBlock.setLocation(getTemplate(), conditionalBlock, this);
            return conditionalBlock.postParseCleanup(stripWhitespace);
        }
        return super.postParseCleanup(stripWhitespace);
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder buf = new StringBuilder();
            int ln = getChildCount();
            for (int i = 0; i < ln; i++) {
                ConditionalBlock cblock = (ConditionalBlock) getChild(i);
                buf.append(cblock.dump(canonical));
            }
            buf.append("</#if>");
            return buf.toString();
        }
        return getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#if-#elseif-#else-container";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
