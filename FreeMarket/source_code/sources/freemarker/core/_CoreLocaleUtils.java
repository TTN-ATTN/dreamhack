package freemarker.core;

import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_CoreLocaleUtils.class */
public class _CoreLocaleUtils {
    public static Locale getLessSpecificLocale(Locale locale) {
        String country = locale.getCountry();
        if (locale.getVariant().length() != 0) {
            String language = locale.getLanguage();
            return country != null ? new Locale(language, country) : new Locale(language);
        }
        if (country.length() != 0) {
            return new Locale(locale.getLanguage());
        }
        return null;
    }
}
