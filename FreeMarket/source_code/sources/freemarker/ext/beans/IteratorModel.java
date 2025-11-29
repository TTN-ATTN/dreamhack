package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/IteratorModel.class */
public class IteratorModel extends BeanModel implements TemplateModelIterator, TemplateCollectionModel {
    private boolean accessed;

    public IteratorModel(Iterator iterator, BeansWrapper wrapper) {
        super(iterator, wrapper);
        this.accessed = false;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        synchronized (this) {
            if (this.accessed) {
                throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
            }
            this.accessed = true;
        }
        return this;
    }

    @Override // freemarker.template.TemplateModelIterator
    public boolean hasNext() {
        return ((Iterator) this.object).hasNext();
    }

    @Override // freemarker.template.TemplateModelIterator
    public TemplateModel next() throws TemplateModelException {
        try {
            return wrap(((Iterator) this.object).next());
        } catch (NoSuchElementException e) {
            throw new TemplateModelException("No more elements in the iterator.", (Exception) e);
        }
    }

    public boolean getAsBoolean() {
        return hasNext();
    }
}
