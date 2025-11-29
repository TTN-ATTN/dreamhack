package freemarker.core;

import freemarker.log.Logger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaTemplateNumberFormatFactory.class */
class JavaTemplateNumberFormatFactory extends TemplateNumberFormatFactory {
    static final JavaTemplateNumberFormatFactory INSTANCE = new JavaTemplateNumberFormatFactory();
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static final ConcurrentHashMap<CacheKey, NumberFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap<>();
    private static final int LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE = 1024;

    private JavaTemplateNumberFormatFactory() {
    }

    @Override // freemarker.core.TemplateNumberFormatFactory
    public TemplateNumberFormat get(String params, Locale locale, Environment env) throws InvalidFormatParametersException {
        CacheKey cacheKey = new CacheKey(params, locale);
        NumberFormat jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
        if (jFormat == null) {
            if ("number".equals(params)) {
                jFormat = NumberFormat.getNumberInstance(locale);
            } else if ("currency".equals(params)) {
                jFormat = NumberFormat.getCurrencyInstance(locale);
            } else if ("percent".equals(params)) {
                jFormat = NumberFormat.getPercentInstance(locale);
            } else {
                try {
                    jFormat = ExtendedDecimalFormatParser.parse(params, locale);
                } catch (java.text.ParseException e) {
                    String msg = e.getMessage();
                    throw new InvalidFormatParametersException(msg != null ? msg : "Invalid DecimalFormat pattern", e);
                }
            }
            if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                boolean triggered = false;
                synchronized (JavaTemplateNumberFormatFactory.class) {
                    if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                        triggered = true;
                        GLOBAL_FORMAT_CACHE.clear();
                    }
                }
                if (triggered) {
                    LOG.warn("Global Java NumberFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
                }
            }
            NumberFormat prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat);
            if (prevJFormat != null) {
                jFormat = prevJFormat;
            }
        }
        return new JavaTemplateNumberFormat((NumberFormat) jFormat.clone(), params);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JavaTemplateNumberFormatFactory$CacheKey.class */
    private static final class CacheKey {
        private final String pattern;
        private final Locale locale;

        CacheKey(String pattern, Locale locale) {
            this.pattern = pattern;
            this.locale = locale;
        }

        public boolean equals(Object o) {
            if (o instanceof CacheKey) {
                CacheKey fk = (CacheKey) o;
                return fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale);
            }
            return false;
        }

        public int hashCode() {
            return this.pattern.hashCode() ^ this.locale.hashCode();
        }
    }
}
