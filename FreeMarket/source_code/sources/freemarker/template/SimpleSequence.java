package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleSequence.class */
public class SimpleSequence extends WrappingTemplateModel implements TemplateSequenceModel, Serializable {
    protected final List list;
    private List unwrappedList;

    @Deprecated
    public SimpleSequence() {
        this((ObjectWrapper) null);
    }

    @Deprecated
    public SimpleSequence(int capacity) {
        this.list = new ArrayList(capacity);
    }

    @Deprecated
    public SimpleSequence(Collection collection) {
        this(collection, (ObjectWrapper) null);
    }

    public SimpleSequence(TemplateCollectionModel tcm) throws TemplateModelException {
        ArrayList alist = new ArrayList();
        TemplateModelIterator it = tcm.iterator();
        while (it.hasNext()) {
            alist.add(it.next());
        }
        alist.trimToSize();
        this.list = alist;
    }

    public SimpleSequence(ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList();
    }

    public SimpleSequence(int capacity, ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList(capacity);
    }

    public SimpleSequence(Collection collection, ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList(collection);
    }

    public void add(Object obj) {
        this.list.add(obj);
        this.unwrappedList = null;
    }

    @Deprecated
    public void add(boolean b) {
        add(b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
    }

    @Deprecated
    public List toList() throws TemplateModelException {
        if (this.unwrappedList == null) {
            Class listClass = this.list.getClass();
            try {
                List result = (List) listClass.newInstance();
                BeansWrapper bw = BeansWrapper.getDefaultInstance();
                for (int i = 0; i < this.list.size(); i++) {
                    Object elem = this.list.get(i);
                    if (elem instanceof TemplateModel) {
                        elem = bw.unwrap((TemplateModel) elem);
                    }
                    result.add(elem);
                }
                this.unwrappedList = result;
            } catch (Exception e) {
                throw new TemplateModelException("Error instantiating an object of type " + listClass.getName(), e);
            }
        }
        return this.unwrappedList;
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            Object value = this.list.get(index);
            if (value instanceof TemplateModel) {
                return (TemplateModel) value;
            }
            TemplateModel tm = wrap(value);
            this.list.set(index, tm);
            return tm;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() {
        return this.list.size();
    }

    public SimpleSequence synchronizedWrapper() {
        return new SynchronizedSequence();
    }

    public String toString() {
        return this.list.toString();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleSequence$SynchronizedSequence.class */
    private class SynchronizedSequence extends SimpleSequence {
        private SynchronizedSequence() {
            super(SimpleSequence.this.getObjectWrapper());
        }

        @Override // freemarker.template.SimpleSequence
        public void add(Object obj) {
            synchronized (SimpleSequence.this) {
                SimpleSequence.this.add(obj);
            }
        }

        @Override // freemarker.template.SimpleSequence, freemarker.template.TemplateSequenceModel
        public TemplateModel get(int i) throws TemplateModelException {
            TemplateModel templateModel;
            synchronized (SimpleSequence.this) {
                templateModel = SimpleSequence.this.get(i);
            }
            return templateModel;
        }

        @Override // freemarker.template.SimpleSequence, freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() {
            int size;
            synchronized (SimpleSequence.this) {
                size = SimpleSequence.this.size();
            }
            return size;
        }

        @Override // freemarker.template.SimpleSequence
        public List toList() throws TemplateModelException {
            List list;
            synchronized (SimpleSequence.this) {
                list = SimpleSequence.this.toList();
            }
            return list;
        }

        @Override // freemarker.template.SimpleSequence
        public SimpleSequence synchronizedWrapper() {
            return this;
        }
    }
}
