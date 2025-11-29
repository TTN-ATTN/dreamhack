package freemarker.core;

import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateNumberFormatFactory.class */
public abstract class TemplateNumberFormatFactory extends TemplateValueFormatFactory {
    public abstract TemplateNumberFormat get(String str, Locale locale, Environment environment) throws TemplateValueFormatException;
}
