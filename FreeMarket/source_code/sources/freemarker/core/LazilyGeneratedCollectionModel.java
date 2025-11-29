package freemarker.core;

import freemarker.template.TemplateModelIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazilyGeneratedCollectionModel.class */
abstract class LazilyGeneratedCollectionModel extends SingleIterationCollectionModel {
    private final boolean sequence;

    protected abstract LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue();

    protected LazilyGeneratedCollectionModel(TemplateModelIterator iterator, boolean sequence) {
        super(iterator);
        this.sequence = sequence;
    }

    final boolean isSequence() {
        return this.sequence;
    }

    final LazilyGeneratedCollectionModel withIsSequenceTrue() {
        return isSequence() ? this : withIsSequenceFromFalseToTrue();
    }
}
