package freemarker.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/StrongCacheStorage.class */
public class StrongCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
    private final Map map = new ConcurrentHashMap();

    @Override // freemarker.cache.ConcurrentCacheStorage
    public boolean isConcurrent() {
        return true;
    }

    @Override // freemarker.cache.CacheStorage
    public Object get(Object key) {
        return this.map.get(key);
    }

    @Override // freemarker.cache.CacheStorage
    public void put(Object key, Object value) {
        this.map.put(key, value);
    }

    @Override // freemarker.cache.CacheStorage
    public void remove(Object key) {
        this.map.remove(key);
    }

    @Override // freemarker.cache.CacheStorageWithGetSize
    public int getSize() {
        return this.map.size();
    }

    @Override // freemarker.cache.CacheStorage
    public void clear() {
        this.map.clear();
    }
}
