package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/SingleIterationCollectionModel.class */
class SingleIterationCollectionModel implements TemplateCollectionModel {
    private TemplateModelIterator iterator;

    SingleIterationCollectionModel(TemplateModelIterator iterator) {
        NullArgumentException.check(iterator);
        this.iterator = iterator;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        if (this.iterator == null) {
            throw new IllegalStateException("Can't return the iterator again, as this TemplateCollectionModel can only be iterated once.");
        }
        TemplateModelIterator result = this.iterator;
        this.iterator = null;
        return result;
    }

    protected TemplateModelIterator getIterator() {
        return this.iterator;
    }
}
