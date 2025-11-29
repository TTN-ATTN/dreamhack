package freemarker.ext.jakarta.jsp;

import freemarker.core.Environment;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/PageContextFactory.class */
class PageContextFactory {
    PageContextFactory() {
    }

    static FreeMarkerPageContext getCurrentPageContext() throws TemplateModelException {
        Environment env = Environment.getCurrentEnvironment();
        TemplateModel pageContextModel = env.getGlobalVariable("jakarta.servlet.jsp.jspPageContext");
        if (pageContextModel instanceof FreeMarkerPageContext) {
            return (FreeMarkerPageContext) pageContextModel;
        }
        FreeMarkerPageContext pageContext = new FreeMarkerPageContext();
        env.setGlobalVariable("jakarta.servlet.jsp.jspPageContext", pageContext);
        return pageContext;
    }
}
