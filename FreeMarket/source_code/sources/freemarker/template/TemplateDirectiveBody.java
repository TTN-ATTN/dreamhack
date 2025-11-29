package freemarker.template;

import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateDirectiveBody.class */
public interface TemplateDirectiveBody {
    void render(Writer writer) throws TemplateException, IOException;
}
