package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BoundedRangeModel.class */
final class BoundedRangeModel extends RangeModel {
    private final int step;
    private final int size;
    private final boolean rightAdaptive;
    private final boolean affectedByStringSlicingBug;

    BoundedRangeModel(int begin, int end, boolean inclusiveEnd, boolean rightAdaptive) {
        super(begin);
        this.step = begin <= end ? 1 : -1;
        this.size = Math.abs(end - begin) + (inclusiveEnd ? 1 : 0);
        this.rightAdaptive = rightAdaptive;
        this.affectedByStringSlicingBug = inclusiveEnd;
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() {
        return this.size;
    }

    @Override // freemarker.core.RangeModel
    int getStep() {
        return this.step;
    }

    @Override // freemarker.core.RangeModel
    boolean isRightUnbounded() {
        return false;
    }

    @Override // freemarker.core.RangeModel
    boolean isRightAdaptive() {
        return this.rightAdaptive;
    }

    @Override // freemarker.core.RangeModel
    boolean isAffectedByStringSlicingBug() {
        return this.affectedByStringSlicingBug;
    }
}
