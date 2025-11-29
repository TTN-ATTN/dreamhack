package freemarker.core;

import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazilyGeneratedCollectionModelWithSameSizeCollEx.class */
class LazilyGeneratedCollectionModelWithSameSizeCollEx extends LazilyGeneratedCollectionModelEx {
    private final TemplateCollectionModelEx sizeSourceCollEx;

    public LazilyGeneratedCollectionModelWithSameSizeCollEx(TemplateModelIterator iterator, TemplateCollectionModelEx sizeSourceCollEx, boolean sequenceSourced) {
        super(iterator, sequenceSourced);
        NullArgumentException.check(sizeSourceCollEx);
        this.sizeSourceCollEx = sizeSourceCollEx;
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return this.sizeSourceCollEx.size();
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public boolean isEmpty() throws TemplateModelException {
        return this.sizeSourceCollEx.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.LazilyGeneratedCollectionModel
    public LazilyGeneratedCollectionModelWithSameSizeCollEx withIsSequenceFromFalseToTrue() {
        return new LazilyGeneratedCollectionModelWithSameSizeCollEx(getIterator(), this.sizeSourceCollEx, true);
    }
}
