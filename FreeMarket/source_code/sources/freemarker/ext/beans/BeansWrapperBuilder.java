package freemarker.ext.beans;

import freemarker.ext.beans._BeansAPI;
import freemarker.template.Version;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapperBuilder.class */
public class BeansWrapperBuilder extends BeansWrapperConfiguration {
    private static final Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> INSTANCE_CACHE = new WeakHashMap();
    private static final ReferenceQueue<BeansWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue<>();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BeansWrapperBuilder$BeansWrapperFactory.class */
    private static class BeansWrapperFactory implements _BeansAPI._BeansWrapperSubclassFactory<BeansWrapper, BeansWrapperConfiguration> {
        private static final BeansWrapperFactory INSTANCE = new BeansWrapperFactory();

        private BeansWrapperFactory() {
        }

        @Override // freemarker.ext.beans._BeansAPI._BeansWrapperSubclassFactory
        public BeansWrapper create(BeansWrapperConfiguration bwConf) {
            return new BeansWrapper(bwConf, true);
        }
    }

    public BeansWrapperBuilder(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    static void clearInstanceCache() {
        synchronized (INSTANCE_CACHE) {
            INSTANCE_CACHE.clear();
        }
    }

    static Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> getInstanceCache() {
        return INSTANCE_CACHE;
    }

    public BeansWrapper build() {
        return _BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, BeansWrapperFactory.INSTANCE);
    }
}
