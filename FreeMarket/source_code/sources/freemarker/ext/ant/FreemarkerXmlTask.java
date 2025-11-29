package freemarker.ext.ant;

import freemarker.core.Environment;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.xml.NodeListModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.SecurityUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/ant/FreemarkerXmlTask.class */
public class FreemarkerXmlTask extends MatchingTask {
    private JythonAntTask prepareModel;
    private JythonAntTask prepareEnvironment;
    private DocumentBuilder builder;
    private File destDir;
    private File baseDir;
    private File templateDir;
    private String templateName;
    private Template parsedTemplate;
    private TemplateModel projectTemplate;
    private TemplateNodeModel projectNode;
    private TemplateModel propertiesTemplate;
    private TemplateModel userPropertiesTemplate;
    private Configuration cfg = new Configuration();
    private long templateFileLastModified = 0;
    private String projectAttribute = null;
    private File projectFile = null;
    private long projectFileLastModified = 0;
    private boolean incremental = true;
    private String extension = ThymeleafProperties.DEFAULT_SUFFIX;
    private String encoding = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
    private String templateEncoding = this.encoding;
    private boolean validation = false;
    private String models = "";
    private final Map modelsMap = new HashMap();
    private final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    public FreemarkerXmlTask() {
        this.builderFactory.setNamespaceAware(true);
    }

    public void setBasedir(File dir) {
        this.baseDir = dir;
    }

