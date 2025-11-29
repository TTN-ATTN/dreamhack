package freemarker.core;

import freemarker.template.TemplateException;
import java.text.NumberFormat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CFormat.class */
public abstract class CFormat {
    abstract TemplateNumberFormat getTemplateNumberFormat(Environment environment);

    @Deprecated
    abstract NumberFormat getLegacyNumberFormat(Environment environment);

    abstract String formatString(String str, Environment environment) throws TemplateException;

    abstract String getTrueString();

    abstract String getFalseString();

    abstract String getNullString();

    public abstract String getName();

    CFormat() {
    }
}
