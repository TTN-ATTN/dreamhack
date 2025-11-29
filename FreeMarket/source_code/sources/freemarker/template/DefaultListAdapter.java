package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import freemarker.template.utility.RichObjectWrapper;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultListAdapter.class */
public class DefaultListAdapter extends WrappingTemplateModel implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
    protected final List list;

    public static DefaultListAdapter adapt(List list, RichObjectWrapper wrapper) {
        return list instanceof AbstractSequentialList ? new DefaultListAdapterWithCollectionSupport(list, wrapper) : new DefaultListAdapter(list, wrapper);
    }

    private DefaultListAdapter(List list, RichObjectWrapper wrapper) {
        super(wrapper);
        this.list = list;
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        if (index < 0 || index >= this.list.size()) {
            return null;
        }
        return wrap(this.list.get(index));
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return this.list.size();
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return getWrappedObject();
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.list;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultListAdapter$DefaultListAdapterWithCollectionSupport.class */
    private static class DefaultListAdapterWithCollectionSupport extends DefaultListAdapter implements TemplateCollectionModel {
        private DefaultListAdapterWithCollectionSupport(List list, RichObjectWrapper wrapper) {
            super(list, wrapper);
        }

        @Override // freemarker.template.TemplateCollectionModel
        public TemplateModelIterator iterator() throws TemplateModelException {
            return new IteratorToTemplateModelIteratorAdapter(this.list.iterator(), getObjectWrapper());
        }
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(this.list);
    }
}
