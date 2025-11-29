package freemarker.template.utility;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Execute.class */
public class Execute implements TemplateMethodModel {
    private static final int OUTPUT_BUFFER_SIZE = 1024;

    /* JADX WARN: Finally extract failed */
    @Override // freemarker.template.TemplateMethodModel
    public Object exec(List arguments) throws TemplateModelException, IOException {
        StringBuilder aOutputBuffer = new StringBuilder();
        if (arguments.size() < 1) {
            throw new TemplateModelException("Need an argument to execute");
        }
        String aExecute = (String) arguments.get(0);
        try {
            Process exec = Runtime.getRuntime().exec(aExecute);
            InputStream execOut = exec.getInputStream();
            Throwable th = null;
            try {
                Reader execReader = new InputStreamReader(execOut);
                char[] buffer = new char[1024];
                for (int bytes_read = execReader.read(buffer); bytes_read > 0; bytes_read = execReader.read(buffer)) {
                    aOutputBuffer.append(buffer, 0, bytes_read);
                }
                if (execOut != null) {
                    if (0 != 0) {
                        try {
                            execOut.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        execOut.close();
                    }
                }
                return aOutputBuffer.toString();
            } catch (Throwable th3) {
                if (execOut != null) {
                    if (0 != 0) {
                        try {
                            execOut.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        execOut.close();
                    }
                }
                throw th3;
            }
        } catch (IOException ioe) {
            throw new TemplateModelException(ioe.getMessage());
        }
    }
}
