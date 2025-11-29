package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RightUnboundedRangeModel.class */
abstract class RightUnboundedRangeModel extends RangeModel {
    RightUnboundedRangeModel(int begin) {
        super(begin);
    }

    @Override // freemarker.core.RangeModel
    final int getStep() {
        return 1;
    }

    @Override // freemarker.core.RangeModel
    final boolean isRightUnbounded() {
        return true;
    }

    @Override // freemarker.core.RangeModel
    final boolean isRightAdaptive() {
        return true;
    }

    @Override // freemarker.core.RangeModel
    final boolean isAffectedByStringSlicingBug() {
        return false;
    }
}
