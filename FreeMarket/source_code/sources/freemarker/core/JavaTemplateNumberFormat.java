package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.text.NumberFormat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaTemplateNumberFormat.class */
class JavaTemplateNumberFormat extends BackwardCompatibleTemplateNumberFormat {
    private final String formatString;
    private final NumberFormat javaNumberFormat;

    public JavaTemplateNumberFormat(NumberFormat javaNumberFormat, String formatString) {
        this.formatString = formatString;
        this.javaNumberFormat = javaNumberFormat;
    }

    @Override // freemarker.core.TemplateNumberFormat
    public String formatToPlainText(TemplateNumberModel numberModel) throws TemplateModelException, UnformattableValueException {
        Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
        return format(number);
    }

    @Override // freemarker.core.TemplateNumberFormat
    public boolean isLocaleBound() {
        return true;
    }

    @Override // freemarker.core.BackwardCompatibleTemplateNumberFormat
    String format(Number number) throws UnformattableValueException {
        try {
            return this.javaNumberFormat.format(number);
        } catch (ArithmeticException e) {
            throw new UnformattableValueException("This format can't format the " + number + " number. Reason: " + e.getMessage(), e);
        }
    }

    public NumberFormat getJavaNumberFormat() {
        return this.javaNumberFormat;
    }

    @Override // freemarker.core.TemplateValueFormat
    public String getDescription() {
        return this.formatString;
    }
}
