package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateDateFormat.class */
public abstract class TemplateDateFormat extends TemplateValueFormat {
    public abstract String formatToPlainText(TemplateDateModel templateDateModel) throws TemplateValueFormatException, TemplateModelException;

    public abstract Object parse(String str, int i) throws TemplateValueFormatException;

    public abstract boolean isLocaleBound();

    public abstract boolean isTimeZoneBound();

    public Object format(TemplateDateModel dateModel) throws TemplateValueFormatException, TemplateModelException {
        return formatToPlainText(dateModel);
    }
}
