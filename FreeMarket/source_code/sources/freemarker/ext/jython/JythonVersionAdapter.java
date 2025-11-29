package freemarker.ext.jython;

import org.python.core.PyObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/JythonVersionAdapter.class */
public abstract class JythonVersionAdapter {
    public abstract boolean isPyInstance(Object obj);

    public abstract Object pyInstanceToJava(Object obj);

    public abstract String getPythonClassName(PyObject pyObject);
}
