package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ThreadInterruptionSupportTemplatePostProcessor.class */
class ThreadInterruptionSupportTemplatePostProcessor extends TemplatePostProcessor {
    ThreadInterruptionSupportTemplatePostProcessor() {
    }

    @Override // freemarker.core.TemplatePostProcessor
    public void postProcess(Template t) throws TemplatePostProcessorException {
        TemplateElement te = t.getRootTreeNode();
        addInterruptionChecks(te);
    }

    private void addInterruptionChecks(TemplateElement te) throws TemplatePostProcessorException {
        if (te == null) {
            return;
        }
        int childCount = te.getChildCount();
        for (int i = 0; i < childCount; i++) {
            addInterruptionChecks(te.getChild(i));
        }
        if (te.isNestedBlockRepeater()) {
            try {
                te.addChild(0, new ThreadInterruptionCheck(te));
            } catch (ParseException e) {
                throw new TemplatePostProcessorException("Unexpected error; see cause", e);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ThreadInterruptionSupportTemplatePostProcessor$ThreadInterruptionCheck.class */
    static class ThreadInterruptionCheck extends TemplateElement {
        private ThreadInterruptionCheck(TemplateElement te) throws ParseException {
            setLocation(te.getTemplate(), te.beginColumn, te.beginLine, te.beginColumn, te.beginLine);
        }

        @Override // freemarker.core.TemplateElement
        TemplateElement[] accept(Environment env) throws TemplateException, IOException {
            if (Thread.currentThread().isInterrupted()) {
                throw new TemplateProcessingThreadInterruptedException();
            }
            return null;
        }

        @Override // freemarker.core.TemplateElement
        protected String dump(boolean canonical) {
            return canonical ? "" : "<#--" + getNodeTypeSymbol() + "--#>";
        }

        @Override // freemarker.core.TemplateObject
        String getNodeTypeSymbol() {
            return "##threadInterruptionCheck";
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

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ThreadInterruptionSupportTemplatePostProcessor$TemplateProcessingThreadInterruptedException.class */
    static class TemplateProcessingThreadInterruptedException extends FlowControlException {
        TemplateProcessingThreadInterruptedException() {
            super("Template processing thread \"interrupted\" flag was set.");
        }
    }
}
