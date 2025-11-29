package freemarker.template;

import freemarker.core.BugException;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.FMParser;
import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.TemplateElement;
import freemarker.core.TextBlock;
import freemarker.core.TokenMgrError;
import freemarker.core._CoreAPI;
import freemarker.debug.impl.DebuggerService;
import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.TreePath;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Template.class */
public class Template extends Configurable {
    public static final String DEFAULT_NAMESPACE_PREFIX = "D";
    public static final String NO_NS_PREFIX = "N";
    private static final int READER_BUFFER_SIZE = 4096;
    private Map macros;
    private List imports;
    private TemplateElement rootElement;
    private String encoding;
    private String defaultNS;
    private Object customLookupCondition;
    private int interpolationSyntax;
    private int actualTagSyntax;
    private int actualNamingConvention;
    private boolean autoEscaping;
    private OutputFormat outputFormat;
    private final String name;
    private final String sourceName;
    private final ArrayList lines;
    private final ParserConfiguration parserConfiguration;
    private Map prefixToNamespaceURILookup;
    private Map namespaceURIToPrefixLookup;
    private Version templateLanguageVersion;

    private Template(String name, String sourceName, Configuration cfg, ParserConfiguration customParserConfiguration) {
        super(toNonNull(cfg));
        this.macros = new HashMap();
        this.imports = new Vector();
        this.lines = new ArrayList();
        this.prefixToNamespaceURILookup = new HashMap();
        this.namespaceURIToPrefixLookup = new HashMap();
        this.name = name;
        this.sourceName = sourceName;
        this.templateLanguageVersion = normalizeTemplateLanguageVersion(toNonNull(cfg).getIncompatibleImprovements());
        this.parserConfiguration = customParserConfiguration != null ? customParserConfiguration : getConfiguration();
    }

    private static Configuration toNonNull(Configuration cfg) {
        return cfg != null ? cfg : Configuration.getDefaultConfiguration();
    }

    public Template(String name, Reader reader, Configuration cfg) throws IOException {
        this(name, (String) null, reader, cfg);
    }

    public Template(String name, String sourceCode, Configuration cfg) throws IOException {
        this(name, new StringReader(sourceCode), cfg);
    }

    public Template(String name, Reader reader, Configuration cfg, String encoding) throws IOException {
        this(name, null, reader, cfg, encoding);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg) throws IOException {
        this(name, sourceName, reader, cfg, null);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg, String encoding) throws IOException {
        this(name, sourceName, reader, cfg, null, encoding);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg, ParserConfiguration customParserConfiguration, String encoding) throws IOException {
        this(name, sourceName, cfg, customParserConfiguration);
        setEncoding(encoding);
        try {
            try {
                ParserConfiguration actualParserConfiguration = getParserConfiguration();
                if (!(reader instanceof BufferedReader) && !(reader instanceof StringReader)) {
                    reader = new BufferedReader(reader, 4096);
                }
                LineTableBuilder ltbReader = new LineTableBuilder(reader, actualParserConfiguration);
                try {
                    FMParser parser = new FMParser(this, ltbReader, actualParserConfiguration);
                    if (cfg != null) {
                        _CoreAPI.setPreventStrippings(parser, cfg.getPreventStrippings());
                    }
                    try {
                        this.rootElement = parser.Root();
                    } catch (IndexOutOfBoundsException exc) {
                        if (!ltbReader.hasFailure()) {
                            throw exc;
                        }
                        this.rootElement = null;
                    }
                    this.actualTagSyntax = parser._getLastTagSyntax();
                    this.interpolationSyntax = actualParserConfiguration.getInterpolationSyntax();
                    this.actualNamingConvention = parser._getLastNamingConvention();
                    ltbReader.close();
                    ltbReader.throwFailure();
                    DebuggerService.registerTemplate(this);
                    this.namespaceURIToPrefixLookup = Collections.unmodifiableMap(this.namespaceURIToPrefixLookup);
                    this.prefixToNamespaceURILookup = Collections.unmodifiableMap(this.prefixToNamespaceURILookup);
                } catch (TokenMgrError exc2) {
                    throw exc2.toParseException(this);
                }
            } catch (ParseException e) {
                e.setTemplateName(getSourceName());
                throw e;
            }
        } catch (Throwable th) {
            reader.close();
            throw th;
        }
    }

