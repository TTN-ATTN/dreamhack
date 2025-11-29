package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;
import java.util.ArrayList;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CollectionAndSequence.class */
public final class CollectionAndSequence implements TemplateCollectionModel, TemplateSequenceModel, Serializable {
    private TemplateCollectionModel collection;
    private TemplateSequenceModel sequence;
    private ArrayList<TemplateModel> data;

    public CollectionAndSequence(TemplateCollectionModel collection) {
        this.collection = collection;
    }

    public CollectionAndSequence(TemplateSequenceModel sequence) {
        this.sequence = sequence;
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() throws TemplateModelException {
        if (this.collection != null) {
            return this.collection.iterator();
        }
        return new SequenceIterator(this.sequence);
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int i) throws TemplateModelException {
        if (this.sequence != null) {
            return this.sequence.get(i);
        }
        initSequence();
        return this.data.get(i);
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        if (this.sequence != null) {
            return this.sequence.size();
        }
        if (this.collection instanceof TemplateCollectionModelEx) {
            return ((TemplateCollectionModelEx) this.collection).size();
        }
        initSequence();
        return this.data.size();
    }

    private void initSequence() throws TemplateModelException {
        if (this.data == null) {
            this.data = new ArrayList<>();
            TemplateModelIterator it = this.collection.iterator();
            while (it.hasNext()) {
                this.data.add(it.next());
            }
        }
    }
}
