package freemarker.core;

import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonListableRightUnboundedRangeModel.class */
final class NonListableRightUnboundedRangeModel extends RightUnboundedRangeModel {
    NonListableRightUnboundedRangeModel(int begin) {
        super(begin);
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        return 0;
    }
}
