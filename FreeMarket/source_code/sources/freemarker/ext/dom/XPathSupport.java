package freemarker.ext.dom;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/XPathSupport.class */
public interface XPathSupport {
    TemplateModel executeQuery(Object obj, String str) throws TemplateModelException;
}
