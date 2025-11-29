package freemarker.ext.ant;

import freemarker.template.utility.ClassUtil;
import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/ant/JythonAntTask.class */
public class JythonAntTask extends Task {
    private File scriptFile;
    private String script = "";
    private UnlinkedJythonOperations jythonOps;

    public void setFile(File scriptFile) throws BuildException {
        ensureJythonOpsExists();
        this.scriptFile = scriptFile;
    }

    public void addText(String text) {
        this.script += text;
    }

    public void execute(Map vars) throws BuildException {
        if (this.scriptFile != null) {
            ensureJythonOpsExists();
            this.jythonOps.execute(this.scriptFile, vars);
        }
        if (this.script.trim().length() > 0) {
            ensureJythonOpsExists();
            String finalScript = ProjectHelper.replaceProperties(this.project, this.script, this.project.getProperties());
            this.jythonOps.execute(finalScript, vars);
        }
    }

    private void ensureJythonOpsExists() {
        if (this.jythonOps == null) {
            try {
                Class clazz = ClassUtil.forName("freemarker.ext.ant.UnlinkedJythonOperationsImpl");
                try {
                    this.jythonOps = (UnlinkedJythonOperations) clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("An exception has been thrown when trying to create a freemarker.ext.ant.JythonAntTask object. The exception was: " + e);
                }
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException("A ClassNotFoundException has been thrown when trying to get the freemarker.ext.ant.UnlinkedJythonOperationsImpl class. The error message was: " + e2.getMessage());
            }
        }
    }
}
