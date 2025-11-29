package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonNumberModel.class */
public class JythonNumberModel extends JythonModel implements TemplateNumberModel {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.jython.JythonNumberModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new JythonNumberModel((PyObject) object, (JythonWrapper) wrapper);
        }
    };

    public JythonNumberModel(PyObject object, JythonWrapper wrapper) {
        super(object, wrapper);
    }

    @Override // freemarker.template.TemplateNumberModel
    public Number getAsNumber() throws TemplateModelException {
        try {
            Object value = this.object.__tojava__(Number.class);
            if (value == null || value == Py.NoConversion) {
                return Double.valueOf(this.object.__float__().getValue());
            }
            return (Number) value;
        } catch (PyException e) {
            throw new TemplateModelException((Exception) e);
        }
    }
}
