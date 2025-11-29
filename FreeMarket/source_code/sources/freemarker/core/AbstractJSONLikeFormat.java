package freemarker.core;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AbstractJSONLikeFormat.class */
public abstract class AbstractJSONLikeFormat extends CFormat {
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("Infinity", "-Infinity", "NaN", "Infinity", "-Infinity", "NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat) LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("Infinity");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }

    AbstractJSONLikeFormat() {
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
    final String getNullString() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }

    @Override // freemarker.core.CFormat
    final TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override // freemarker.core.CFormat
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat) LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }
}
