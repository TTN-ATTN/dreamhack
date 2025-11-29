package freemarker.cache;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/NullCacheStorage.class */
public class NullCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
    public static final NullCacheStorage INSTANCE = new NullCacheStorage();

    @Override // freemarker.cache.ConcurrentCacheStorage
    public boolean isConcurrent() {
        return true;
    }

    @Override // freemarker.cache.CacheStorage
    public Object get(Object key) {
        return null;
    }

    @Override // freemarker.cache.CacheStorage
    public void put(Object key, Object value) {
    }

    @Override // freemarker.cache.CacheStorage
    public void remove(Object key) {
    }

    @Override // freemarker.cache.CacheStorage
    public void clear() {
    }

    @Override // freemarker.cache.CacheStorageWithGetSize
    public int getSize() {
        return 0;
    }
}
