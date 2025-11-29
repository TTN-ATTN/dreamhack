package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractCollection;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/CollectionAdapter.class */
class CollectionAdapter extends AbstractCollection implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateCollectionModel model;

    CollectionAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateModelAdapter
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator iterator() {
        try {
            return new Iterator() { // from class: freemarker.ext.beans.CollectionAdapter.1
                final TemplateModelIterator i;

                {
                    this.i = CollectionAdapter.this.model.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    try {
                        return this.i.hasNext();
                    } catch (TemplateModelException e) {
                        throw new UndeclaredThrowableException(e);
                    }
                }

                @Override // java.util.Iterator
                public Object next() {
                    try {
                        return CollectionAdapter.this.wrapper.unwrap(this.i.next());
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
}
