package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/Transform.class */
public class Transform {
    private File inputFile;
    private File ftlFile;
    private File outputFile;
    private String encoding;
    private Locale locale;
    private Configuration cfg;

    @Deprecated
    public static void main(String[] args) {
        try {
            Transform proc = transformFromArgs(args);
            proc.transform();
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            usage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Transform(File inputFile, File ftlFile, File outputFile, Locale locale, String encoding) throws IOException {
        encoding = encoding == null ? System.getProperty("file.encoding") : encoding;
        locale = locale == null ? Locale.getDefault() : locale;
        this.encoding = encoding;
        this.locale = locale;
        this.inputFile = inputFile;
        this.ftlFile = ftlFile;
        this.outputFile = outputFile;
        File ftlDirectory = ftlFile.getAbsoluteFile().getParentFile();
        this.cfg = new Configuration();
        this.cfg.setDirectoryForTemplateLoading(ftlDirectory);
    }

    void transform() throws Exception {
        String templateName = this.ftlFile.getName();
        Template template = this.cfg.getTemplate(templateName, this.locale);
        NodeModel rootNode = NodeModel.parse(this.inputFile);
        OutputStream outputStream = System.out;
        if (this.outputFile != null) {
            outputStream = new FileOutputStream(this.outputFile);
        }
        Writer outputWriter = new OutputStreamWriter(outputStream, this.encoding);
        try {
            template.process(null, outputWriter, null, rootNode);
            if (this.outputFile != null) {
                outputWriter.close();
            }
        } catch (Throwable th) {
            if (this.outputFile != null) {
                outputWriter.close();
            }
            throw th;
        }
    }

    static Transform transformFromArgs(String[] args) throws IOException {
        int i = 0;
        String input = null;
        String output = null;
        String ftl = null;
        String loc = null;
        String enc = null;
        while (i < args.length) {
            int i2 = i;
            int i3 = i + 1;
            String dashArg = args[i2];
            if (i3 >= args.length) {
                throw new IllegalArgumentException("");
            }
            i = i3 + 1;
            String arg = args[i3];
            if (dashArg.equals("-in")) {
                if (input != null) {
                    throw new IllegalArgumentException("The input file should only be specified once");
                }
                input = arg;
            } else if (dashArg.equals("-ftl")) {
                if (ftl != null) {
                    throw new IllegalArgumentException("The ftl file should only be specified once");
                }
                ftl = arg;
            } else if (dashArg.equals("-out")) {
                if (output != null) {
                    throw new IllegalArgumentException("The output file should only be specified once");
                }
                output = arg;
            } else if (dashArg.equals("-locale")) {
                if (loc != null) {
                    throw new IllegalArgumentException("The locale should only be specified once");
                }
                loc = arg;
            } else if (dashArg.equals("-encoding")) {
                if (enc != null) {
                    throw new IllegalArgumentException("The encoding should only be specified once");
                }
                enc = arg;
            } else {
                throw new IllegalArgumentException("Unknown input argument: " + dashArg);
            }
        }
        if (input == null) {
            throw new IllegalArgumentException("No input file specified.");
        }
        if (ftl == null) {
            throw new IllegalArgumentException("No ftl file specified.");
        }
        File inputFile = new File(input).getAbsoluteFile();
        File ftlFile = new File(ftl).getAbsoluteFile();
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + input);
        }
        if (!ftlFile.exists()) {
            throw new IllegalArgumentException("FTL file does not exist: " + ftl);
        }
        if (!inputFile.isFile() || !inputFile.canRead()) {
            throw new IllegalArgumentException("Input file must be a readable file: " + input);
        }
        if (!ftlFile.isFile() || !ftlFile.canRead()) {
            throw new IllegalArgumentException("FTL file must be a readable file: " + ftl);
        }
        File outputFile = null;
        if (output != null) {
            outputFile = new File(output).getAbsoluteFile();
            File outputDirectory = outputFile.getParentFile();
            if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
                throw new IllegalArgumentException("The output directory must exist and be writable: " + outputDirectory);
            }
        }
        Locale locale = Locale.getDefault();
        if (loc != null) {
            locale = localeFromString(loc);
        }
        return new Transform(inputFile, ftlFile, outputFile, locale, enc);
    }

    static Locale localeFromString(String ls) {
        if (ls == null) {
            ls = "";
        }
        String country = "";
        String variant = "";
        StringTokenizer st = new StringTokenizer(ls, "_-,");
        if (st.hasMoreTokens()) {
            String lang = st.nextToken();
            if (st.hasMoreTokens()) {
                country = st.nextToken();
                if (st.hasMoreTokens()) {
                    variant = st.nextToken();
                }
            }
            return new Locale(lang, country, variant);
        }
        return Locale.getDefault();
    }

    static void usage() {
        System.err.println("Usage: java freemarker.ext.dom.Transform -in <xmlfile> -ftl <ftlfile> [-out <outfile>] [-locale <locale>] [-encoding <encoding>]");
        if (Environment.getCurrentEnvironment() == null) {
            System.exit(-1);
        }
    }
}
