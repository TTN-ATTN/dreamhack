package freemarker.cache;

import freemarker.template.MalformedTemplateNameException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/_CacheAPI.class */
public final class _CacheAPI {
    private _CacheAPI() {
    }

    public static String toRootBasedName(TemplateNameFormat templateNameFormat, String baseName, String targetName) throws MalformedTemplateNameException {
        return templateNameFormat.toRootBasedName(baseName, targetName);
    }

    public static String normalizeRootBasedName(TemplateNameFormat templateNameFormat, String name) throws MalformedTemplateNameException {
        return templateNameFormat.normalizeRootBasedName(name);
    }

    public static String rootBasedNameToAbsoluteName(TemplateNameFormat templateNameFormat, String rootBasedName) throws MalformedTemplateNameException {
        return templateNameFormat.rootBasedNameToAbsoluteName(rootBasedName);
    }
}
