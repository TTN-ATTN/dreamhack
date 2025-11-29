package freemarker.ext.beans;

import java.lang.ref.WeakReference;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.ReloaderFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/JRebelClassChangeNotifier.class */
class JRebelClassChangeNotifier implements ClassChangeNotifier {
    JRebelClassChangeNotifier() {
    }

    static void testAvailability() {
        ReloaderFactory.getInstance();
    }

    @Override // freemarker.ext.beans.ClassChangeNotifier
    public void subscribe(ClassIntrospector classIntrospector) {
        ReloaderFactory.getInstance().addClassReloadListener(new ClassIntrospectorCacheInvalidator(classIntrospector));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/JRebelClassChangeNotifier$ClassIntrospectorCacheInvalidator.class */
    private static class ClassIntrospectorCacheInvalidator implements ClassEventListener {
        private final WeakReference ref;

        ClassIntrospectorCacheInvalidator(ClassIntrospector w) {
            this.ref = new WeakReference(w);
        }

        public void onClassEvent(int eventType, Class pClass) {
            ClassIntrospector ci = (ClassIntrospector) this.ref.get();
            if (ci == null) {
                ReloaderFactory.getInstance().removeClassReloadListener(this);
            } else if (eventType == 1) {
                ci.remove(pClass);
            }
        }
    }
}
