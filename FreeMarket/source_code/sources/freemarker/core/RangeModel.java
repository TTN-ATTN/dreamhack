package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RangeModel.class */
abstract class RangeModel implements TemplateSequenceModel, Serializable {
    private final int begin;

    abstract int getStep();

    abstract boolean isRightUnbounded();

    abstract boolean isRightAdaptive();

    abstract boolean isAffectedByStringSlicingBug();

    public RangeModel(int begin) {
        this.begin = begin;
    }

    final int getBegining() {
        return this.begin;
    }

    @Override // freemarker.template.TemplateSequenceModel
    public final TemplateModel get(int index) throws TemplateModelException {
        if (index < 0 || index >= size()) {
            throw new _TemplateModelException("Range item index ", Integer.valueOf(index), " is out of bounds.");
        }
        long value = this.begin + (getStep() * index);
        return value <= 2147483647L ? new SimpleNumber((int) value) : new SimpleNumber(value);
    }
}
