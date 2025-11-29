package freemarker.template;

import freemarker.core.Environment;
import java.io.IOException;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateDirectiveModel.class */
public interface TemplateDirectiveModel extends TemplateModel {
    void execute(Environment environment, Map map, TemplateModel[] templateModelArr, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException;
}
