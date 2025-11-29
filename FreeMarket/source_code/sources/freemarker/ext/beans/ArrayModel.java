package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.lang.reflect.Array;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ArrayModel.class */
public class ArrayModel extends BeanModel implements TemplateCollectionModel, TemplateSequenceModel {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.ArrayModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new ArrayModel(object, (BeansWrapper) wrapper);
        }
    };
    private final int length;

    public ArrayModel(Object array, BeansWrapper wrapper) {
        super(array, wrapper);
        Class clazz = array.getClass();
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("Object is not an array, it's " + array.getClass().getName());
        }
        this.length = Array.getLength(array);
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() {
        return new Iterator();
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            return wrap(Array.get(this.object, index));
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ArrayModel$Iterator.class */
    private class Iterator implements TemplateSequenceModel, TemplateModelIterator {
        private int position;

        private Iterator() {
            this.position = 0;
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() {
            return this.position < ArrayModel.this.length;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            return ArrayModel.this.get(index);
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            if (this.position >= ArrayModel.this.length) {
                return null;
            }
            int i = this.position;
            this.position = i + 1;
            return get(i);
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() {
            return ArrayModel.this.size();
        }
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModelEx
    public int size() {
        return this.length;
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return this.length == 0;
    }
}
