package freemarker.ext.ant;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.python.util.PythonInterpreter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/ant/UnlinkedJythonOperationsImpl.class */
public class UnlinkedJythonOperationsImpl implements UnlinkedJythonOperations {
    @Override // freemarker.ext.ant.UnlinkedJythonOperations
    public void execute(String script, Map vars) throws BuildException {
        PythonInterpreter pi = createInterpreter(vars);
        pi.exec(script);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    @Override // freemarker.ext.ant.UnlinkedJythonOperations
    public void execute(File file, Map vars) throws BuildException {
        PythonInterpreter pi = createInterpreter(vars);
        try {
            pi.execfile(file.getCanonicalPath());
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private PythonInterpreter createInterpreter(Map vars) {
        PythonInterpreter pi = new PythonInterpreter();
        for (Map.Entry ent : vars.entrySet()) {
            pi.set((String) ent.getKey(), ent.getValue());
        }
        return pi;
    }
}
