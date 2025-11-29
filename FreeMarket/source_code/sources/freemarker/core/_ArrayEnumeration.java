package freemarker.core;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ArrayEnumeration.class */
public class _ArrayEnumeration implements Enumeration {
    private final Object[] array;
    private final int size;
    private int nextIndex = 0;

    public _ArrayEnumeration(Object[] array, int size) {
        this.array = array;
        this.size = size;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return this.nextIndex < this.size;
    }

    @Override // java.util.Enumeration
    public Object nextElement() {
        if (this.nextIndex >= this.size) {
            throw new NoSuchElementException();
        }
        Object[] objArr = this.array;
        int i = this.nextIndex;
        this.nextIndex = i + 1;
        return objArr[i];
    }
}
