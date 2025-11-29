package freemarker.cache;

import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.net.URL;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/ClassTemplateLoader.class */
public class ClassTemplateLoader extends URLTemplateLoader {
    private final Class<?> resourceLoaderClass;
    private final ClassLoader classLoader;
    private final String basePackagePath;

    @Deprecated
    public ClassTemplateLoader() {
        this(null, true, null, "/");
    }

    @Deprecated
    public ClassTemplateLoader(Class<?> resourceLoaderClass) {
        this(resourceLoaderClass, "");
    }

    public ClassTemplateLoader(Class<?> resourceLoaderClass, String basePackagePath) {
        this(resourceLoaderClass, false, null, basePackagePath);
    }

    public ClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
        this(null, true, classLoader, basePackagePath);
    }

    private ClassTemplateLoader(Class<?> resourceLoaderClass, boolean allowNullResourceLoaderClass, ClassLoader classLoader, String basePackagePath) {
        if (!allowNullResourceLoaderClass) {
            NullArgumentException.check("resourceLoaderClass", resourceLoaderClass);
        }
        NullArgumentException.check("basePackagePath", basePackagePath);
        this.resourceLoaderClass = classLoader == null ? resourceLoaderClass == null ? getClass() : resourceLoaderClass : null;
        if (this.resourceLoaderClass == null && classLoader == null) {
            throw new NullArgumentException("classLoader");
        }
        this.classLoader = classLoader;
        String canonBasePackagePath = canonicalizePrefix(basePackagePath);
        if (this.classLoader != null && canonBasePackagePath.startsWith("/")) {
            canonBasePackagePath = canonBasePackagePath.substring(1);
        }
        this.basePackagePath = canonBasePackagePath;
    }

    @Override // freemarker.cache.URLTemplateLoader
    protected URL getURL(String name) {
        String fullPath = this.basePackagePath + name;
        if (!this.basePackagePath.equals("/") || isSchemeless(fullPath)) {
            return this.resourceLoaderClass != null ? this.resourceLoaderClass.getResource(fullPath) : this.classLoader.getResource(fullPath);
        }
        return null;
    }

    private static boolean isSchemeless(String fullPath) {
        char c;
        int i = 0;
        int ln = fullPath.length();
        if (0 < ln && fullPath.charAt(0) == '/') {
            i = 0 + 1;
        }
        while (i < ln && (c = fullPath.charAt(i)) != '/') {
            if (c == ':') {
                return false;
            }
            i++;
        }
        return true;
    }

    public String toString() {
        String str;
        StringBuilder sbAppend = new StringBuilder().append(TemplateLoaderUtils.getClassNameForToString(this)).append("(");
        if (this.resourceLoaderClass != null) {
            str = "resourceLoaderClass=" + this.resourceLoaderClass.getName();
        } else {
            str = "classLoader=" + StringUtil.jQuote(this.classLoader);
        }
        StringBuilder sbAppend2 = sbAppend.append(str).append(", basePackagePath=").append(StringUtil.jQuote(this.basePackagePath));
        String str2 = (this.resourceLoaderClass == null || this.basePackagePath.startsWith("/")) ? "" : " /* relatively to resourceLoaderClass pkg */";
        return sbAppend2.append(str2).append(")").toString();
    }

    public Class getResourceLoaderClass() {
        return this.resourceLoaderClass;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public String getBasePackagePath() {
        return this.basePackagePath;
    }
}
