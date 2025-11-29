package org.springframework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/MultiValueMapAdapter.class */
public class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable {
    private final Map<K, List<V>> targetMap;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object put(Object key, Object value) {
        return put((MultiValueMapAdapter<K, V>) key, (List) value);
    }

    public MultiValueMapAdapter(Map<K, List<V>> targetMap) {
        Assert.notNull(targetMap, "'targetMap' must not be null");
        this.targetMap = targetMap;
    }

    @Override // org.springframework.util.MultiValueMap
    @Nullable
    public V getFirst(K key) {
        List<V> values = this.targetMap.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(K key, @Nullable V value) {
        List<V> values = this.targetMap.computeIfAbsent(key, k -> {
            return new ArrayList(1);
        });
        values.add(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(K key, List<? extends V> values) {
        List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> {
            return new ArrayList(1);
        });
        currentValues.addAll(values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<K, V> values) {
        for (Map.Entry<K, V> entry : values.entrySet()) {
            addAll(entry.getKey(), (List) entry.getValue());
        }
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(K key, @Nullable V value) {
        List<V> values = new ArrayList<>(1);
        values.add(value);
        this.targetMap.put(key, values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<K, V> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<K, V> toSingleValueMap() {
        Map<K, V> singleValueMap = CollectionUtils.newLinkedHashMap(this.targetMap.size());
        this.targetMap.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                singleValueMap.put(key, values.get(0));
            }
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.targetMap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.targetMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override // java.util.Map
    @Nullable
    public List<V> get(Object key) {
        return this.targetMap.get(key);
    }

    @Nullable
    public List<V> put(K key, List<V> value) {
        return this.targetMap.put(key, value);
    }

    @Override // java.util.Map
    @Nullable
    public List<V> remove(Object key) {
        return this.targetMap.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends List<V>> map) {
        this.targetMap.putAll(map);
    }

    @Override // java.util.Map
    public void clear() {
        this.targetMap.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.targetMap.keySet();
    }

    @Override // java.util.Map
    public Collection<List<V>> values() {
        return this.targetMap.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.targetMap.entrySet();
    }

    @Override // java.util.Map
    public boolean equals(@Nullable Object other) {
        return this == other || this.targetMap.equals(other);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    public String toString() {
        return this.targetMap.toString();
    }
}
