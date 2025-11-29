package org.springframework.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/DecoratingClassLoader.class */
public abstract class DecoratingClassLoader extends ClassLoader {
    private final Set<String> excludedPackages;
    private final Set<String> excludedClasses;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public DecoratingClassLoader() {
        this.excludedPackages = Collections.newSetFromMap(new ConcurrentHashMap(8));
        this.excludedClasses = Collections.newSetFromMap(new ConcurrentHashMap(8));
    }

    public DecoratingClassLoader(@Nullable ClassLoader parent) {
        super(parent);
        this.excludedPackages = Collections.newSetFromMap(new ConcurrentHashMap(8));
        this.excludedClasses = Collections.newSetFromMap(new ConcurrentHashMap(8));
    }

    public void excludePackage(String packageName) {
        Assert.notNull(packageName, "Package name must not be null");
        this.excludedPackages.add(packageName);
    }

    public void excludeClass(String className) {
        Assert.notNull(className, "Class name must not be null");
        this.excludedClasses.add(className);
    }

    protected boolean isExcluded(String className) {
        if (this.excludedClasses.contains(className)) {
            return true;
        }
        for (String packageName : this.excludedPackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
}
