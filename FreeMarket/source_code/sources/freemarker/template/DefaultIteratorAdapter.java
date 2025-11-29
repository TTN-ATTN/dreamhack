package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultIteratorAdapter.class */
public class DefaultIteratorAdapter extends WrappingTemplateModel implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "We hope it's Seralizable")
    private final Iterator iterator;
    private boolean iteratorOwnedBySomeone;

    public static DefaultIteratorAdapter adapt(Iterator iterator, ObjectWrapper wrapper) {
        return new DefaultIteratorAdapter(iterator, wrapper);
    }

    private DefaultIteratorAdapter(Iterator iterator, ObjectWrapper wrapper) {
        super(wrapper);
        this.iterator = iterator;
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.iterator;
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return getWrappedObject();
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new SimpleTemplateModelIterator();
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(this.iterator);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultIteratorAdapter$SimpleTemplateModelIterator.class */
    private class SimpleTemplateModelIterator implements TemplateModelIterator {
        private boolean iteratorOwnedByMe;

        private SimpleTemplateModelIterator() {
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                checkNotOwner();
                DefaultIteratorAdapter.this.iteratorOwnedBySomeone = true;
                this.iteratorOwnedByMe = true;
            }
            if (DefaultIteratorAdapter.this.iterator.hasNext()) {
                Object value = DefaultIteratorAdapter.this.iterator.next();
                return value instanceof TemplateModel ? (TemplateModel) value : DefaultIteratorAdapter.this.wrap(value);
            }
            throw new TemplateModelException("The collection has no more items.");
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                checkNotOwner();
            }
            return DefaultIteratorAdapter.this.iterator.hasNext();
        }

        private void checkNotOwner() throws TemplateModelException {
            if (DefaultIteratorAdapter.this.iteratorOwnedBySomeone) {
                throw new TemplateModelException("This collection value wraps a java.util.Iterator, thus it can be listed only once.");
            }
        }
    }
}
