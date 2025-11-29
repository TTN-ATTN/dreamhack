package freemarker.ext.beans;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ResourceBundleModel.class */
public class ResourceBundleModel extends BeanModel implements TemplateMethodModelEx {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.ResourceBundleModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new ResourceBundleModel((ResourceBundle) object, (BeansWrapper) wrapper);
        }
    };
    private Hashtable formats;

    public ResourceBundleModel(ResourceBundle bundle, BeansWrapper wrapper) {
        super(bundle, wrapper);
        this.formats = null;
    }

    @Override // freemarker.ext.beans.BeanModel
    protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
        try {
            return wrap(((ResourceBundle) this.object).getObject(key));
        } catch (MissingResourceException e) {
            throw new _TemplateModelException(e, "No ", new _DelayedJQuote(key), " key in the ResourceBundle. Note that conforming to the ResourceBundle Java API, this is an error and not just a missing sub-variable (a null).");
        }
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return !((ResourceBundle) this.object).getKeys().hasMoreElements() && super.isEmpty();
    }

    @Override // freemarker.ext.beans.BeanModel, freemarker.template.TemplateHashModelEx
    public int size() {
        return keySet().size();
    }

    @Override // freemarker.ext.beans.BeanModel
    protected Set keySet() {
        Set set = super.keySet();
        Enumeration e = ((ResourceBundle) this.object).getKeys();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return set;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() < 1) {
            throw new TemplateModelException("No message key was specified");
        }
        Iterator it = arguments.iterator();
        String key = unwrap((TemplateModel) it.next()).toString();
        try {
            if (!it.hasNext()) {
                return wrap(((ResourceBundle) this.object).getObject(key));
            }
            int args = arguments.size() - 1;
            Object[] params = new Object[args];
            for (int i = 0; i < args; i++) {
                params[i] = unwrap((TemplateModel) it.next());
            }
            return new StringModel(format(key, params), this.wrapper);
        } catch (MissingResourceException e) {
            throw new TemplateModelException("No such key: " + key);
        } catch (Exception e2) {
            throw new TemplateModelException(e2.getMessage());
        }
    }

    public String format(String key, Object[] params) throws MissingResourceException {
        String str;
        if (this.formats == null) {
            this.formats = new Hashtable();
        }
        MessageFormat format = (MessageFormat) this.formats.get(key);
        if (format == null) {
            format = new MessageFormat(((ResourceBundle) this.object).getString(key));
            format.setLocale(getBundle().getLocale());
            this.formats.put(key, format);
        }
        synchronized (format) {
            str = format.format(params);
        }
        return str;
    }

    public ResourceBundle getBundle() {
        return (ResourceBundle) this.object;
    }
}
