package freemarker.cache;

import freemarker.template.Configuration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLoaderUtils.class */
public final class TemplateLoaderUtils {
    private TemplateLoaderUtils() {
    }

    public static String getClassNameForToString(TemplateLoader templateLoader) {
        Class tlClass = templateLoader.getClass();
        Package tlPackage = tlClass.getPackage();
        return (tlPackage == Configuration.class.getPackage() || tlPackage == TemplateLoader.class.getPackage()) ? tlClass.getSimpleName() : tlClass.getName();
    }
}
