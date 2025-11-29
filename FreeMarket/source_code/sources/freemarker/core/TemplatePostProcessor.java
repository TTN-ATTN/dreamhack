package freemarker.core;

import freemarker.template.Template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplatePostProcessor.class */
abstract class TemplatePostProcessor {
    public abstract void postProcess(Template template) throws TemplatePostProcessorException;

    TemplatePostProcessor() {
    }
}
