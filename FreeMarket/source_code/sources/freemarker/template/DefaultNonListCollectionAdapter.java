package freemarker.template;

import freemarker.core._DelayedShortClassName;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Collection;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultNonListCollectionAdapter.class */
public class DefaultNonListCollectionAdapter extends WrappingTemplateModel implements TemplateCollectionModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
    private final Collection collection;

    public static DefaultNonListCollectionAdapter adapt(Collection collection, ObjectWrapperWithAPISupport wrapper) {
        return new DefaultNonListCollectionAdapter(collection, wrapper);
    }

    private DefaultNonListCollectionAdapter(Collection collection, ObjectWrapperWithAPISupport wrapper) {
        super(wrapper);
        this.collection = collection;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new IteratorToTemplateModelIteratorAdapter(this.collection.iterator(), getObjectWrapper());
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public int size() {
        return this.collection.size();
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.collection;
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return getWrappedObject();
    }

    public boolean contains(TemplateModel item) throws TemplateModelException {
        Object itemPojo = ((ObjectWrapperAndUnwrapper) getObjectWrapper()).unwrap(item);
        try {
            return this.collection.contains(itemPojo);
        } catch (ClassCastException e) {
            Object[] objArr = new Object[3];
            objArr[0] = "Failed to check if the collection contains the item. Probably the item's Java type, ";
            objArr[1] = itemPojo != null ? new _DelayedShortClassName(itemPojo.getClass()) : "Null";
            objArr[2] = ", doesn't match the type of (some of) the collection items; see cause exception.";
            throw new _TemplateModelException(e, objArr);
        }
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(this.collection);
    }
}
