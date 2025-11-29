package freemarker.core;

import freemarker.template.TemplateException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/XSCFormat.class */
public final class XSCFormat extends CFormat {
    public static final String NAME = "XS";
    public static final XSCFormat INSTANCE = new XSCFormat();
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("INF", "-INF", "NaN", "INF", "-INF", "NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat) LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("INF");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }

    @Override // freemarker.core.CFormat
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat) LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }

    private XSCFormat() {
    }

    @Override // freemarker.core.CFormat
    TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override // freemarker.core.CFormat
    String formatString(String s, Environment env) throws TemplateException {
        return s;
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
        return "";
    }

    @Override // freemarker.core.CFormat
    public String getName() {
        return NAME;
    }
}
