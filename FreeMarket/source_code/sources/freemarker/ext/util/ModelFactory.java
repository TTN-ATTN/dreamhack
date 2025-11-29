package freemarker.ext.util;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/util/ModelFactory.class */
public interface ModelFactory {
    TemplateModel create(Object obj, ObjectWrapper objectWrapper);
}
