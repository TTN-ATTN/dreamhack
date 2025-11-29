package freemarker.template.utility;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/ToCanonical.class */
public class ToCanonical {
    static Configuration config = Configuration.getDefaultConfiguration();

    @Deprecated
    public static void main(String[] args) {
        config.setWhitespaceStripping(false);
        if (args.length == 0) {
            usage();
        }
        for (String str : args) {
            File f = new File(str);
            if (!f.exists()) {
                System.err.println("File " + f + " doesn't exist.");
            }
            try {
                convertFile(f);
            } catch (Exception e) {
                System.err.println("Error converting file: " + f);
                e.printStackTrace();
            }
        }
    }

    static void convertFile(File f) throws IOException {
        File fullPath = f.getAbsoluteFile();
        File dir = fullPath.getParentFile();
        String filename = fullPath.getName();
        File convertedFile = new File(dir, filename + ".canonical");
        config.setDirectoryForTemplateLoading(dir);
        Template template = config.getTemplate(filename);
        FileWriter output = new FileWriter(convertedFile);
        Throwable th = null;
        try {
            template.dump(output);
            if (output != null) {
                if (0 != 0) {
                    try {
                        output.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                output.close();
            }
        } catch (Throwable th3) {
            if (output != null) {
                if (0 != 0) {
                    try {
                        output.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    output.close();
                }
            }
            throw th3;
        }
    }

    static void usage() {
        System.err.println("Usage: java freemarker.template.utility.ToCanonical <filename(s)>");
    }
}
