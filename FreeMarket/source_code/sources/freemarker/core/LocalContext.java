package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Collection;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LocalContext.class */
public interface LocalContext {
    TemplateModel getLocalVariable(String str) throws TemplateModelException;

    Collection getLocalVariableNames() throws TemplateModelException;
}
