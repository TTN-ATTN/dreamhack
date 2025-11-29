package freemarker.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ArrayIterator.class */
public class _ArrayIterator implements Iterator {
    private final Object[] array;
    private int nextIndex = 0;

    public _ArrayIterator(Object[] array) {
        this.array = array;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextIndex < this.array.length;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (this.nextIndex >= this.array.length) {
            throw new NoSuchElementException();
        }
        Object[] objArr = this.array;
        int i = this.nextIndex;
        this.nextIndex = i + 1;
        return objArr[i];
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
