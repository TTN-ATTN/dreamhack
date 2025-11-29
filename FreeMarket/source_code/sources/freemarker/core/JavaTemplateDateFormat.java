package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaTemplateDateFormat.class */
class JavaTemplateDateFormat extends TemplateDateFormat {
    private final DateFormat javaDateFormat;

    public JavaTemplateDateFormat(DateFormat javaDateFormat) {
        this.javaDateFormat = javaDateFormat;
    }

    @Override // freemarker.core.TemplateDateFormat
    public String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
        return this.javaDateFormat.format(TemplateFormatUtil.getNonNullDate(dateModel));
    }

    @Override // freemarker.core.TemplateDateFormat
    public Date parse(String s, int dateType) throws UnparsableValueException {
        try {
            return this.javaDateFormat.parse(s);
        } catch (java.text.ParseException e) {
            throw new UnparsableValueException(e.getMessage(), e);
        }
    }

    @Override // freemarker.core.TemplateValueFormat
    public String getDescription() {
        if (this.javaDateFormat instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) this.javaDateFormat).toPattern();
        }
        return this.javaDateFormat.toString();
    }

    @Override // freemarker.core.TemplateDateFormat
    public boolean isLocaleBound() {
        return true;
    }

    @Override // freemarker.core.TemplateDateFormat
    public boolean isTimeZoneBound() {
        return true;
    }
}
