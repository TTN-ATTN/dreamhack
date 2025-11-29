package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core.CollectionAndSequence;
import freemarker.core.Macro;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MethodCallAwareTemplateHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeanModel.class */
public class BeanModel implements TemplateHashModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport {
    protected final Object object;
    protected final BeansWrapper wrapper;
    private HashMap<Object, TemplateModel> memberCache;
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    static final TemplateModel UNKNOWN = new SimpleScalar("UNKNOWN");
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.BeanModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new BeanModel(object, (BeansWrapper) wrapper);
        }
    };

    public BeanModel(Object object, BeansWrapper wrapper) {
        this(object, wrapper, true);
    }

    BeanModel(Object object, BeansWrapper wrapper, boolean inrospectNow) {
        this.object = object;
        this.wrapper = wrapper;
        if (inrospectNow && object != null) {
            wrapper.getClassIntrospector().get(object.getClass());
        }
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        try {
            return get(key, false);
        } catch (MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException e) {
            throw new BugException(e);
        }
    }

    protected TemplateModel get(String key, boolean beforeMethodCall) throws Exception {
        Class<?> clazz = this.object.getClass();
        Map<Object, Object> classInfo = this.wrapper.getClassIntrospector().get(clazz);
        TemplateModel retval = null;
        try {
            try {
                if (this.wrapper.isMethodsShadowItems()) {
                    Object fd = classInfo.get(key);
                    if (fd != null) {
                        retval = invokeThroughDescriptor(fd, classInfo, beforeMethodCall);
                    } else {
                        retval = invokeGenericGet(classInfo, clazz, key);
                    }
                } else {
                    TemplateModel model = invokeGenericGet(classInfo, clazz, key);
                    TemplateModel nullModel = this.wrapper.wrap(null);
                    if (model != nullModel && model != UNKNOWN) {
                        return model;
                    }
                    Object fd2 = classInfo.get(key);
                    if (fd2 != null) {
                        retval = invokeThroughDescriptor(fd2, classInfo, beforeMethodCall);
                        if (retval == UNKNOWN && model == nullModel) {
                            retval = nullModel;
                        }
                    }
                }
                if (retval == UNKNOWN) {
                    if (this.wrapper.isStrict()) {
                        throw new InvalidPropertyException("No such bean property: " + key);
                    }
                    if (LOG.isDebugEnabled()) {
                        logNoSuchKey(key, classInfo);
                    }
                    retval = this.wrapper.wrap(null);
                }
                return retval;
            } catch (MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException | TemplateModelException e) {
                throw e;
            }
        } catch (Exception e2) {
            throw new _TemplateModelException(e2, "An error has occurred when reading existing sub-variable ", new _DelayedJQuote(key), "; see cause exception! The type of the containing value was: ", new _DelayedFTLTypeDescription(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TemplateModel getBeforeMethodCall(String key) throws Exception {
        TemplateModel result = get(key, true);
        if ((result instanceof TemplateMethodModelEx) || result == null) {
            return result;
        }
        throw new MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException(result, null);
    }

    private void logNoSuchKey(String key, Map<?, ?> keyMap) {
        LOG.debug("Key " + StringUtil.jQuoteNoXSS(key) + " was not found on instance of " + this.object.getClass().getName() + ". Introspection information for the class is: " + keyMap);
    }

    protected boolean hasPlainGetMethod() {
        return this.wrapper.getClassIntrospector().get(this.object.getClass()).get(ClassIntrospector.GENERIC_GET_KEY) != null;
    }

    private TemplateModel invokeThroughDescriptor(Object desc, Map<Object, Object> classInfo, boolean beforeMethodCall) throws IllegalAccessException, TemplateModelException, IllegalArgumentException, InvocationTargetException, MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException {
        TemplateModel cachedModel;
        synchronized (this) {
            cachedModel = this.memberCache != null ? this.memberCache.get(desc) : null;
        }
        if (cachedModel != null) {
            return cachedModel;
        }
        TemplateModel resultModel = UNKNOWN;
        if (desc instanceof FastPropertyDescriptor) {
            FastPropertyDescriptor pd = (FastPropertyDescriptor) desc;
            Method indexedReadMethod = pd.getIndexedReadMethod();
            if (indexedReadMethod != null) {
                if (!this.wrapper.getPreferIndexedReadMethod() && pd.getReadMethod() != null) {
                    resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
                } else {
                    SimpleMethodModel simpleMethodModel = new SimpleMethodModel(this.object, indexedReadMethod, ClassIntrospector.getArgTypes(classInfo, indexedReadMethod), this.wrapper);
                    cachedModel = simpleMethodModel;
                    resultModel = simpleMethodModel;
                }
            } else if (!beforeMethodCall) {
                resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
            } else if (pd.isMethodInsteadOfPropertyValueBeforeCall()) {
                resultModel = new SimpleMethodModel(this.object, pd.getReadMethod(), CollectionUtils.EMPTY_CLASS_ARRAY, this.wrapper);
            } else {
                resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
                if (!(resultModel instanceof TemplateMethodModel) && !(resultModel instanceof Macro)) {
                    throw new MethodCallAwareTemplateHashModel.ShouldNotBeGetAsMethodException(resultModel, "This member of the parent object is seen by templates as a property of it (with other words, an attribute, or a field), not a method of it. Thus, to get its value, it must not be called as a method.");
                }
            }
        } else if (desc instanceof Field) {
            resultModel = this.wrapper.readField(this.object, (Field) desc);
        } else if (desc instanceof Method) {
            Method method = (Method) desc;
            SimpleMethodModel simpleMethodModel2 = new SimpleMethodModel(this.object, method, ClassIntrospector.getArgTypes(classInfo, method), this.wrapper);
            cachedModel = simpleMethodModel2;
            resultModel = simpleMethodModel2;
        } else if (desc instanceof OverloadedMethods) {
            OverloadedMethodsModel overloadedMethodsModel = new OverloadedMethodsModel(this.object, (OverloadedMethods) desc, this.wrapper);
            cachedModel = overloadedMethodsModel;
            resultModel = overloadedMethodsModel;
        }
        if (cachedModel != null) {
            synchronized (this) {
                if (this.memberCache == null) {
                    this.memberCache = new HashMap<>();
                }
                this.memberCache.put(desc, cachedModel);
            }
        }
        return resultModel;
    }

    void clearMemberCache() {
        synchronized (this) {
            this.memberCache = null;
        }
    }

    protected TemplateModel invokeGenericGet(Map classInfo, Class<?> clazz, String key) throws IllegalAccessException, TemplateModelException, InvocationTargetException {
        Method genericGet = (Method) classInfo.get(ClassIntrospector.GENERIC_GET_KEY);
        return genericGet == null ? UNKNOWN : this.wrapper.invokeMethod(this.object, genericGet, new Object[]{key});
    }

    protected TemplateModel wrap(Object obj) throws TemplateModelException {
        return this.wrapper.getOuterIdentity().wrap(obj);
    }

    protected Object unwrap(TemplateModel model) throws TemplateModelException {
        return this.wrapper.unwrap(model);
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        if (this.object instanceof String) {
            return ((String) this.object).length() == 0;
        }
        if (this.object instanceof Collection) {
            return ((Collection) this.object).isEmpty();
        }
        if ((this.object instanceof Iterator) && this.wrapper.is2324Bugfixed()) {
            return !((Iterator) this.object).hasNext();
        }
        if (this.object instanceof Map) {
            return ((Map) this.object).isEmpty();
        }
        return this.object == null || Boolean.FALSE.equals(this.object);
    }

    @Override // freemarker.template.AdapterTemplateModel
    public Object getAdaptedObject(Class<?> hint) {
        return this.object;
    }

    @Override // freemarker.ext.util.WrapperTemplateModel
    public Object getWrappedObject() {
        return this.object;
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        return this.wrapper.getClassIntrospector().keyCount(this.object.getClass());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() {
        return new CollectionAndSequence(new SimpleSequence(keySet(), this.wrapper));
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() throws TemplateModelException {
        List<Object> values = new ArrayList<>(size());
        TemplateModelIterator it = keys().iterator();
        while (it.hasNext()) {
            String key = ((TemplateScalarModel) it.next()).getAsString();
            values.add(get(key));
        }
        return new CollectionAndSequence(new SimpleSequence(values, this.wrapper));
    }

    String getAsClassicCompatibleString() {
        String s;
        return (this.object == null || (s = this.object.toString()) == null) ? BeanDefinitionParserDelegate.NULL_ELEMENT : s;
    }

    public String toString() {
        return this.object.toString();
    }

    protected Set keySet() {
        return this.wrapper.getClassIntrospector().keySet(this.object.getClass());
    }

    @Override // freemarker.template.TemplateModelWithAPISupport
    public TemplateModel getAPI() throws TemplateModelException {
        return this.wrapper.wrapAsAPI(this.object);
    }
}
