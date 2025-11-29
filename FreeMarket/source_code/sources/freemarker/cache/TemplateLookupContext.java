package freemarker.cache;

import java.io.IOException;
import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupContext.class */
public abstract class TemplateLookupContext {
    private final String templateName;
    private final Locale templateLocale;
    private final Object customLookupCondition;

    public abstract TemplateLookupResult lookupWithAcquisitionStrategy(String str) throws IOException;

    public abstract TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String str, Locale locale) throws IOException;

    TemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
        this.templateName = templateName;
        this.templateLocale = templateLocale;
        this.customLookupCondition = customLookupCondition;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public Locale getTemplateLocale() {
        return this.templateLocale;
    }

    public Object getCustomLookupCondition() {
        return this.customLookupCondition;
    }

    public TemplateLookupResult createNegativeLookupResult() {
        return TemplateLookupResult.createNegativeResult();
    }
}
