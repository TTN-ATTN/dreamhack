package freemarker.ext.jython;

import freemarker.template.utility.StringUtil;
import org.python.core.PySystemState;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonVersionAdapterHolder.class */
class JythonVersionAdapterHolder {
    static final JythonVersionAdapter INSTANCE;

    JythonVersionAdapterHolder() {
    }

    static {
        try {
            int version = StringUtil.versionStringToInt(PySystemState.class.getField("version").get(null).toString());
            ClassLoader cl = JythonVersionAdapter.class.getClassLoader();
            try {
                if (version >= 2005000) {
                    INSTANCE = (JythonVersionAdapter) cl.loadClass("freemarker.ext.jython._Jython25VersionAdapter").newInstance();
                } else if (version >= 2002000) {
                    INSTANCE = (JythonVersionAdapter) cl.loadClass("freemarker.ext.jython._Jython22VersionAdapter").newInstance();
                } else {
                    INSTANCE = (JythonVersionAdapter) cl.loadClass("freemarker.ext.jython._Jython20And21VersionAdapter").newInstance();
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw adapterCreationException(e);
            }
        } catch (Exception e2) {
            throw new RuntimeException("Failed to get Jython version: " + e2);
        }
    }

    private static RuntimeException adapterCreationException(Exception e) {
        return new RuntimeException("Unexpected exception when creating JythonVersionAdapter", e);
    }
}
