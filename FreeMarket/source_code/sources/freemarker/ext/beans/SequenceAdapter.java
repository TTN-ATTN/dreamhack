package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractList;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/SequenceAdapter.class */
class SequenceAdapter extends AbstractList implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateSequenceModel model;

    SequenceAdapter(TemplateSequenceModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateModelAdapter
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        try {
            return this.model.size();
        } catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public Object get(int index) {
        try {
            return this.wrapper.unwrap(this.model.get(index));
        } catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    public TemplateSequenceModel getTemplateSequenceModel() {
        return this.model;
    }
}