    @Deprecated
    public Template(String name, Reader reader) throws IOException {
        this(name, reader, (Configuration) null);
    }

    @Deprecated
    Template(String name, TemplateElement root, Configuration cfg) {
        this(name, (String) null, cfg, (ParserConfiguration) null);
        this.rootElement = root;
        DebuggerService.registerTemplate(this);
    }

    public static Template getPlainTextTemplate(String name, String content, Configuration config) {
        return getPlainTextTemplate(name, null, content, config);
    }

    public static Template getPlainTextTemplate(String name, String sourceName, String content, Configuration config) {
        try {
            Template template = new Template(name, sourceName, new StringReader("X"), config);
            _CoreAPI.replaceText((TextBlock) template.rootElement, content);
            DebuggerService.registerTemplate(template);
            return template;
        } catch (IOException e) {
            throw new BugException("Plain text template creation failed", e);
        }
    }

    private static Version normalizeTemplateLanguageVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        int v = incompatibleImprovements.intValue();
        if (v < _VersionInts.V_2_3_19) {
            return Configuration.VERSION_2_3_0;
        }
        if (v > _VersionInts.V_2_3_21) {
            return Configuration.VERSION_2_3_21;
        }
        return incompatibleImprovements;
    }

    public void process(Object dataModel, Writer out) throws TemplateException, IOException {
        createProcessingEnvironment(dataModel, out, null).process();
    }

    public void process(Object dataModel, Writer out, ObjectWrapper wrapper, TemplateNodeModel rootNode) throws TemplateException, IOException {
        Environment env = createProcessingEnvironment(dataModel, out, wrapper);
        if (rootNode != null) {
            env.setCurrentVisitorNode(rootNode);
        }
        env.process();
    }

    public void process(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
        createProcessingEnvironment(dataModel, out, wrapper).process();
    }

    public Environment createProcessingEnvironment(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
        TemplateHashModel dataModelHash;
        if (dataModel instanceof TemplateHashModel) {
            dataModelHash = (TemplateHashModel) dataModel;
        } else {
            if (wrapper == null) {
                wrapper = getObjectWrapper();
            }
            if (dataModel == null) {
                dataModelHash = new SimpleHash(wrapper);
            } else {
                TemplateModel wrappedDataModel = wrapper.wrap(dataModel);
                if (wrappedDataModel instanceof TemplateHashModel) {
                    dataModelHash = (TemplateHashModel) wrappedDataModel;
                } else {
                    if (wrappedDataModel == null) {
                        throw new IllegalArgumentException(wrapper.getClass().getName() + " converted " + dataModel.getClass().getName() + " to null.");
                    }
                    throw new IllegalArgumentException(wrapper.getClass().getName() + " didn't convert " + dataModel.getClass().getName() + " to a TemplateHashModel. Generally, you want to use a Map<String, Object> or a JavaBean as the root-map (aka. data-model) parameter. The Map key-s or JavaBean property names will be the variable names in the template.");
                }
            }
        }
        return new Environment(this, dataModelHash, out);
    }

    public Environment createProcessingEnvironment(Object dataModel, Writer out) throws TemplateException, IOException {
        return createProcessingEnvironment(dataModel, out, null);
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            dump(sw);
            return sw.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    public String getName() {
        return this.name;
    }

    public String getSourceName() {
        return this.sourceName != null ? this.sourceName : getName();
    }

    public Configuration getConfiguration() {
        return (Configuration) getParent();
    }

    public ParserConfiguration getParserConfiguration() {
        return this.parserConfiguration;
    }

    Version getTemplateLanguageVersion() {
        return this.templateLanguageVersion;
    }

    @Deprecated
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public Object getCustomLookupCondition() {
        return this.customLookupCondition;
    }

    public void setCustomLookupCondition(Object customLookupCondition) {
        this.customLookupCondition = customLookupCondition;
    }

    public int getActualTagSyntax() {
        return this.actualTagSyntax;
    }

    public int getInterpolationSyntax() {
        return this.interpolationSyntax;
    }

    public int getActualNamingConvention() {
        return this.actualNamingConvention;
    }

    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }

    void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean getAutoEscaping() {
        return this.autoEscaping;
    }

    void setAutoEscaping(boolean autoEscaping) {
        this.autoEscaping = autoEscaping;
    }

    public void dump(PrintStream ps) {
        ps.print(this.rootElement.getCanonicalForm());
    }

    public void dump(Writer out) throws IOException {
        out.write(this.rootElement.getCanonicalForm());
    }

    @Deprecated
    public void addMacro(Macro macro) {
        this.macros.put(macro.getName(), macro);
    }

    @Deprecated
    public void addImport(LibraryLoad ll) {
        this.imports.add(ll);
    }

    public String getSource(int beginColumn, int beginLine, int endColumn, int endLine) {
        if (beginLine < 1 || endLine < 1) {
            return null;
        }
        int beginColumn2 = beginColumn - 1;
        int endColumn2 = endColumn - 1;
        int endLine2 = endLine - 1;
        StringBuilder buf = new StringBuilder();
        for (int i = beginLine - 1; i <= endLine2; i++) {
            if (i < this.lines.size()) {
                buf.append(this.lines.get(i));
            }
        }
        int lastLineLength = this.lines.get(endLine2).toString().length();
        int trailingCharsToDelete = endColumn2 < lastLineLength ? (lastLineLength - endColumn2) - 1 : 0;
        buf.delete(0, beginColumn2);
        buf.delete(buf.length() - trailingCharsToDelete, buf.length());
        return buf.toString();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Template$LineTableBuilder.class */
    private class LineTableBuilder extends FilterReader {
        private final int tabSize;
        private final StringBuilder lineBuf;
        int lastChar;
        boolean closed;
        private Exception failure;

        LineTableBuilder(Reader r, ParserConfiguration parserConfiguration) {
            super(r);
            this.lineBuf = new StringBuilder();
            this.tabSize = parserConfiguration.getTabSize();
        }

        public boolean hasFailure() {
            return this.failure != null;
        }

        public void throwFailure() throws IOException {
            if (this.failure != null) {
                if (this.failure instanceof IOException) {
                    throw ((IOException) this.failure);
                }
                if (this.failure instanceof RuntimeException) {
                    throw ((RuntimeException) this.failure);
                }
                throw new UndeclaredThrowableException(this.failure);
            }
        }

        @Override // java.io.FilterReader, java.io.Reader
        public int read() throws IOException {
            try {
                int c = this.in.read();
                handleChar(c);
                return c;
            } catch (Exception e) {
                throw rememberException(e);
            }
        }

        private IOException rememberException(Exception e) throws IOException {
            if (!this.closed) {
                this.failure = e;
            }
            if (e instanceof IOException) {
                return (IOException) e;
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new UndeclaredThrowableException(e);
        }

        @Override // java.io.FilterReader, java.io.Reader
        public int read(char[] cbuf, int off, int len) throws IOException {
            try {
                int numchars = this.in.read(cbuf, off, len);
                for (int i = off; i < off + numchars; i++) {
                    char c = cbuf[i];
                    handleChar(c);
                }
                return numchars;
            } catch (Exception e) {
                throw rememberException(e);
            }
        }

        @Override // java.io.FilterReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.lineBuf.length() > 0) {
                Template.this.lines.add(this.lineBuf.toString());
                this.lineBuf.setLength(0);
            }
            super.close();
            this.closed = true;
        }

        private void handleChar(int c) {
            if (c == 10 || c == 13) {
                if (this.lastChar == 13 && c == 10) {
                    int lastIndex = Template.this.lines.size() - 1;
                    String lastLine = (String) Template.this.lines.get(lastIndex);
                    Template.this.lines.set(lastIndex, lastLine + '\n');
                } else {
                    this.lineBuf.append((char) c);
                    Template.this.lines.add(this.lineBuf.toString());
                    this.lineBuf.setLength(0);
                }
            } else if (c == 9 && this.tabSize != 1) {
                int numSpaces = this.tabSize - (this.lineBuf.length() % this.tabSize);
                for (int i = 0; i < numSpaces; i++) {
                    this.lineBuf.append(' ');
                }
            } else {
                this.lineBuf.append((char) c);
            }
            this.lastChar = c;
        }
    }

    @Deprecated
    public TemplateElement getRootTreeNode() {
        return this.rootElement;
    }

    @Deprecated
    public Map getMacros() {
        return this.macros;
    }

    @Deprecated
    public List getImports() {
        return this.imports;
    }

    @Deprecated
    public void addPrefixNSMapping(String prefix, String nsURI) {
        if (nsURI.length() == 0) {
            throw new IllegalArgumentException("Cannot map empty string URI");
        }
        if (prefix.length() == 0) {
            throw new IllegalArgumentException("Cannot map empty string prefix");
        }
        if (prefix.equals(NO_NS_PREFIX)) {
            throw new IllegalArgumentException("The prefix: " + prefix + " cannot be registered, it's reserved for special internal use.");
        }
        if (this.prefixToNamespaceURILookup.containsKey(prefix)) {
            throw new IllegalArgumentException("The prefix: '" + prefix + "' was repeated. This is illegal.");
        }
        if (this.namespaceURIToPrefixLookup.containsKey(nsURI)) {
            throw new IllegalArgumentException("The namespace URI: " + nsURI + " cannot be mapped to 2 different prefixes.");
        }
        if (prefix.equals(DEFAULT_NAMESPACE_PREFIX)) {
            this.defaultNS = nsURI;
        } else {
            this.prefixToNamespaceURILookup.put(prefix, nsURI);
            this.namespaceURIToPrefixLookup.put(nsURI, prefix);
        }
    }

    public String getDefaultNS() {
        return this.defaultNS;
    }

    public String getNamespaceForPrefix(String prefix) {
        if (prefix.equals("")) {
            return this.defaultNS == null ? "" : this.defaultNS;
        }
        return (String) this.prefixToNamespaceURILookup.get(prefix);
    }

    public String getPrefixForNamespace(String nsURI) {
        if (nsURI == null) {
            return null;
        }
        if (nsURI.length() == 0) {
            return this.defaultNS == null ? "" : NO_NS_PREFIX;
        }
        if (nsURI.equals(this.defaultNS)) {
            return "";
        }
        return (String) this.namespaceURIToPrefixLookup.get(nsURI);
    }

    public String getPrefixedName(String localName, String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            if (this.defaultNS != null) {
                return "N:" + localName;
            }
            return localName;
        }
        if (nsURI.equals(this.defaultNS)) {
            return localName;
        }
        String prefix = getPrefixForNamespace(nsURI);
        if (prefix == null) {
            return null;
        }
        return prefix + ":" + localName;
    }

    @Deprecated
    public TreePath containingElements(int column, int line) {
        TemplateElement elem;
        ArrayList elements = new ArrayList();
        TemplateElement templateElement = this.rootElement;
        loop0: while (true) {
            TemplateElement element = templateElement;
            if (!element.contains(column, line)) {
                break;
            }
            elements.add(element);
            Enumeration enumeration = element.children();
            while (enumeration.hasMoreElements()) {
                elem = (TemplateElement) enumeration.nextElement();
                if (elem.contains(column, line)) {
                    break;
                }
            }
            break loop0;
            templateElement = elem;
        }
        if (elements.isEmpty()) {
            return null;
        }
        return new TreePath(elements.toArray());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Template$WrongEncodingException.class */
    public static class WrongEncodingException extends ParseException {
        private static final long serialVersionUID = 1;

        @Deprecated
        public String specifiedEncoding;
        private final String constructorSpecifiedEncoding;

        @Deprecated
        public WrongEncodingException(String templateSpecifiedEncoding) {
            this(templateSpecifiedEncoding, null);
        }

        public WrongEncodingException(String templateSpecifiedEncoding, String constructorSpecifiedEncoding) {
            this.specifiedEncoding = templateSpecifiedEncoding;
            this.constructorSpecifiedEncoding = constructorSpecifiedEncoding;
        }

        @Override // freemarker.core.ParseException, java.lang.Throwable
        public String getMessage() {
            return "Encoding specified inside the template (" + this.specifiedEncoding + ") doesn't match the encoding specified for the Template constructor" + (this.constructorSpecifiedEncoding != null ? " (" + this.constructorSpecifiedEncoding + ")." : ".");
        }

        public String getTemplateSpecifiedEncoding() {
            return this.specifiedEncoding;
        }

        public String getConstructorSpecifiedEncoding() {
            return this.constructorSpecifiedEncoding;
        }
    }
}
