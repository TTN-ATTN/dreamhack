package freemarker.cache;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/CacheStorage.class */
public interface CacheStorage {
    Object get(Object obj);

    void put(Object obj, Object obj2);

    void remove(Object obj);

    void clear();
}
