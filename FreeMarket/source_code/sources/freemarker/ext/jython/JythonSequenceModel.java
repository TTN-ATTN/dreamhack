package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import org.python.core.PyException;
import org.python.core.PyObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonSequenceModel.class */
public class JythonSequenceModel extends JythonModel implements TemplateSequenceModel, TemplateCollectionModel {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.jython.JythonSequenceModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonSequenceModel((PyObject) object, (JythonWrapper) wrapper);
        }
    };

    public JythonSequenceModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override // freemarker.template.TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            return this.wrapper.wrap(this.object.__finditem__(index));
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
    public int size() throws TemplateModelException {
        try {
            return this.object.__len__();
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    @Override // freemarker.template.TemplateCollectionModel
    public TemplateModelIterator iterator() {
        return new TemplateModelIterator() { // from class: freemarker.ext.jython.JythonSequenceModel.2
            int i = 0;

            @Override // freemarker.template.TemplateModelIterator
            public boolean hasNext() throws TemplateModelException {
                return this.i < JythonSequenceModel.this.size();
            }

            @Override // freemarker.template.TemplateModelIterator
            public TemplateModel next() throws TemplateModelException {
                JythonSequenceModel jythonSequenceModel = JythonSequenceModel.this;
                int i = this.i;
                this.i = i + 1;
                return jythonSequenceModel.get(i);
            }
        };
    }
}
