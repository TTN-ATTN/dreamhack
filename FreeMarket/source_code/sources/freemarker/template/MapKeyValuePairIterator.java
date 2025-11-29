package freemarker.template;

import freemarker.template.TemplateHashModelEx2;
import java.util.Iterator;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/MapKeyValuePairIterator.class */
public class MapKeyValuePairIterator implements TemplateHashModelEx2.KeyValuePairIterator {
    private final Iterator<Map.Entry<?, ?>> entrySetIterator;
    private final ObjectWrapper objectWrapper;

    public <K, V> MapKeyValuePairIterator(Map<?, ?> map, ObjectWrapper objectWrapper) {
        this.entrySetIterator = map.entrySet().iterator();
        this.objectWrapper = objectWrapper;
    }

    @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
    public boolean hasNext() {
        return this.entrySetIterator.hasNext();
    }

    @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
    public TemplateHashModelEx2.KeyValuePair next() {
        final Map.Entry<?, ?> entry = this.entrySetIterator.next();
        return new TemplateHashModelEx2.KeyValuePair() { // from class: freemarker.template.MapKeyValuePairIterator.1
            @Override // freemarker.template.TemplateHashModelEx2.KeyValuePair
            public TemplateModel getKey() throws TemplateModelException {
                return MapKeyValuePairIterator.this.wrap(entry.getKey());
            }

            @Override // freemarker.template.TemplateHashModelEx2.KeyValuePair
            public TemplateModel getValue() throws TemplateModelException {
                return MapKeyValuePairIterator.this.wrap(entry.getValue());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        return obj instanceof TemplateModel ? (TemplateModel) obj : this.objectWrapper.wrap(obj);
    }
}
