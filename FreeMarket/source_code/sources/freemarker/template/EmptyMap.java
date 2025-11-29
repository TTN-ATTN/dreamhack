package freemarker.template;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/EmptyMap.class */
public class EmptyMap implements Map, Cloneable {
    public static final EmptyMap instance = new EmptyMap();

    @Override // java.util.Map
    public void clear() {
    }

    @Override // java.util.Map
    public boolean containsKey(Object arg0) {
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object arg0) {
        return false;
    }

    @Override // java.util.Map
    public Set entrySet() {
        return Collections.EMPTY_SET;
    }

    @Override // java.util.Map
    public Object get(Object arg0) {
        return null;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return true;
    }

    @Override // java.util.Map
    public Set keySet() {
        return Collections.EMPTY_SET;
    }

    @Override // java.util.Map
    public Object put(Object arg0, Object arg1) {
        throw new UnsupportedOperationException("This Map is read-only.");
    }

    @Override // java.util.Map
    public void putAll(Map arg0) {
        if (arg0.entrySet().iterator().hasNext()) {
            throw new UnsupportedOperationException("This Map is read-only.");
        }
    }

    @Override // java.util.Map
    public Object remove(Object arg0) {
        return null;
    }

    @Override // java.util.Map
    public int size() {
        return 0;
    }

    @Override // java.util.Map
    public Collection values() {
        return Collections.EMPTY_LIST;
    }
}
