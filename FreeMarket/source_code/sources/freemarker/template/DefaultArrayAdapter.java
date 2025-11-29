package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import java.io.Serializable;
import java.lang.reflect.Array;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter.class */
public abstract class DefaultArrayAdapter extends WrappingTemplateModel implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, Serializable {
    public static DefaultArrayAdapter adapt(Object array, ObjectWrapperAndUnwrapper wrapper) {
        Class componentType = array.getClass().getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("Not an array");
        }
        if (componentType.isPrimitive()) {
            if (componentType == Integer.TYPE) {
                return new IntArrayAdapter((int[]) array, wrapper);
            }
            if (componentType == Double.TYPE) {
                return new DoubleArrayAdapter((double[]) array, wrapper);
            }
            if (componentType == Long.TYPE) {
                return new LongArrayAdapter((long[]) array, wrapper);
            }
            if (componentType == Boolean.TYPE) {
                return new BooleanArrayAdapter((boolean[]) array, wrapper);
            }
            if (componentType == Float.TYPE) {
                return new FloatArrayAdapter((float[]) array, wrapper);
            }
            if (componentType == Character.TYPE) {
                return new CharArrayAdapter((char[]) array, wrapper);
            }
            if (componentType == Short.TYPE) {
                return new ShortArrayAdapter((short[]) array, wrapper);
            }
            if (componentType == Byte.TYPE) {
                return new ByteArrayAdapter((byte[]) array, wrapper);
            }
            return new GenericPrimitiveArrayAdapter(array, wrapper);
        }
        return new ObjectArrayAdapter((Object[]) array, wrapper);
    }

    private DefaultArrayAdapter(ObjectWrapper wrapper) {
        super(wrapper);
    }

    @Override // freemarker.template.AdapterTemplateModel
    public final Object getAdaptedObject(Class hint) {
        return getWrappedObject();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$ObjectArrayAdapter.class */
    private static class ObjectArrayAdapter extends DefaultArrayAdapter {
        private final Object[] array;

        private ObjectArrayAdapter(Object[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(this.array[index]);
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$ByteArrayAdapter.class */
    private static class ByteArrayAdapter extends DefaultArrayAdapter {
        private final byte[] array;

        private ByteArrayAdapter(byte[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Byte.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$ShortArrayAdapter.class */
    private static class ShortArrayAdapter extends DefaultArrayAdapter {
        private final short[] array;

        private ShortArrayAdapter(short[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Short.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$IntArrayAdapter.class */
    private static class IntArrayAdapter extends DefaultArrayAdapter {
        private final int[] array;

        private IntArrayAdapter(int[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Integer.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$LongArrayAdapter.class */
    private static class LongArrayAdapter extends DefaultArrayAdapter {
        private final long[] array;

        private LongArrayAdapter(long[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Long.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$FloatArrayAdapter.class */
    private static class FloatArrayAdapter extends DefaultArrayAdapter {
        private final float[] array;

        private FloatArrayAdapter(float[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Float.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$DoubleArrayAdapter.class */
    private static class DoubleArrayAdapter extends DefaultArrayAdapter {
        private final double[] array;

        private DoubleArrayAdapter(double[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Double.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$CharArrayAdapter.class */
    private static class CharArrayAdapter extends DefaultArrayAdapter {
        private final char[] array;

        private CharArrayAdapter(char[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Character.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$BooleanArrayAdapter.class */
    private static class BooleanArrayAdapter extends DefaultArrayAdapter {
        private final boolean[] array;

        private BooleanArrayAdapter(boolean[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.array.length) {
                return null;
            }
            return wrap(Boolean.valueOf(this.array[index]));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultArrayAdapter$GenericPrimitiveArrayAdapter.class */
    private static class GenericPrimitiveArrayAdapter extends DefaultArrayAdapter {
        private final Object array;
        private final int length;

        private GenericPrimitiveArrayAdapter(Object array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
            this.length = Array.getLength(array);
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0 || index >= this.length) {
                return null;
            }
            return wrap(Array.get(this.array, index));
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            return this.length;
        }

        @Override // freemarker.ext.util.WrapperTemplateModel
        public Object getWrappedObject() {
            return this.array;
        }
    }
}
