package freemarker.ext.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/util/IdentityHashMap.class */
public class IdentityHashMap extends AbstractMap implements Map, Cloneable, Serializable {
    public static final long serialVersionUID = 362498820763181265L;
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;
    private transient int modCount;
    private transient Set keySet;
    private transient Set entrySet;
    private transient Collection values;
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;
    private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();

    static /* synthetic */ int access$308(IdentityHashMap x0) {
        int i = x0.modCount;
        x0.modCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$110(IdentityHashMap x0) {
        int i = x0.count;
        x0.count = i - 1;
        return i;
    }

    public IdentityHashMap(int initialCapacity, float loadFactor) {
        this.modCount = 0;
        this.keySet = null;
        this.entrySet = null;
        this.values = null;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
        }
        initialCapacity = initialCapacity == 0 ? 1 : initialCapacity;
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    public IdentityHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IdentityHashMap() {
        this(11, 0.75f);
    }

    public IdentityHashMap(Map t) {
        this(Math.max(2 * t.size(), 11), 0.75f);
        putAll(t);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.count;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        Entry[] tab = this.table;
        if (value == null) {
            int i = tab.length;
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    Entry entry = tab[i];
                    while (true) {
                        Entry e = entry;
                        if (e != null) {
                            if (e.value != null) {
                                entry = e.next;
                            } else {
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            int i3 = tab.length;
            while (true) {
                int i4 = i3;
                i3--;
                if (i4 > 0) {
                    Entry entry2 = tab[i3];
                    while (true) {
                        Entry e2 = entry2;
                        if (e2 != null) {
                            if (!value.equals(e2.value)) {
                                entry2 = e2.next;
                            } else {
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry entry = tab[index];
            while (true) {
                Entry e = entry;
                if (e != null) {
                    if (e.hash != hash || key != e.key) {
                        entry = e.next;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            Entry entry2 = tab[0];
            while (true) {
                Entry e2 = entry2;
                if (e2 != null) {
                    if (e2.key != null) {
                        entry2 = e2.next;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object get(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry entry = tab[index];
            while (true) {
                Entry e = entry;
                if (e != null) {
                    if (e.hash != hash || key != e.key) {
                        entry = e.next;
                    } else {
                        return e.value;
                    }
                } else {
                    return null;
                }
            }
        } else {
            Entry entry2 = tab[0];
            while (true) {
                Entry e2 = entry2;
                if (e2 != null) {
                    if (e2.key != null) {
                        entry2 = e2.next;
                    } else {
                        return e2.value;
                    }
                } else {
                    return null;
                }
            }
        }
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = (oldCapacity * 2) + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.modCount++;
        this.threshold = (int) (newCapacity * this.loadFactor);
        this.table = newMap;
        int i = oldCapacity;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                Entry old = oldMap[i];
                while (old != null) {
                    Entry e = old;
                    old = old.next;
                    int index = (e.hash & Integer.MAX_VALUE) % newCapacity;
                    e.next = newMap[index];
                    newMap[index] = e;
                }
            } else {
                return;
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object put(Object key, Object value) {
        Entry[] tab = this.table;
        int hash = 0;
        int index = 0;
        if (key != null) {
            hash = System.identityHashCode(key);
            index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry entry = tab[index];
            while (true) {
                Entry e = entry;
                if (e == null) {
                    break;
                }
                if (e.hash != hash || key != e.key) {
                    entry = e.next;
                } else {
                    Object old = e.value;
                    e.value = value;
                    return old;
                }
            }
        } else {
            Entry entry2 = tab[0];
            while (true) {
                Entry e2 = entry2;
                if (e2 == null) {
                    break;
                }
                if (e2.key != null) {
                    entry2 = e2.next;
                } else {
                    Object old2 = e2.value;
                    e2.value = value;
                    return old2;
                }
            }
        }
        this.modCount++;
        if (this.count >= this.threshold) {
            rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
        }
        tab[index] = new Entry(hash, key, value, tab[index]);
        this.count++;
        return null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object remove(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry prev = null;
            for (Entry e = tab[index]; e != null; e = e.next) {
                if (e.hash != hash || key != e.key) {
                    prev = e;
                } else {
                    this.modCount++;
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        tab[index] = e.next;
                    }
                    this.count--;
                    Object oldValue = e.value;
                    e.value = null;
                    return oldValue;
                }
            }
            return null;
        }
        Entry prev2 = null;
        for (Entry e2 = tab[0]; e2 != null; e2 = e2.next) {
            if (e2.key != null) {
                prev2 = e2;
            } else {
                this.modCount++;
                if (prev2 != null) {
                    prev2.next = e2.next;
                } else {
                    tab[0] = e2.next;
                }
                this.count--;
                Object oldValue2 = e2.value;
                e2.value = null;
                return oldValue2;
            }
        }
        return null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map t) {
        for (Map.Entry e : t.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        Entry[] tab = this.table;
        this.modCount++;
        int index = tab.length;
        while (true) {
            index--;
            if (index >= 0) {
                tab[index] = null;
            } else {
                this.count = 0;
                return;
            }
        }
    }

    @Override // java.util.AbstractMap
    public Object clone() {
        try {
            IdentityHashMap t = (IdentityHashMap) super.clone();
            t.table = new Entry[this.table.length];
            int i = this.table.length;
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    t.table[i] = this.table[i] != null ? (Entry) this.table[i].clone() : null;
                } else {
                    t.keySet = null;
                    t.entrySet = null;
                    t.values = null;
                    t.modCount = 0;
                    return t;
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet() { // from class: freemarker.ext.util.IdentityHashMap.1
                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(0);
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public boolean contains(Object o) {
                    return IdentityHashMap.this.containsKey(o);
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public boolean remove(Object o) {
                    int oldSize = IdentityHashMap.this.count;
                    IdentityHashMap.this.remove(o);
                    return IdentityHashMap.this.count != oldSize;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.keySet;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection values() {
        if (this.values == null) {
            this.values = new AbstractCollection() { // from class: freemarker.ext.util.IdentityHashMap.2
                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(1);
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public boolean contains(Object o) {
                    return IdentityHashMap.this.containsValue(o);
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new AbstractSet() { // from class: freemarker.ext.util.IdentityHashMap.3
                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(2);
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object key = entry.getKey();
                    Entry[] tab = IdentityHashMap.this.table;
                    int hash = key == null ? 0 : System.identityHashCode(key);
                    int index = (hash & Integer.MAX_VALUE) % tab.length;
                    Entry entry2 = tab[index];
                    while (true) {
                        Entry e = entry2;
                        if (e != null) {
                            if (e.hash != hash || !e.equals(entry)) {
                                entry2 = e.next;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object key = entry.getKey();
                    Entry[] tab = IdentityHashMap.this.table;
                    int hash = key == null ? 0 : System.identityHashCode(key);
                    int index = (hash & Integer.MAX_VALUE) % tab.length;
                    Entry prev = null;
                    for (Entry e = tab[index]; e != null; e = e.next) {
                        if (e.hash != hash || !e.equals(entry)) {
                            prev = e;
                        } else {
                            IdentityHashMap.access$308(IdentityHashMap.this);
                            if (prev != null) {
                                prev.next = e.next;
                            } else {
                                tab[index] = e.next;
                            }
                            IdentityHashMap.access$110(IdentityHashMap.this);
                            e.value = null;
                            return true;
                        }
                    }
                    return false;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.entrySet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Iterator getHashIterator(int type) {
        if (this.count == 0) {
            return emptyHashIterator;
        }
        return new HashIterator(type);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/util/IdentityHashMap$Entry.class */
    private static class Entry implements Map.Entry {
        int hash;
        Object key;
        Object value;
        Entry next;

        Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        protected Object clone() {
            return new Entry(this.hash, this.key, this.value, this.next == null ? null : (Entry) this.next.clone());
        }

        @Override // java.util.Map.Entry
        public Object getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public Object getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;
            return this.key == e.getKey() && (this.value != null ? this.value.equals(e.getValue()) : e.getValue() == null);
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return this.hash ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/util/IdentityHashMap$EmptyHashIterator.class */
    private static class EmptyHashIterator implements Iterator {
        EmptyHashIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/util/IdentityHashMap$HashIterator.class */
    private class HashIterator implements Iterator {
        Entry[] table;
        int index;
        Entry entry = null;
        Entry lastReturned = null;
        int type;
        private int expectedModCount;

        HashIterator(int type) {
            this.table = IdentityHashMap.this.table;
            this.index = this.table.length;
            this.expectedModCount = IdentityHashMap.this.modCount;
            this.type = type;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            Entry e = this.entry;
            int i = this.index;
            Entry[] t = this.table;
            while (e == null && i > 0) {
                i--;
                e = t[i];
            }
            this.entry = e;
            this.index = i;
            return e != null;
        }

        @Override // java.util.Iterator
        public Object next() {
            if (IdentityHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry et = this.entry;
            int i = this.index;
            Entry[] t = this.table;
            while (et == null && i > 0) {
                i--;
                et = t[i];
            }
            this.entry = et;
            this.index = i;
            if (et != null) {
                Entry e = this.entry;
                this.lastReturned = e;
                this.entry = e.next;
                return this.type == 0 ? e.key : this.type == 1 ? e.value : e;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.lastReturned != null) {
                if (IdentityHashMap.this.modCount == this.expectedModCount) {
                    Entry[] tab = IdentityHashMap.this.table;
                    int index = (this.lastReturned.hash & Integer.MAX_VALUE) % tab.length;
                    Entry prev = null;
                    for (Entry e = tab[index]; e != null; e = e.next) {
                        if (e != this.lastReturned) {
                            prev = e;
                        } else {
                            IdentityHashMap.access$308(IdentityHashMap.this);
                            this.expectedModCount++;
                            if (prev == null) {
                                tab[index] = e.next;
                            } else {
                                prev.next = e.next;
                            }
                            IdentityHashMap.access$110(IdentityHashMap.this);
                            this.lastReturned = null;
                            return;
                        }
                    }
                    throw new ConcurrentModificationException();
                }
                throw new ConcurrentModificationException();
            }
            throw new IllegalStateException();
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.count);
        for (int index = this.table.length - 1; index >= 0; index--) {
            Entry entry = this.table[index];
            while (true) {
                Entry entry2 = entry;
                if (entry2 != null) {
                    s.writeObject(entry2.key);
                    s.writeObject(entry2.value);
                    entry = entry2.next;
                }
            }
        }
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();
        int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        int size = s.readInt();
        for (int i = 0; i < size; i++) {
            Object key = s.readObject();
            Object value = s.readObject();
            put(key, value);
        }
    }

    int capacity() {
        return this.table.length;
    }

    float loadFactor() {
        return this.loadFactor;
    }
}