    public void setDestdir(File dir) {
        this.destDir = dir;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    public void setTemplateDir(File templateDir) throws BuildException {
        this.templateDir = templateDir;
        try {
            this.cfg.setDirectoryForTemplateLoading(templateDir);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public void setProjectfile(String projectAttribute) {
        this.projectAttribute = projectAttribute;
    }

    public void setIncremental(String incremental) {
        this.incremental = (incremental.equalsIgnoreCase("false") || incremental.equalsIgnoreCase("no") || incremental.equalsIgnoreCase(CustomBooleanEditor.VALUE_OFF)) ? false : true;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTemplateEncoding(String inputEncoding) {
        this.templateEncoding = inputEncoding;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public void setModels(String models) {
        this.models = models;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    public void execute() throws BuildException {
        if (this.baseDir == null) {
            this.baseDir = getProject().getBaseDir();
        }
        if (this.destDir == null) {
            throw new BuildException("destdir attribute must be set!", getLocation());
        }
        File templateFile = null;
        if (this.templateDir == null) {
            if (this.templateName != null) {
                templateFile = new File(this.templateName);
                if (!templateFile.isAbsolute()) {
                    templateFile = new File(getProject().getBaseDir(), this.templateName);
                }
                this.templateDir = templateFile.getParentFile();
                this.templateName = templateFile.getName();
            } else {
                this.templateDir = this.baseDir;
            }
            setTemplateDir(this.templateDir);
        } else if (this.templateName != null) {
            if (new File(this.templateName).isAbsolute()) {
                throw new BuildException("Do not specify an absolute location for the template as well as a templateDir");
            }
            templateFile = new File(this.templateDir, this.templateName);
        }
        if (templateFile != null) {
            this.templateFileLastModified = templateFile.lastModified();
        }
        try {
            if (this.templateName != null) {
                this.parsedTemplate = this.cfg.getTemplate(this.templateName, this.templateEncoding);
            }
            log("Transforming into: " + this.destDir.getAbsolutePath(), 2);
            if (this.projectAttribute != null && this.projectAttribute.length() > 0) {
                this.projectFile = new File(this.baseDir, this.projectAttribute);
                if (this.projectFile.isFile()) {
                    this.projectFileLastModified = this.projectFile.lastModified();
                } else {
                    log("Project file is defined, but could not be located: " + this.projectFile.getAbsolutePath(), 2);
                    this.projectFile = null;
                }
            }
            generateModels();
            DirectoryScanner scanner = getDirectoryScanner(this.baseDir);
            this.propertiesTemplate = wrapMap(this.project.getProperties());
            this.userPropertiesTemplate = wrapMap(this.project.getUserProperties());
            this.builderFactory.setValidating(this.validation);
            try {
                this.builder = this.builderFactory.newDocumentBuilder();
                String[] list = scanner.getIncludedFiles();
                for (String str : list) {
                    process(this.baseDir, str, this.destDir);
                }
            } catch (ParserConfigurationException e) {
                throw new BuildException("Could not create document builder", e, getLocation());
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.toString());
        }
    }

    public void addConfiguredJython(JythonAntTask jythonAntTask) {
        this.prepareEnvironment = jythonAntTask;
    }

    public void addConfiguredPrepareModel(JythonAntTask prepareModel) {
        this.prepareModel = prepareModel;
    }

    public void addConfiguredPrepareEnvironment(JythonAntTask prepareEnvironment) {
        this.prepareEnvironment = prepareEnvironment;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    /* JADX WARN: Failed to apply debug info
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnoreUnknown(TypeUpdate.java:74)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:137)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:133)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.searchAndApplyVarDebugInfo(DebugInfoApplyVisitor.java:75)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.lambda$applyDebugInfo$0(DebugInfoApplyVisitor.java:68)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:68)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.visit(DebugInfoApplyVisitor.java:55)
     */
    /* JADX WARN: Failed to calculate best type for var: r19v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r19v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r20v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r20v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 19, insn: 0x020f: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r19 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('writer' java.io.Writer)]) A[TRY_LEAVE], block:B:45:0x020f */
    /* JADX WARN: Not initialized variable reg: 20, insn: 0x0214: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r20 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:47:0x0214 */
    /* JADX WARN: Type inference failed for: r19v0, names: [writer], types: [java.io.Writer] */
    /* JADX WARN: Type inference failed for: r20v0, types: [java.lang.Throwable] */
    private void process(File baseDir, String xmlFile, File destDir) throws BuildException {
        File outFile = null;
        File inFile = null;
        try {
            inFile = new File(baseDir, xmlFile);
            outFile = new File(destDir, xmlFile.substring(0, xmlFile.lastIndexOf(46)) + this.extension);
            if (!this.incremental || inFile.lastModified() > outFile.lastModified() || this.templateFileLastModified > outFile.lastModified() || this.projectFileLastModified > outFile.lastModified()) {
                ensureDirectoryFor(outFile);
                log("Input:  " + xmlFile, 2);
                if (this.projectTemplate == null && this.projectFile != null) {
                    Document document = this.builder.parse(this.projectFile);
                    this.projectTemplate = new NodeListModel(this.builder.parse(this.projectFile));
                    this.projectNode = NodeModel.wrap(document);
                }
                try {
                    Document document2 = this.builder.parse(inFile);
                    NodeListModel nodeListModel = new NodeListModel(document2);
                    NodeModel nodeModelWrap = NodeModel.wrap(document2);
                    HashMap map = new HashMap();
                    map.put("document", nodeListModel);
                    insertDefaults(map);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), this.encoding));
                    Throwable th = null;
                    if (this.parsedTemplate == null) {
                        throw new BuildException("No template file specified in build script or in XML file");
                    }
                    if (this.prepareModel != null) {
                        HashMap map2 = new HashMap();
                        map2.put("model", map);
                        map2.put("doc", document2);
                        if (this.projectNode != null) {
                            map2.put("project", ((NodeModel) this.projectNode).getNode());
                        }
                        this.prepareModel.execute(map2);
                    }
                    Environment environmentCreateProcessingEnvironment = this.parsedTemplate.createProcessingEnvironment(map, bufferedWriter);
                    environmentCreateProcessingEnvironment.setCurrentVisitorNode(nodeModelWrap);
                    if (this.prepareEnvironment != null) {
                        HashMap map3 = new HashMap();
                        map3.put("env", environmentCreateProcessingEnvironment);
                        map3.put("doc", document2);
                        if (this.projectNode != null) {
                            map3.put("project", ((NodeModel) this.projectNode).getNode());
                        }
                        this.prepareEnvironment.execute(map3);
                    }
                    environmentCreateProcessingEnvironment.process();
                    bufferedWriter.flush();
                    if (bufferedWriter != null) {
                        if (0 != 0) {
                            try {
                                bufferedWriter.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            bufferedWriter.close();
                        }
                    }
                    log("Output: " + outFile, 2);
                } finally {
                }
            }
        } catch (SAXParseException spe) {
            Throwable rootCause = spe;
            if (spe.getException() != null) {
                rootCause = spe.getException();
            }
            log("XML parsing error in " + inFile.getAbsolutePath(), 0);
            log("Line number " + spe.getLineNumber());
            log("Column number " + spe.getColumnNumber());
            throw new BuildException(rootCause, getLocation());
        } catch (Throwable e) {
            if (outFile != null && !outFile.delete() && outFile.exists()) {
                log("Failed to delete " + outFile, 1);
            }
            e.printStackTrace();
            throw new BuildException(e, getLocation());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    private void generateModels() throws BuildException {
        String name;
        String clazz;
        StringTokenizer modelTokenizer = new StringTokenizer(this.models, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
        while (modelTokenizer.hasMoreTokens()) {
            String modelSpec = modelTokenizer.nextToken();
            int sep = modelSpec.indexOf(61);
            if (sep == -1) {
                clazz = modelSpec;
                int dot = clazz.lastIndexOf(46);
                if (dot == -1) {
                    name = clazz;
                } else {
                    name = clazz.substring(dot + 1);
                }
            } else {
                name = modelSpec.substring(0, sep);
                clazz = modelSpec.substring(sep + 1);
            }
            try {
                this.modelsMap.put(name, ClassUtil.forName(clazz).newInstance());
            } catch (Exception e) {
                throw new BuildException(e);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.tools.ant.BuildException */
    private void ensureDirectoryFor(File targetFile) throws BuildException {
        File directory = new File(targetFile.getParent());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new BuildException("Unable to create directory: " + directory.getAbsolutePath(), getLocation());
        }
    }

    private static TemplateModel wrapMap(Map table) {
        SimpleHash model = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        for (Map.Entry entry : table.entrySet()) {
            model.put(String.valueOf(entry.getKey()), new SimpleScalar(String.valueOf(entry.getValue())));
        }
        return model;
    }

    protected void insertDefaults(Map root) {
        root.put("properties", this.propertiesTemplate);
        root.put("userProperties", this.userPropertiesTemplate);
        if (this.projectTemplate != null) {
            root.put("project", this.projectTemplate);
            root.put("project_node", this.projectNode);
        }
        if (this.modelsMap.size() > 0) {
            for (Map.Entry entry : this.modelsMap.entrySet()) {
                root.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
