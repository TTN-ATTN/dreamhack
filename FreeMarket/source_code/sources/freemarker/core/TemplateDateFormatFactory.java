package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateDateFormatFactory.class */
public abstract class TemplateDateFormatFactory extends TemplateValueFormatFactory {
    public abstract TemplateDateFormat get(String str, int i, Locale locale, TimeZone timeZone, boolean z, Environment environment) throws TemplateValueFormatException;
}
