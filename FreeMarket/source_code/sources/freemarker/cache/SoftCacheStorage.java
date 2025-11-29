package freemarker.cache;

import freemarker.template.utility.UndeclaredThrowableException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/SoftCacheStorage.class */
public class SoftCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
    private static final Method atomicRemove = getAtomicRemoveMethod();
    private final ReferenceQueue queue;
    private final Map map;
    private final boolean concurrent;

    public SoftCacheStorage() {
        this(new ConcurrentHashMap());
    }

    @Override // freemarker.cache.ConcurrentCacheStorage
    public boolean isConcurrent() {
        return this.concurrent;
    }

    public SoftCacheStorage(Map backingMap) {
        this.queue = new ReferenceQueue();
        this.map = backingMap;
        this.concurrent = this.map instanceof ConcurrentMap;
    }

    @Override // freemarker.cache.CacheStorage
    public Object get(Object key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        processQueue();
        Reference ref = (Reference) this.map.get(key);
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override // freemarker.cache.CacheStorage
    public void put(Object key, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        processQueue();
        this.map.put(key, new SoftValueReference(key, value, this.queue));
    }

    @Override // freemarker.cache.CacheStorage
    public void remove(Object key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        processQueue();
        this.map.remove(key);
    }

    @Override // freemarker.cache.CacheStorage
    public void clear() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.map.clear();
        processQueue();
    }

    @Override // freemarker.cache.CacheStorageWithGetSize
    public int getSize() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        processQueue();
        return this.map.size();
    }

    private void processQueue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        while (true) {
            SoftValueReference ref = (SoftValueReference) this.queue.poll();
            if (ref == null) {
                return;
            }
            Object key = ref.getKey();
            if (this.concurrent) {
                try {
                    atomicRemove.invoke(this.map, key, ref);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new UndeclaredThrowableException(e);
                }
            } else if (this.map.get(key) == ref) {
                this.map.remove(key);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/SoftCacheStorage$SoftValueReference.class */
    private static final class SoftValueReference extends SoftReference {
        private final Object key;

        SoftValueReference(Object key, Object value, ReferenceQueue queue) {
            super(value, queue);
            this.key = key;
        }

        Object getKey() {
            return this.key;
        }
    }

    private static Method getAtomicRemoveMethod() {
        try {
            return Class.forName("java.util.concurrent.ConcurrentMap").getMethod("remove", Object.class, Object.class);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e2) {
            throw new UndeclaredThrowableException(e2);
        }
    }
}
