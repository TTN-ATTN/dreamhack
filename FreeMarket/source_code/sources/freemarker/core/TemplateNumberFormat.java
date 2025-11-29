package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateNumberFormat.class */
public abstract class TemplateNumberFormat extends TemplateValueFormat {
    public abstract String formatToPlainText(TemplateNumberModel templateNumberModel) throws TemplateValueFormatException, TemplateModelException;

    public abstract boolean isLocaleBound();

    public Object format(TemplateNumberModel numberModel) throws TemplateValueFormatException, TemplateModelException {
        return formatToPlainText(numberModel);
    }

    public final Object parse(String s) throws TemplateValueFormatException {
        throw new ParsingNotSupportedException("Number formats currently don't support parsing");
    }
}
