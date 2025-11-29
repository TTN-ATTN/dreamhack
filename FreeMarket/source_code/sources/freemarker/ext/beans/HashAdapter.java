package freemarker.ext.beans;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/HashAdapter.class */
public class HashAdapter extends AbstractMap implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateHashModel model;
    private Set entrySet;

    HashAdapter(TemplateHashModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateModelAdapter
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        try {
            return this.model.isEmpty();
        } catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        try {
            return getModelEx().size();
        } catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object get(Object key) {
        try {
            return this.wrapper.unwrap(this.model.get(String.valueOf(key)));
        } catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        if (get(key) != null) {
            return true;
        }
        return super.containsKey(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set entrySet() {
        if (this.entrySet != null) {
            return this.entrySet;
        }
        AnonymousClass1 anonymousClass1 = new AnonymousClass1();
        this.entrySet = anonymousClass1;
        return anonymousClass1;
    }

    /* renamed from: freemarker.ext.beans.HashAdapter$1, reason: invalid class name */
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/HashAdapter$1.class */
    class AnonymousClass1 extends AbstractSet {
        AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator iterator() {
            try {
                final TemplateModelIterator i = HashAdapter.this.getModelEx().keys().iterator();
                return new Iterator() { // from class: freemarker.ext.beans.HashAdapter.1.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        try {
                            return i.hasNext();
                        } catch (TemplateModelException e) {
                            throw new UndeclaredThrowableException(e);
                        }
                    }

                    @Override // java.util.Iterator
                    public Object next() {
                        try {
                            final Object key = HashAdapter.this.wrapper.unwrap(i.next());
                            return new Map.Entry() { // from class: freemarker.ext.beans.HashAdapter.1.1.1
                                @Override // java.util.Map.Entry
                                public Object getKey() {
                                    return key;
                                }

                                @Override // java.util.Map.Entry
                                public Object getValue() {
                                    return HashAdapter.this.get(key);
                                }

                                @Override // java.util.Map.Entry
                                public Object setValue(Object value) {
                                    throw new UnsupportedOperationException();
                                }

                                @Override // java.util.Map.Entry
                                public boolean equals(Object o) {
                                    if (!(o instanceof Map.Entry)) {
                                        return false;
                                    }
                                    Map.Entry e = (Map.Entry) o;
                                    Object k1 = getKey();
                                    Object k2 = e.getKey();
                                    if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                                        Object v1 = getValue();
                                        Object v2 = e.getValue();
                                        if (v1 == v2) {
                                            return true;
                                        }
                                        if (v1 != null && v1.equals(v2)) {
                                            return true;
                                        }
                                        return false;
                                    }
                                    return false;
                                }

                                @Override // java.util.Map.Entry
                                public int hashCode() {
                                    Object value = getValue();
                                    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
                                }
                            };
                        } catch (TemplateModelException e) {
                            throw new UndeclaredThrowableException(e);
                        }
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            } catch (TemplateModelException e) {
                throw new UndeclaredThrowableException(e);
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            try {
                return HashAdapter.this.getModelEx().size();
            } catch (TemplateModelException e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TemplateHashModelEx getModelEx() {
        if (this.model instanceof TemplateHashModelEx) {
            return (TemplateHashModelEx) this.model;
        }
        throw new UnsupportedOperationException("Operation supported only on TemplateHashModelEx. " + this.model.getClass().getName() + " does not implement it though.");
    }
}
