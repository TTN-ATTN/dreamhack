package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LazySequenceIterator.class */
class LazySequenceIterator implements TemplateModelIterator {
    private final TemplateSequenceModel sequence;
    private Integer size;
    private int index = 0;

    LazySequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
        this.sequence = sequence;
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
        if (this.size == null) {
            try {
                this.size = Integer.valueOf(this.sequence.size());
            } catch (TemplateModelException e) {
                throw new RuntimeException("Error when getting sequence size", e);
            }
        }
        return this.index < this.size.intValue();
    }
}
