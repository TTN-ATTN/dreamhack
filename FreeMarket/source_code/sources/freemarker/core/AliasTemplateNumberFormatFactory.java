package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.util.Locale;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AliasTemplateNumberFormatFactory.class */
public final class AliasTemplateNumberFormatFactory extends TemplateNumberFormatFactory {
    private final String defaultTargetFormatString;
    private final Map<Locale, String> localizedTargetFormatStrings;

    public AliasTemplateNumberFormatFactory(String targetFormatString) {
        this.defaultTargetFormatString = targetFormatString;
        this.localizedTargetFormatStrings = null;
    }

    public AliasTemplateNumberFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
        this.defaultTargetFormatString = defaultTargetFormatString;
        this.localizedTargetFormatStrings = localizedTargetFormatStrings;
    }

    @Override // freemarker.core.TemplateNumberFormatFactory
    public TemplateNumberFormat get(String params, Locale locale, Environment env) throws TemplateValueFormatException {
        String targetFormatString;
        TemplateFormatUtil.checkHasNoParameters(params);
        try {
            if (this.localizedTargetFormatStrings != null) {
                Locale lookupLocale = locale;
                targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
                while (targetFormatString == null) {
                    Locale lessSpecificLocale = _CoreLocaleUtils.getLessSpecificLocale(lookupLocale);
                    lookupLocale = lessSpecificLocale;
                    if (lessSpecificLocale == null) {
                        break;
                    }
                    targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
                }
            } else {
                targetFormatString = null;
            }
            if (targetFormatString == null) {
                targetFormatString = this.defaultTargetFormatString;
            }
            return env.getTemplateNumberFormat(targetFormatString, locale);
        } catch (TemplateValueFormatException e) {
            throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + StringUtil.jQuote(params) + ". Reason given: " + e.getMessage(), e);
        }
    }
}
