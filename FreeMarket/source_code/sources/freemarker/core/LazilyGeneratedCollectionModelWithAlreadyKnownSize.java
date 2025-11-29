package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazilyGeneratedCollectionModelWithAlreadyKnownSize.class */
final class LazilyGeneratedCollectionModelWithAlreadyKnownSize extends LazilyGeneratedCollectionModelEx {
    private final int size;

    LazilyGeneratedCollectionModelWithAlreadyKnownSize(TemplateModelIterator iterator, int size, boolean sequence) {
        super(iterator, sequence);
        this.size = size;
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return this.size;
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override // freemarker.core.LazilyGeneratedCollectionModel
    protected LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue() {
        return new LazilyGeneratedCollectionModelWithAlreadyKnownSize(getIterator(), this.size, true);
    }
}
