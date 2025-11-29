package freemarker.core;

import freemarker.ext.beans.BeansWrapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_Java9Impl.class */
public class _Java9Impl implements _Java9 {
    public static final _Java9 INSTANCE = new _Java9Impl();
    private static final Module ACCESSOR_MODULE = BeansWrapper.class.getModule();

    private _Java9Impl() {
    }

    @Override // freemarker.core._Java9
    public boolean isAccessibleAccordingToModuleExports(Class<?> accessedClass) {
        Module accessedModule = accessedClass.getModule();
        Package accessedPackage = accessedClass.getPackage();
        if (accessedPackage == null) {
            return true;
        }
        return accessedModule.isExported(accessedPackage.getName(), ACCESSOR_MODULE);
    }
}
