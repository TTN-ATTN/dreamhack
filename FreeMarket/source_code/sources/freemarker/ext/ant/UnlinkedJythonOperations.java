package freemarker.ext.ant;

import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/ant/UnlinkedJythonOperations.class */
interface UnlinkedJythonOperations {
    void execute(String str, Map map) throws BuildException;

    void execute(File file, Map map) throws BuildException;
}
