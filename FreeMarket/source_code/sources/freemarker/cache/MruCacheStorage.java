package freemarker.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MruCacheStorage.class */
public class MruCacheStorage implements CacheStorageWithGetSize {
    private final MruEntry strongHead = new MruEntry();
    private final MruEntry softHead = new MruEntry();
    private final Map map;
    private final ReferenceQueue refQueue;
    private final int strongSizeLimit;
    private final int softSizeLimit;
    private int strongSize;
    private int softSize;

    public MruCacheStorage(int strongSizeLimit, int softSizeLimit) {
        this.softHead.linkAfter(this.strongHead);
        this.map = new HashMap();
        this.refQueue = new ReferenceQueue();
        this.strongSize = 0;
        this.softSize = 0;
        if (strongSizeLimit < 0) {
            throw new IllegalArgumentException("strongSizeLimit < 0");
        }
        if (softSizeLimit < 0) {
            throw new IllegalArgumentException("softSizeLimit < 0");
        }
        this.strongSizeLimit = strongSizeLimit;
        this.softSizeLimit = softSizeLimit;
    }

    @Override // freemarker.cache.CacheStorage
    public Object get(Object key) {
        removeClearedReferences();
        MruEntry entry = (MruEntry) this.map.get(key);
        if (entry == null) {
            return null;
        }
        relinkEntryAfterStrongHead(entry, null);
        Object value = entry.getValue();
        if (value instanceof MruReference) {
            return ((MruReference) value).get();
        }
        return value;
    }

    @Override // freemarker.cache.CacheStorage
    public void put(Object key, Object value) {
        removeClearedReferences();
        MruEntry entry = (MruEntry) this.map.get(key);
        if (entry == null) {
            MruEntry entry2 = new MruEntry(key, value);
            this.map.put(key, entry2);
            linkAfterStrongHead(entry2);
            return;
        }
        relinkEntryAfterStrongHead(entry, value);
    }

    @Override // freemarker.cache.CacheStorage
    public void remove(Object key) {
        removeClearedReferences();
        removeInternal(key);
    }

    private void removeInternal(Object key) {
        MruEntry entry = (MruEntry) this.map.remove(key);
        if (entry != null) {
            unlinkEntryAndInspectIfSoft(entry);
        }
    }

    @Override // freemarker.cache.CacheStorage
    public void clear() {
        this.strongHead.makeHead();
        this.softHead.linkAfter(this.strongHead);
        this.map.clear();
        this.softSize = 0;
        this.strongSize = 0;
        while (this.refQueue.poll() != null) {
        }
    }

    private void relinkEntryAfterStrongHead(MruEntry entry, Object newValue) {
        if (unlinkEntryAndInspectIfSoft(entry) && newValue == null) {
            MruReference mref = (MruReference) entry.getValue();
            Object strongValue = mref.get();
            if (strongValue != null) {
                entry.setValue(strongValue);
                linkAfterStrongHead(entry);
                return;
            } else {
                this.map.remove(mref.getKey());
                return;
            }
        }
        if (newValue != null) {
            entry.setValue(newValue);
        }
        linkAfterStrongHead(entry);
    }

    private void linkAfterStrongHead(MruEntry entry) {
        entry.linkAfter(this.strongHead);
        if (this.strongSize == this.strongSizeLimit) {
            MruEntry lruStrong = this.softHead.getPrevious();
            if (lruStrong != this.strongHead) {
                lruStrong.unlink();
                if (this.softSizeLimit > 0) {
                    lruStrong.linkAfter(this.softHead);
                    lruStrong.setValue(new MruReference(lruStrong, this.refQueue));
                    if (this.softSize == this.softSizeLimit) {
                        MruEntry lruSoft = this.strongHead.getPrevious();
                        lruSoft.unlink();
                        this.map.remove(lruSoft.getKey());
                        return;
                    }
                    this.softSize++;
                    return;
                }
                this.map.remove(lruStrong.getKey());
                return;
            }
            return;
        }
        this.strongSize++;
    }

    private boolean unlinkEntryAndInspectIfSoft(MruEntry entry) {
        entry.unlink();
        if (entry.getValue() instanceof MruReference) {
            this.softSize--;
            return true;
        }
        this.strongSize--;
        return false;
    }

    private void removeClearedReferences() {
        while (true) {
            MruReference ref = (MruReference) this.refQueue.poll();
            if (ref != null) {
                removeInternal(ref.getKey());
            } else {
                return;
            }
        }
    }

    public int getStrongSizeLimit() {
        return this.strongSizeLimit;
    }

    public int getSoftSizeLimit() {
        return this.softSizeLimit;
    }

    public int getStrongSize() {
        return this.strongSize;
    }

    public int getSoftSize() {
        removeClearedReferences();
        return this.softSize;
    }

    @Override // freemarker.cache.CacheStorageWithGetSize
    public int getSize() {
        return getSoftSize() + getStrongSize();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MruCacheStorage$MruEntry.class */
    private static final class MruEntry {
        private MruEntry prev;
        private MruEntry next;
        private final Object key;
        private Object value;

        MruEntry() {
            makeHead();
            this.value = null;
            this.key = null;
        }

        MruEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        Object getKey() {
            return this.key;
        }

        Object getValue() {
            return this.value;
        }

        void setValue(Object value) {
            this.value = value;
        }

        MruEntry getPrevious() {
            return this.prev;
        }

        void linkAfter(MruEntry entry) {
            this.next = entry.next;
            entry.next = this;
            this.prev = entry;
            this.next.prev = this;
        }

        void unlink() {
            this.next.prev = this.prev;
            this.prev.next = this.next;
            this.prev = null;
            this.next = null;
        }

        void makeHead() {
            this.next = this;
            this.prev = this;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MruCacheStorage$MruReference.class */
    private static class MruReference extends SoftReference {
        private final Object key;

        MruReference(MruEntry entry, ReferenceQueue queue) {
            super(entry.getValue(), queue);
            this.key = entry.getKey();
        }

        Object getKey() {
            return this.key;
        }
    }
}
