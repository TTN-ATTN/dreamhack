package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazilyGeneratedCollectionModelWithSameSizeSeq.class */
class LazilyGeneratedCollectionModelWithSameSizeSeq extends LazilyGeneratedCollectionModelEx {
    private final TemplateSequenceModel sizeSourceSeq;

    public LazilyGeneratedCollectionModelWithSameSizeSeq(TemplateModelIterator iterator, TemplateSequenceModel sizeSourceSeq) {
        super(iterator, true);
        NullArgumentException.check(sizeSourceSeq);
        this.sizeSourceSeq = sizeSourceSeq;
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return this.sizeSourceSeq.size();
    }

    @Override // freemarker.template.TemplateCollectionModelEx
    public boolean isEmpty() throws TemplateModelException {
        return this.sizeSourceSeq.size() == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.LazilyGeneratedCollectionModel
    public LazilyGeneratedCollectionModelWithSameSizeSeq withIsSequenceFromFalseToTrue() {
        return this;
    }
}
