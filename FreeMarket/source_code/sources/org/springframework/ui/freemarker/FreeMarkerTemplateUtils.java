package org.springframework.ui.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/ui/freemarker/FreeMarkerTemplateUtils.class */
public abstract class FreeMarkerTemplateUtils {
    public static String processTemplateIntoString(Template template, Object model) throws TemplateException, IOException {
        StringWriter result = new StringWriter(1024);
        template.process(model, result);
        return result.toString();
    }
}
