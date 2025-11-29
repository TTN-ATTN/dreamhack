package freemarker.core;

import freemarker.template.Template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateProcessingTracer.class */
public interface TemplateProcessingTracer {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateProcessingTracer$TracedElement.class */
    public interface TracedElement {
        Template getTemplate();

        int getBeginLine();

        int getBeginColumn();

        int getEndColumn();

        int getEndLine();

        boolean isLeaf();

        String getDescription();
    }

    void enterElement(Environment environment, TracedElement tracedElement);

    void exitElement(Environment environment, TracedElement tracedElement);
}
