package freemarker.template;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleCollection.class */
public class SimpleCollection extends WrappingTemplateModel implements TemplateCollectionModel, Serializable {
    private boolean iteratorOwned;
    private final Iterator iterator;
    private final Iterable iterable;

    @Deprecated
    public SimpleCollection(Iterator iterator) {
        this.iterator = iterator;
        this.iterable = null;
    }

    @Deprecated
    public SimpleCollection(Iterable iterable) {
        this.iterable = iterable;
        this.iterator = null;
    }

    @Deprecated
    public SimpleCollection(Collection collection) {
        this((Iterable) collection);
    }

    public SimpleCollection(Collection collection, ObjectWrapper wrapper) {
        this((Iterable) collection, wrapper);
    }

    public SimpleCollection(Iterator iterator, ObjectWrapper wrapper) {
        super(wrapper);
        this.iterator = iterator;
        this.iterable = null;
    }

    public SimpleCollection(Iterable iterable, ObjectWrapper wrapper) {
        super(wrapper);
        this.iterable = iterable;
        this.iterator = null;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() {
        return this.iterator != null ? new SimpleTemplateModelIterator(this.iterator, false) : new SimpleTemplateModelIterator(this.iterable.iterator(), true);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleCollection$SimpleTemplateModelIterator.class */
    private class SimpleTemplateModelIterator implements TemplateModelIterator {
        private final Iterator iterator;
        private boolean iteratorOwnedByMe;

        SimpleTemplateModelIterator(Iterator iterator, boolean iteratorOwnedByMe) {
            this.iterator = iterator;
            this.iteratorOwnedByMe = iteratorOwnedByMe;
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                synchronized (SimpleCollection.this) {
                    checkIteratorOwned();
                    SimpleCollection.this.iteratorOwned = true;
                    this.iteratorOwnedByMe = true;
                }
            }
            if (!this.iterator.hasNext()) {
                throw new TemplateModelException("The collection has no more items.");
            }
            Object value = this.iterator.next();
            return value instanceof TemplateModel ? (TemplateModel) value : SimpleCollection.this.wrap(value);
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                synchronized (SimpleCollection.this) {
                    checkIteratorOwned();
                }
            }
            return this.iterator.hasNext();
        }

        private void checkIteratorOwned() throws TemplateModelException {
            if (SimpleCollection.this.iteratorOwned) {
                throw new TemplateModelException("This collection value wraps a java.util.Iterator, thus it can be listed only once.");
            }
        }
    }
}
