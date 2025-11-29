package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.Execute;
import freemarker.template.utility.ObjectConstructor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateClassResolver.class */
public interface TemplateClassResolver {
    public static final TemplateClassResolver UNRESTRICTED_RESOLVER = new TemplateClassResolver() { // from class: freemarker.core.TemplateClassResolver.1
        @Override // freemarker.core.TemplateClassResolver
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            try {
                return ClassUtil.forName(className);
            } catch (ClassNotFoundException e) {
                throw new _MiscTemplateException(e, env);
            }
        }
    };
    public static final TemplateClassResolver SAFER_RESOLVER = new TemplateClassResolver() { // from class: freemarker.core.TemplateClassResolver.2
        @Override // freemarker.core.TemplateClassResolver
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            if (className.equals(ObjectConstructor.class.getName()) || className.equals(Execute.class.getName()) || className.equals("freemarker.template.utility.JythonRuntime")) {
                throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
            }
            try {
                return ClassUtil.forName(className);
            } catch (ClassNotFoundException e) {
                throw new _MiscTemplateException(e, env);
            }
        }
    };
    public static final TemplateClassResolver ALLOWS_NOTHING_RESOLVER = new TemplateClassResolver() { // from class: freemarker.core.TemplateClassResolver.3
        @Override // freemarker.core.TemplateClassResolver
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
        }
    };

    Class resolve(String str, Environment environment, Template template) throws TemplateException;
}
