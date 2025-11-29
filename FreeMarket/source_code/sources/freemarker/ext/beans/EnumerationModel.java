package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/EnumerationModel.class */
public class EnumerationModel extends BeanModel implements TemplateModelIterator, TemplateCollectionModel {
    private boolean accessed;

    public EnumerationModel(Enumeration enumeration, BeansWrapper wrapper) {
        super(enumeration, wrapper);
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
        return ((Enumeration) this.object).hasMoreElements();
    }

    @Override // freemarker.template.TemplateModelIterator
    public TemplateModel next() throws TemplateModelException {
        try {
            return wrap(((Enumeration) this.object).nextElement());
        } catch (NoSuchElementException e) {
            throw new TemplateModelException("No more elements in the enumeration.");
        }
    }

    public boolean getAsBoolean() {
        return hasNext();
    }
}
