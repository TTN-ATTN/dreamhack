package freemarker.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateTransformModel.class */
public interface TemplateTransformModel extends TemplateModel {
    Writer getWriter(Writer writer, Map map) throws TemplateModelException, IOException;
}
