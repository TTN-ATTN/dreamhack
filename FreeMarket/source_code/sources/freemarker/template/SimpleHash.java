package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModelEx2;
import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleHash.class */
public class SimpleHash extends WrappingTemplateModel implements TemplateHashModelEx2, Serializable {
    private final Map map;
    private boolean putFailed;
    private Map unwrappedMap;

    @Deprecated
    public SimpleHash() {
        this((ObjectWrapper) null);
    }

    @Deprecated
    public SimpleHash(Map map) {
        this(map, null);
    }

    public SimpleHash(ObjectWrapper wrapper) {
        super(wrapper);
        this.map = new HashMap();
    }

    public SimpleHash(Map<String, Object> directMap, ObjectWrapper wrapper, int overloadDistinction) {
        super(wrapper);
        this.map = directMap;
    }

    public SimpleHash(Map map, ObjectWrapper wrapper) throws InterruptedException {
        Map mapCopy;
        super(wrapper);
        try {
            mapCopy = copyMap(map);
        } catch (ConcurrentModificationException e) {
            try {
                Thread.sleep(5L);
            } catch (InterruptedException e2) {
            }
            synchronized (map) {
                mapCopy = copyMap(map);
            }
        }
        this.map = mapCopy;
    }

    protected Map copyMap(Map map) {
        if (map instanceof HashMap) {
            return (Map) ((HashMap) map).clone();
        }
        if (map instanceof SortedMap) {
            if (map instanceof TreeMap) {
                return (Map) ((TreeMap) map).clone();
            }
            return new TreeMap((SortedMap) map);
        }
        return new HashMap(map);
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
        this.unwrappedMap = null;
    }

    public void put(String key, boolean b) {
        put(key, b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:20:0x008a  */
    /* JADX WARN: Type inference failed for: r0v34, types: [java.lang.Character, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v38, types: [java.util.Map] */
    /* JADX WARN: Type inference failed for: r0v43, types: [java.util.Map] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public freemarker.template.TemplateModel get(java.lang.String r11) throws freemarker.template.TemplateModelException {
        /*
            Method dump skipped, instructions count: 286
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.SimpleHash.get(java.lang.String):freemarker.template.TemplateModel");
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    public void putAll(Map m) {
        for (Map.Entry entry : m.entrySet()) {
            put((String) entry.getKey(), entry.getValue());
        }
    }

    public Map toMap() throws TemplateModelException {
        if (this.unwrappedMap == null) {
            Class mapClass = this.map.getClass();
            try {
                Map m = (Map) mapClass.newInstance();
                BeansWrapper bw = BeansWrapper.getDefaultInstance();
                for (Map.Entry entry : this.map.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof TemplateModel) {
                        value = bw.unwrap((TemplateModel) value);
                    }
                    m.put(key, value);
                }
                this.unwrappedMap = m;
            } catch (Exception e) {
                throw new TemplateModelException("Error instantiating map of type " + mapClass.getName() + "\n" + e.getMessage());
            }
        }
        return this.unwrappedMap;
    }

    public String toString() {
        return this.map.toString();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map == null || this.map.isEmpty();
    }

    public TemplateCollectionModel keys() {
        return new SimpleCollection((Collection) this.map.keySet(), getObjectWrapper());
    }

    public TemplateCollectionModel values() {
        return new SimpleCollection(this.map.values(), getObjectWrapper());
    }

    public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
        return new MapKeyValuePairIterator(this.map, getObjectWrapper());
    }

    public SimpleHash synchronizedWrapper() {
        return new SynchronizedHash();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleHash$SynchronizedHash.class */
    private class SynchronizedHash extends SimpleHash {
        private SynchronizedHash() {
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModel
        public boolean isEmpty() {
            boolean zIsEmpty;
            synchronized (SimpleHash.this) {
                zIsEmpty = SimpleHash.this.isEmpty();
            }
            return zIsEmpty;
        }

        @Override // freemarker.template.SimpleHash
        public void put(String key, Object obj) {
            synchronized (SimpleHash.this) {
                SimpleHash.this.put(key, obj);
            }
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModel
        public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel templateModel;
            synchronized (SimpleHash.this) {
                templateModel = SimpleHash.this.get(key);
            }
            return templateModel;
        }

        @Override // freemarker.template.SimpleHash
        public void remove(String key) {
            synchronized (SimpleHash.this) {
                SimpleHash.this.remove(key);
            }
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public int size() {
            int size;
            synchronized (SimpleHash.this) {
                size = SimpleHash.this.size();
            }
            return size;
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel keys() {
            TemplateCollectionModel templateCollectionModelKeys;
            synchronized (SimpleHash.this) {
                templateCollectionModelKeys = SimpleHash.this.keys();
            }
            return templateCollectionModelKeys;
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel values() {
            TemplateCollectionModel templateCollectionModelValues;
            synchronized (SimpleHash.this) {
                templateCollectionModelValues = SimpleHash.this.values();
            }
            return templateCollectionModelValues;
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx2
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
            TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator;
            synchronized (SimpleHash.this) {
                keyValuePairIterator = SimpleHash.this.keyValuePairIterator();
            }
            return keyValuePairIterator;
        }

        @Override // freemarker.template.SimpleHash
        public Map toMap() throws TemplateModelException {
            Map map;
            synchronized (SimpleHash.this) {
                map = SimpleHash.this.toMap();
            }
            return map;
        }
    }
}
