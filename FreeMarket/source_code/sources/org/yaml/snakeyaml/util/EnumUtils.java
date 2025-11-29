package org.yaml.snakeyaml.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/snakeyaml-1.30.jar:org/yaml/snakeyaml/util/EnumUtils.class */
public class EnumUtils {
    public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().compareToIgnoreCase(name) == 0) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
    }
}
