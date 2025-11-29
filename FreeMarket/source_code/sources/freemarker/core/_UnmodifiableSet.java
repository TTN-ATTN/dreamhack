package freemarker.core;

import java.util.AbstractSet;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_UnmodifiableSet.class */
public abstract class _UnmodifiableSet<E> extends AbstractSet<E> {
    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object o) {
        if (contains(o)) {
            throw new UnsupportedOperationException();
        }
        return false;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        if (!isEmpty()) {
            throw new UnsupportedOperationException();
        }
    }
}
