package freemarker.core;

import freemarker.template.TemplateModelIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazilyGeneratedCollectionModelWithUnknownSize.class */
final class LazilyGeneratedCollectionModelWithUnknownSize extends LazilyGeneratedCollectionModel {
    public LazilyGeneratedCollectionModelWithUnknownSize(TemplateModelIterator iterator, boolean sequence) {
        super(iterator, sequence);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.core.LazilyGeneratedCollectionModel
    public LazilyGeneratedCollectionModelWithUnknownSize withIsSequenceFromFalseToTrue() {
        return new LazilyGeneratedCollectionModelWithUnknownSize(getIterator(), true);
    }
}
