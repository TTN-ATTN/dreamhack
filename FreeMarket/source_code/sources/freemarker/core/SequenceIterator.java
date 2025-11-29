package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/SequenceIterator.class */
class SequenceIterator implements TemplateModelIterator {
    private final TemplateSequenceModel sequence;
    private final int size;
    private int index = 0;

    SequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
        this.sequence = sequence;
        this.size = sequence.size();
    }

    @Override // freemarker.template.TemplateModelIterator
    public TemplateModel next() throws TemplateModelException {
        TemplateSequenceModel templateSequenceModel = this.sequence;
        int i = this.index;
        this.index = i + 1;
        return templateSequenceModel.get(i);
    }

    @Override // freemarker.template.TemplateModelIterator
    public boolean hasNext() {
        return this.index < this.size;
    }
}
