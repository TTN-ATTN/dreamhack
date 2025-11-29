package freemarker.ext.jython;

import org.python.core.PyJavaInstance;
import org.python.core.PyObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jython/_Jython22VersionAdapter.class */
public class _Jython22VersionAdapter extends JythonVersionAdapter {
    @Override // freemarker.ext.jython.JythonVersionAdapter
    public boolean isPyInstance(Object obj) {
        return obj instanceof PyJavaInstance;
    }

    @Override // freemarker.ext.jython.JythonVersionAdapter
    public Object pyInstanceToJava(Object pyInstance) {
        return ((PyJavaInstance) pyInstance).__tojava__(Object.class);
    }

    @Override // freemarker.ext.jython.JythonVersionAdapter
    public String getPythonClassName(PyObject pyObject) {
        return pyObject.getType().getFullName();
    }
}
