package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Enumeration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultEnumerationAdapter.class */
public class DefaultEnumerationAdapter extends WrappingTemplateModel implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "We hope it's Seralizable")
    private final Enumeration<?> enumeration;
    private boolean enumerationOwnedBySomeone;

    public static DefaultEnumerationAdapter adapt(Enumeration<?> enumeration, ObjectWrapper wrapper) {
        return new DefaultEnumerationAdapter(enumeration, wrapper);
    }

    private DefaultEnumerationAdapter(Enumeration<?> enumeration, ObjectWrapper wrapper) {
        super(wrapper);
        this.enumeration = enumeration;
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.enumeration;
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class<?> hint) {
        return getWrappedObject();
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new SimpleTemplateModelIterator();
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(this.enumeration);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultEnumerationAdapter$SimpleTemplateModelIterator.class */
    private class SimpleTemplateModelIterator implements TemplateModelIterator {
        private boolean enumerationOwnedByMe;

        private SimpleTemplateModelIterator() {
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            if (!this.enumerationOwnedByMe) {
                checkNotOwner();
                DefaultEnumerationAdapter.this.enumerationOwnedBySomeone = true;
                this.enumerationOwnedByMe = true;
            }
            if (DefaultEnumerationAdapter.this.enumeration.hasMoreElements()) {
                Object value = DefaultEnumerationAdapter.this.enumeration.nextElement();
                return value instanceof TemplateModel ? (TemplateModel) value : DefaultEnumerationAdapter.this.wrap(value);
            }
            throw new TemplateModelException("The collection has no more items.");
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() throws TemplateModelException {
            if (!this.enumerationOwnedByMe) {
                checkNotOwner();
            }
            return DefaultEnumerationAdapter.this.enumeration.hasMoreElements();
        }

        private void checkNotOwner() throws TemplateModelException {
            if (DefaultEnumerationAdapter.this.enumerationOwnedBySomeone) {
                throw new TemplateModelException("This collection value wraps a java.util.Enumeration, thus it can be listed only once.");
            }
        }
    }
}
