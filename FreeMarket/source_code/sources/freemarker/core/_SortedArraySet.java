package freemarker.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_SortedArraySet.class */
public class _SortedArraySet<E> extends _UnmodifiableSet<E> {
    private final E[] array;

    public _SortedArraySet(E[] array) {
        this.array = array;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.array.length;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(Object o) {
        return Arrays.binarySearch(this.array, o) >= 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<E> iterator() {
        return new _ArrayIterator(this.array);
    }

    @Override // freemarker.core._UnmodifiableSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    @Override // freemarker.core._UnmodifiableSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override // freemarker.core._UnmodifiableSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
