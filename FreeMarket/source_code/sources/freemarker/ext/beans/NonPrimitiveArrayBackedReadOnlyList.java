package freemarker.ext.beans;

import java.util.AbstractList;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/NonPrimitiveArrayBackedReadOnlyList.class */
class NonPrimitiveArrayBackedReadOnlyList extends AbstractList {
    private final Object[] array;

    NonPrimitiveArrayBackedReadOnlyList(Object[] array) {
        this.array = array;
    }

    @Override // java.util.AbstractList, java.util.List
    public Object get(int index) {
        return this.array[index];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.array.length;
    }
}
