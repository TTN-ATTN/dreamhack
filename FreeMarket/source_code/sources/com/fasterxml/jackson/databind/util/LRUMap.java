package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/util/LRUMap.class */
public class LRUMap<K, V> implements LookupCache<K, V>, Serializable {
    private static final long serialVersionUID = 1;
    protected final transient int _maxEntries;
    protected final transient ConcurrentHashMap<K, V> _map;
    protected transient int _jdkSerializeMaxEntries;

    public LRUMap(int initialEntries, int maxEntries) {
        this._map = new ConcurrentHashMap<>(initialEntries, 0.8f, 4);
        this._maxEntries = maxEntries;
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V put(K key, V value) {
        if (this._map.size() >= this._maxEntries) {
            synchronized (this) {
                if (this._map.size() >= this._maxEntries) {
                    clear();
                }
            }
        }
        return this._map.put(key, value);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V putIfAbsent(K key, V value) {
        if (this._map.size() >= this._maxEntries) {
            synchronized (this) {
                if (this._map.size() >= this._maxEntries) {
                    clear();
                }
            }
        }
        return this._map.putIfAbsent(key, value);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V get(Object key) {
        return this._map.get(key);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public void clear() {
        this._map.clear();
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public int size() {
        return this._map.size();
    }

    private void readObject(ObjectInputStream in) throws IOException {
        this._jdkSerializeMaxEntries = in.readInt();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this._jdkSerializeMaxEntries);
    }

    protected Object readResolve() {
        return new LRUMap(this._jdkSerializeMaxEntries, this._jdkSerializeMaxEntries);
    }
}
