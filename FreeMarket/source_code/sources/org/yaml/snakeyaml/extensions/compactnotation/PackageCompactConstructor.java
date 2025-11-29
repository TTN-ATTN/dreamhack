package org.yaml.snakeyaml.extensions.compactnotation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/snakeyaml-1.30.jar:org/yaml/snakeyaml/extensions/compactnotation/PackageCompactConstructor.class */
public class PackageCompactConstructor extends CompactConstructor {
    private String packageName;

    public PackageCompactConstructor(String packageName) {
        this.packageName = packageName;
    }

    @Override // org.yaml.snakeyaml.constructor.Constructor
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf(46) < 0) {
            try {
                Class<?> clazz = Class.forName(this.packageName + "." + name);
                return clazz;
            } catch (ClassNotFoundException e) {
            }
        }
        return super.getClassForName(name);
    }
}
