package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaCFormat.class */
public final class JavaCFormat extends CFormat {
    public static final String NAME = "Java";
    public static final JavaCFormat INSTANCE = new JavaCFormat();
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("Double.POSITIVE_INFINITY", "Double.NEGATIVE_INFINITY", "Double.NaN", "Float.POSITIVE_INFINITY", "Float.NEGATIVE_INFINITY", "Float.NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat) LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("Double.POSITIVE_INFINITY");
        symbols.setNaN("Double.NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }

    private JavaCFormat() {
    }

    @Override // freemarker.core.CFormat
    TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override // freemarker.core.CFormat
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.javaStringEnc(s, true);
    }

    @Override // freemarker.core.CFormat
    String getTrueString() {
        return "true";
    }

    @Override // freemarker.core.CFormat
    String getFalseString() {
        return "false";
    }

    @Override // freemarker.core.CFormat
    String getNullString() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }

    @Override // freemarker.core.CFormat
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat) LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }

    @Override // freemarker.core.CFormat
    public String getName() {
        return NAME;
    }
}
