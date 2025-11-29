package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AliasTemplateDateFormatFactory.class */
public final class AliasTemplateDateFormatFactory extends TemplateDateFormatFactory {
    private final String defaultTargetFormatString;
    private final Map<Locale, String> localizedTargetFormatStrings;

    public AliasTemplateDateFormatFactory(String targetFormatString) {
        this.defaultTargetFormatString = targetFormatString;
        this.localizedTargetFormatStrings = null;
    }

    public AliasTemplateDateFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
        this.defaultTargetFormatString = defaultTargetFormatString;
        this.localizedTargetFormatStrings = localizedTargetFormatStrings;
    }

    @Override // freemarker.core.TemplateDateFormatFactory
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws TemplateValueFormatException {
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
            return env.getTemplateDateFormat(targetFormatString, dateType, locale, timeZone, zonelessInput);
        } catch (TemplateValueFormatException e) {
            throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + StringUtil.jQuote(params) + ". Reason given: " + e.getMessage(), e);
        }
    }
}
