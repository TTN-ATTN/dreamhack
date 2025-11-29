package freemarker.ext.beans;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ClassBasedModelFactory.class */
abstract class ClassBasedModelFactory implements TemplateHashModel {
    private final BeansWrapper wrapper;
    private final Map<String, TemplateModel> cache = new ConcurrentHashMap();
    private final Set<String> classIntrospectionsInProgress = new HashSet();

    protected abstract TemplateModel createModel(Class<?> cls) throws TemplateModelException;

    protected ClassBasedModelFactory(BeansWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String key) throws TemplateModelException {
        try {
            return getInternal(key);
        } catch (Exception e) {
            if (e instanceof TemplateModelException) {
                throw ((TemplateModelException) e);
            }
            throw new _TemplateModelException(e, "Failed to get value for key ", new _DelayedJQuote(key), "; see cause exception.");
        }
    }

    private TemplateModel getInternal(String key) throws TemplateModelException, ClassNotFoundException {
        TemplateModel model = this.cache.get(key);
        if (model != null) {
            return model;
        }
        Object sharedLock = this.wrapper.getSharedIntrospectionLock();
        synchronized (sharedLock) {
            TemplateModel model2 = this.cache.get(key);
            if (model2 != null) {
                return model2;
            }
            while (model2 == null && this.classIntrospectionsInProgress.contains(key)) {
                try {
                    sharedLock.wait();
                    model2 = this.cache.get(key);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Class inrospection data lookup aborted: " + e);
                }
            }
            if (model2 != null) {
                return model2;
            }
            this.classIntrospectionsInProgress.add(key);
            ClassIntrospector classIntrospector = this.wrapper.getClassIntrospector();
            int classIntrospectorClearingCounter = classIntrospector.getClearingCounter();
            try {
                Class<?> clazz = ClassUtil.forName(key);
                classIntrospector.get(clazz);
                TemplateModel model3 = createModel(clazz);
                if (model3 != null) {
                    synchronized (sharedLock) {
                        if (classIntrospector == this.wrapper.getClassIntrospector() && classIntrospectorClearingCounter == classIntrospector.getClearingCounter()) {
                            this.cache.put(key, model3);
                        }
                    }
                }
                synchronized (sharedLock) {
                    this.classIntrospectionsInProgress.remove(key);
                    sharedLock.notifyAll();
                }
                return model3;
            } catch (Throwable th) {
                synchronized (sharedLock) {
                    this.classIntrospectionsInProgress.remove(key);
                    sharedLock.notifyAll();
                    throw th;
                }
            }
        }
    }

    void clearCache() {
        synchronized (this.wrapper.getSharedIntrospectionLock()) {
            this.cache.clear();
        }
    }

    void removeFromCache(Class<?> clazz) {
        synchronized (this.wrapper.getSharedIntrospectionLock()) {
            this.cache.remove(clazz.getName());
        }
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return false;
    }

    protected BeansWrapper getWrapper() {
        return this.wrapper;
    }
}
