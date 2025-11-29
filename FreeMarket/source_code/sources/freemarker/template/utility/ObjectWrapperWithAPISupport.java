package freemarker.template.utility;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/ObjectWrapperWithAPISupport.class */
public interface ObjectWrapperWithAPISupport extends ObjectWrapper {
    TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException;
}
