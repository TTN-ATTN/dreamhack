package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ListElseContainer.class */
class ListElseContainer extends TemplateElement {
    private final IteratorBlock listPart;
    private final ElseOfList elsePart;

    public ListElseContainer(IteratorBlock listPart, ElseOfList elsePart) {
        setChildBufferCapacity(2);
        addChild(listPart);
        addChild(elsePart);
        this.listPart = listPart;
        this.elsePart = elsePart;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        boolean hadItems;
        TemplateProcessingTracer templateProcessingTracer = env.getTemplateProcessingTracer();
        if (templateProcessingTracer == null) {
            hadItems = this.listPart.acceptWithResult(env);
        } else {
            templateProcessingTracer.enterElement(env, this.listPart);
            try {
                hadItems = this.listPart.acceptWithResult(env);
                templateProcessingTracer.exitElement(env, this.listPart);
            } catch (Throwable th) {
                templateProcessingTracer.exitElement(env, this.listPart);
                throw th;
            }
        }
        if (hadItems) {
            return null;
        }
        return new TemplateElement[]{this.elsePart};
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder buf = new StringBuilder();
            int ln = getChildCount();
            for (int i = 0; i < ln; i++) {
                TemplateElement element = getChild(i);
                buf.append(element.dump(canonical));
            }
            buf.append("</#list>");
            return buf.toString();
        }
        return getNodeTypeSymbol();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#list-#else-container";
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
}
