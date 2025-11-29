package freemarker.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/IteratorToTemplateModelIteratorAdapter.class */
class IteratorToTemplateModelIteratorAdapter implements TemplateModelIterator {
    private final Iterator<?> it;
    private final ObjectWrapper wrapper;

    IteratorToTemplateModelIteratorAdapter(Iterator<?> it, ObjectWrapper wrapper) {
        this.it = it;
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateModelIterator
    public TemplateModel next() throws TemplateModelException {
        try {
            return this.wrapper.wrap(this.it.next());
        } catch (NoSuchElementException e) {
            throw new TemplateModelException("The collection has no more items.", (Exception) e);
        }
    }

    @Override // freemarker.template.TemplateModelIterator
    public boolean hasNext() throws TemplateModelException {
        return this.it.hasNext();
    }
}
