package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazyCollectionTemplateModelIterator.class */
class LazyCollectionTemplateModelIterator implements TemplateModelIterator {
    private final TemplateCollectionModel templateCollectionModel;
    private TemplateModelIterator iterator;

    public LazyCollectionTemplateModelIterator(TemplateCollectionModel templateCollectionModel) {
        this.templateCollectionModel = templateCollectionModel;
    }

    @Override // freemarker.template.TemplateModelIterator
    public TemplateModel next() throws TemplateModelException {
        ensureIteratorInitialized();
        return this.iterator.next();
    }

    @Override // freemarker.template.TemplateModelIterator
    public boolean hasNext() throws TemplateModelException {
        ensureIteratorInitialized();
        return this.iterator.hasNext();
    }

    private void ensureIteratorInitialized() throws TemplateModelException {
        if (this.iterator == null) {
            this.iterator = this.templateCollectionModel.iterator();
        }
    }
}
