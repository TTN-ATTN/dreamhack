package freemarker.core;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.IdentityHashMap;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_Java16Impl.class */
public class _Java16Impl implements _Java16 {
    public static final _Java16 INSTANCE = new _Java16Impl();

    private _Java16Impl() {
    }

    @Override // freemarker.core._Java16
    public boolean isRecord(Class<?> cl) {
        return cl.isRecord();
    }

    @Override // freemarker.core._Java16
    public Set<Method> getComponentAccessors(Class<?> recordType) {
        RecordComponent[] recordComponents = recordType.getRecordComponents();
        if (recordComponents == null) {
            throw new IllegalArgumentException("Argument must be a record type");
        }
        IdentityHashMap<Method, Void> methods = new IdentityHashMap<>(recordComponents.length);
        for (RecordComponent recordComponent : recordComponents) {
            methods.put(recordComponent.getAccessor(), null);
        }
        return methods.keySet();
    }
}
