package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LegacyCFormat.class */
public final class LegacyCFormat extends CFormat {
    public static final String NAME = "legacy";
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21;
    public static final LegacyCFormat INSTANCE = new LegacyCFormat();
    static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0 = new DecimalFormat("0.################", new DecimalFormatSymbols(Locale.US));

    static {
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.setGroupingUsed(false);
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.setDecimalSeparatorAlwaysShown(false);
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21 = (DecimalFormat) LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21.getDecimalFormatSymbols();
        symbols.setInfinity("INF");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21.setDecimalFormatSymbols(symbols);
    }

    private LegacyCFormat() {
    }

    @Override // freemarker.core.CFormat
    final String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JAVA_SCRIPT_OR_JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override // freemarker.core.CFormat
    final TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return getTemplateNumberFormat(env.getConfiguration().getIncompatibleImprovements().intValue());
    }

    TemplateNumberFormat getTemplateNumberFormat(int iciVersion) {
        return new LegacyCTemplateNumberFormat(getLegacyNumberFormat(iciVersion));
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
    NumberFormat getLegacyNumberFormat(Environment env) {
        return getLegacyNumberFormat(env.getConfiguration().getIncompatibleImprovements().intValue());
    }

    NumberFormat getLegacyNumberFormat(int iciVersion) {
        NumberFormat numberFormatPrototype;
        if (iciVersion < _VersionInts.V_2_3_21) {
            numberFormatPrototype = LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0;
        } else {
            numberFormatPrototype = LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21;
        }
        return (NumberFormat) numberFormatPrototype.clone();
    }

    @Override // freemarker.core.CFormat
    public String getName() {
        return "legacy";
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LegacyCFormat$LegacyCTemplateNumberFormat.class */
    static final class LegacyCTemplateNumberFormat extends JavaTemplateNumberFormat {
        public LegacyCTemplateNumberFormat(NumberFormat numberFormat) {
            super(numberFormat, "computer");
        }

        @Override // freemarker.core.JavaTemplateNumberFormat, freemarker.core.TemplateNumberFormat
        public String formatToPlainText(TemplateNumberModel numberModel) throws TemplateModelException, UnformattableValueException {
            Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
            return format(number);
        }

        @Override // freemarker.core.JavaTemplateNumberFormat, freemarker.core.TemplateNumberFormat
        public boolean isLocaleBound() {
            return false;
        }

        @Override // freemarker.core.JavaTemplateNumberFormat, freemarker.core.BackwardCompatibleTemplateNumberFormat
        String format(Number number) throws UnformattableValueException {
            if ((number instanceof Integer) || (number instanceof Long)) {
                return number.toString();
            }
            return super.format(number);
        }

        @Override // freemarker.core.JavaTemplateNumberFormat, freemarker.core.TemplateValueFormat
        public String getDescription() {
            return "LegacyC(" + super.getDescription() + ")";
        }
    }
}
