package freemarker.template.utility;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;
import java.util.NoSuchElementException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants.class */
public class Constants {
    public static final TemplateBooleanModel TRUE = TemplateBooleanModel.TRUE;
    public static final TemplateBooleanModel FALSE = TemplateBooleanModel.FALSE;
    public static final TemplateScalarModel EMPTY_STRING = (TemplateScalarModel) TemplateScalarModel.EMPTY_STRING;
    public static final TemplateNumberModel ZERO = new SimpleNumber(0);
    public static final TemplateNumberModel ONE = new SimpleNumber(1);
    public static final TemplateNumberModel MINUS_ONE = new SimpleNumber(-1);
    public static final TemplateModelIterator EMPTY_ITERATOR = new EmptyIteratorModel();
    public static final TemplateCollectionModel EMPTY_COLLECTION = new EmptyCollectionModel();
    public static final TemplateSequenceModel EMPTY_SEQUENCE = new EmptySequenceModel();
    public static final TemplateHashModelEx2 EMPTY_HASH_EX2 = new EmptyHashModel();
    public static final TemplateHashModelEx EMPTY_HASH = EMPTY_HASH_EX2;
    public static final TemplateHashModelEx2.KeyValuePairIterator EMPTY_KEY_VALUE_PAIR_ITERATOR = new EmptyKeyValuePairIterator();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants$EmptyIteratorModel.class */
    private static class EmptyIteratorModel implements TemplateModelIterator, Serializable {
        private EmptyIteratorModel() {
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            throw new TemplateModelException("The collection has no more elements.");
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() throws TemplateModelException {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants$EmptyCollectionModel.class */
    private static class EmptyCollectionModel implements TemplateCollectionModel, Serializable {
        private EmptyCollectionModel() {
        }

        @Override // freemarker.template.TemplateCollectionModel
        public TemplateModelIterator iterator() throws TemplateModelException {
            return Constants.EMPTY_ITERATOR;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants$EmptySequenceModel.class */
    private static class EmptySequenceModel implements TemplateSequenceModel, Serializable {
        private EmptySequenceModel() {
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            return null;
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return 0;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants$EmptyHashModel.class */
    private static class EmptyHashModel implements TemplateHashModelEx2, Serializable {
        private EmptyHashModel() {
        }

        @Override // freemarker.template.TemplateHashModelEx
        public int size() throws TemplateModelException {
            return 0;
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel keys() throws TemplateModelException {
            return Constants.EMPTY_COLLECTION;
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel values() throws TemplateModelException {
            return Constants.EMPTY_COLLECTION;
        }

        @Override // freemarker.template.TemplateHashModel
        public TemplateModel get(String key) throws TemplateModelException {
            return null;
        }

        @Override // freemarker.template.TemplateHashModel
        public boolean isEmpty() throws TemplateModelException {
            return true;
        }

        @Override // freemarker.template.TemplateHashModelEx2
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
            return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Constants$EmptyKeyValuePairIterator.class */
    private static class EmptyKeyValuePairIterator implements TemplateHashModelEx2.KeyValuePairIterator {
        private EmptyKeyValuePairIterator() {
        }

        @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
        public boolean hasNext() throws TemplateModelException {
            return false;
        }

        @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
        public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
            throw new NoSuchElementException("Can't retrieve element from empty key-value pair iterator.");
        }
    }
}
