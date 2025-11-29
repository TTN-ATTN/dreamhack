package org.springframework.cglib.transform;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/transform/AbstractProcessTask.class */
public abstract class AbstractProcessTask extends Task {
    private Vector filesets = new Vector();

    protected abstract void processFile(File file) throws Exception;

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    protected Collection getFiles() {
        Map fileMap = new HashMap();
        Project p = getProject();
        for (int i = 0; i < this.filesets.size(); i++) {
            FileSet fs = (FileSet) this.filesets.elementAt(i);
            DirectoryScanner ds = fs.getDirectoryScanner(p);
            String[] srcFiles = ds.getIncludedFiles();
            File dir = fs.getDir(p);
            for (String str : srcFiles) {
                File src = new File(dir, str);
                fileMap.put(src.getAbsolutePath(), src);
            }
        }
        return fileMap.values();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    public void execute() throws BuildException {
        beforeExecute();
        Iterator it = getFiles().iterator();
        while (it.hasNext()) {
            try {
                processFile((File) it.next());
            } catch (Exception e) {
                throw new BuildException(e);
            }
        }
    }

    protected void beforeExecute() throws BuildException {
    }
}
