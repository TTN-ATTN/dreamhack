package freemarker.ext.beans;

import freemarker.log.Logger;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/StaticModel.class */
final class StaticModel implements TemplateHashModelEx {
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    private final Class<?> clazz;
    private final BeansWrapper wrapper;
    private final Map<String, Object> map = new HashMap();

    StaticModel(Class<?> clazz, BeansWrapper wrapper) throws TemplateModelException, SecurityException {
        this.clazz = clazz;
        this.wrapper = wrapper;
        populate();
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        Object model = this.map.get(key);
        if (model instanceof TemplateModel) {
            return (TemplateModel) model;
        }
        if (model instanceof Field) {
            try {
                return this.wrapper.readField(null, (Field) model);
            } catch (IllegalAccessException e) {
                throw new TemplateModelException("Illegal access for field " + key + " of class " + this.clazz.getName());
            }
        }
        throw new TemplateModelException("No such key: " + key + " in class " + this.clazz.getName());
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public int size() {
        return this.map.size();
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel) this.wrapper.getOuterIdentity().wrap(this.map.keySet());
    }

    @Override // freemarker.template.TemplateHashModelEx
    public TemplateCollectionModel values() throws TemplateModelException {
        return (TemplateCollectionModel) this.wrapper.getOuterIdentity().wrap(this.map.values());
    }

    private void populate() throws TemplateModelException, SecurityException {
        if (!Modifier.isPublic(this.clazz.getModifiers())) {
            throw new TemplateModelException("Can't wrap the non-public class " + this.clazz.getName());
        }
        if (this.wrapper.getExposureLevel() == 3) {
            return;
        }
        ClassMemberAccessPolicy effClassMemberAccessPolicy = this.wrapper.getClassIntrospector().getEffectiveMemberAccessPolicy().forClass(this.clazz);
        Field[] fields = this.clazz.getFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && effClassMemberAccessPolicy.isFieldExposed(field)) {
                if (Modifier.isFinal(mod)) {
                    try {
                        this.map.put(field.getName(), this.wrapper.readField(null, field));
                    } catch (IllegalAccessException e) {
                    }
                } else {
                    this.map.put(field.getName(), field);
                }
            }
        }
        if (this.wrapper.getExposureLevel() < 2) {
            Method[] methods = this.clazz.getMethods();
            for (Method method : methods) {
                int mod2 = method.getModifiers();
                if (Modifier.isPublic(mod2) && Modifier.isStatic(mod2) && effClassMemberAccessPolicy.isMethodExposed(method)) {
                    String name = method.getName();
                    Object obj = this.map.get(name);
                    if (obj instanceof Method) {
                        OverloadedMethods overloadedMethods = new OverloadedMethods(this.wrapper.is2321Bugfixed());
                        overloadedMethods.addMethod((Method) obj);
                        overloadedMethods.addMethod(method);
                        this.map.put(name, overloadedMethods);
                    } else if (obj instanceof OverloadedMethods) {
                        ((OverloadedMethods) obj).addMethod(method);
                    } else {
                        if (obj != null && LOG.isInfoEnabled()) {
                            LOG.info("Overwriting value [" + obj + "] for  key '" + name + "' with [" + method + "] in static model for " + this.clazz.getName());
                        }
                        this.map.put(name, method);
                    }
                }
            }
            for (Map.Entry<String, Object> entry : this.map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Method) {
                    Method method2 = (Method) value;
                    entry.setValue(new SimpleMethodModel(null, method2, method2.getParameterTypes(), this.wrapper));
                } else if (value instanceof OverloadedMethods) {
                    entry.setValue(new OverloadedMethodsModel(null, (OverloadedMethods) value, this.wrapper));
                }
            }
        }
    }
}
