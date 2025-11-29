package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.python.core.PyException;
import org.python.core.PyObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonHashModel.class */
public class JythonHashModel extends JythonModel implements TemplateHashModelEx {
    private static final String KEYS = "keys";
    private static final String KEYSET = "keySet";
    private static final String VALUES = "values";
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.jython.JythonHashModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonHashModel((PyObject) object, (JythonWrapper) wrapper);
        }
    };

    public JythonHashModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() throws TemplateModelException {
        try {
            return this.object.__len__();
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() throws TemplateModelException {
        try {
            PyObject method = this.object.__findattr__(KEYS);
            if (method == null) {
                method = this.object.__findattr__(KEYSET);
            }
            if (method != null) {
                return (TemplateCollectionModel) this.wrapper.wrap(method.__call__());
            }
            throw new TemplateModelException("'?keys' is not supported as there is no 'keys' nor 'keySet' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() throws TemplateModelException {
        try {
            PyObject method = this.object.__findattr__(VALUES);
            if (method != null) {
                return (TemplateCollectionModel) this.wrapper.wrap(method.__call__());
            }
            throw new TemplateModelException("'?values' is not supported as there is no 'values' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }
}
