package freemarker.ext.jython;

import freemarker.ext.util.ModelCache;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.OptimizerUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonWrapper.class */
public class JythonWrapper implements ObjectWrapper {
    private static final Class PYOBJECT_CLASS = PyObject.class;
    public static final JythonWrapper INSTANCE = new JythonWrapper();
    private final ModelCache modelCache = new JythonModelCache(this);
    private boolean attributesShadowItems = true;

    public void setUseCache(boolean useCache) {
        this.modelCache.setUseCache(useCache);
    }

    public synchronized void setAttributesShadowItems(boolean attributesShadowItems) {
        this.attributesShadowItems = attributesShadowItems;
    }

    boolean isAttributesShadowItems() {
        return this.attributesShadowItems;
    }

    @Override // freemarker.template.ObjectWrapper
    public TemplateModel wrap(Object obj) {
        if (obj == null) {
            return null;
        }
        return this.modelCache.getInstance(obj);
    }

    public PyObject unwrap(TemplateModel model) throws TemplateModelException {
        if (model instanceof AdapterTemplateModel) {
            return Py.java2py(((AdapterTemplateModel) model).getAdaptedObject(PYOBJECT_CLASS));
        }
        if (model instanceof WrapperTemplateModel) {
            return Py.java2py(((WrapperTemplateModel) model).getWrappedObject());
        }
        if (model instanceof TemplateScalarModel) {
            return new PyString(((TemplateScalarModel) model).getAsString());
        }
        if (model instanceof TemplateNumberModel) {
            Number number = ((TemplateNumberModel) model).getAsNumber();
            if (number instanceof BigDecimal) {
                number = OptimizerUtil.optimizeNumberRepresentation(number);
            }
            if (number instanceof BigInteger) {
                return new PyLong((BigInteger) number);
            }
            return Py.java2py(number);
        }
        return new TemplateModelToJythonAdapter(model);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonWrapper$TemplateModelToJythonAdapter.class */
    private class TemplateModelToJythonAdapter extends PyObject implements TemplateModelAdapter {
        private final TemplateModel model;

        TemplateModelToJythonAdapter(TemplateModel model) {
            this.model = model;
        }

        @Override // freemarker.template.TemplateModelAdapter
        public TemplateModel getTemplateModel() {
            return this.model;
        }

        public PyObject __finditem__(PyObject key) {
            if (key instanceof PyInteger) {
                return __finditem__(((PyInteger) key).getValue());
            }
            return __finditem__(key.toString());
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.python.core.PyException */
        public PyObject __finditem__(String key) throws PyException {
            if (this.model instanceof TemplateHashModel) {
                try {
                    return JythonWrapper.this.unwrap(((TemplateHashModel) this.model).get(key));
                } catch (TemplateModelException e) {
                    throw Py.JavaError(e);
                }
            }
            throw Py.TypeError("item lookup on non-hash model (" + getModelClass() + ")");
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.python.core.PyException */
        public PyObject __finditem__(int index) throws PyException {
            if (this.model instanceof TemplateSequenceModel) {
                try {
                    return JythonWrapper.this.unwrap(((TemplateSequenceModel) this.model).get(index));
                } catch (TemplateModelException e) {
                    throw Py.JavaError(e);
                }
            }
            throw Py.TypeError("item lookup on non-sequence model (" + getModelClass() + ")");
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.python.core.PyException */
        public PyObject __call__(PyObject[] args, String[] keywords) throws PyException {
            Object string;
            if (this.model instanceof TemplateMethodModel) {
                boolean isEx = this.model instanceof TemplateMethodModelEx;
                List list = new ArrayList(args.length);
                for (int i = 0; i < args.length; i++) {
                    try {
                        if (isEx) {
                            string = JythonWrapper.this.wrap(args[i]);
                        } else {
                            string = args[i] == null ? null : args[i].toString();
                        }
                        list.add(string);
                    } catch (TemplateModelException e) {
                        throw Py.JavaError(e);
                    }
                }
                return JythonWrapper.this.unwrap((TemplateModel) ((TemplateMethodModelEx) this.model).exec(list));
            }
            throw Py.TypeError("call of non-method model (" + getModelClass() + ")");
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.python.core.PyException */
        public int __len__() throws PyException {
            try {
                if (this.model instanceof TemplateSequenceModel) {
                    return ((TemplateSequenceModel) this.model).size();
                }
                if (this.model instanceof TemplateHashModelEx) {
                    return ((TemplateHashModelEx) this.model).size();
                }
                return 0;
            } catch (TemplateModelException e) {
                throw Py.JavaError(e);
            }
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.python.core.PyException */
        public boolean __nonzero__() throws PyException {
            try {
                if (this.model instanceof TemplateBooleanModel) {
                    return ((TemplateBooleanModel) this.model).getAsBoolean();
                }
                return this.model instanceof TemplateSequenceModel ? ((TemplateSequenceModel) this.model).size() > 0 : (this.model instanceof TemplateHashModel) && !((TemplateHashModelEx) this.model).isEmpty();
            } catch (TemplateModelException e) {
                throw Py.JavaError(e);
            }
        }

        private String getModelClass() {
            return this.model == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : this.model.getClass().getName();
        }
    }
}
