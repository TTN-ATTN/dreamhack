package freemarker.core;

import freemarker.cache.TemplateNameFormat;
import freemarker.cache._CacheAPI;
import freemarker.core.BodyInstruction;
import freemarker.core.IteratorBlock;
import freemarker.core.Macro;
import freemarker.core.Macro.Context;
import freemarker.core.ReturnInstruction;
import freemarker.ext.beans.BeansWrapper;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._ObjectWrappers;
import freemarker.template._VersionInts;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.NullWriter;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.TemplateModelUtils;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment.class */
public final class Environment extends Configurable {
    private final Configuration configuration;
    private final boolean incompatibleImprovementsGE2328;
    private final TemplateHashModel rootDataModel;
    private TemplateElement[] instructionStack;
    private int instructionStackSize;
    private final ArrayList recoveredErrorStack;
    private TemplateNumberFormat cachedTemplateNumberFormat;
    private Map<String, TemplateNumberFormat> cachedTemplateNumberFormats;
    private TemplateDateFormat[] cachedTempDateFormatArray;
    private HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray;
    private static final int CACHED_TDFS_ZONELESS_INPUT_OFFS = 4;
    private static final int CACHED_TDFS_SQL_D_T_TZ_OFFS = 8;
    private static final int CACHED_TDFS_LENGTH = 16;
    private Boolean cachedSQLDateAndTimeTimeZoneSameAsNormal;

    @Deprecated
    private NumberFormat cNumberFormat;
    private TemplateNumberFormat cTemplateNumberFormat;
    private TemplateNumberFormat cTemplateNumberFormatWithPre2331IcIBug;
    private Configurable trueAndFalseStringsCachedForParent;
    private String cachedTrueString;
    private String cachedFalseString;
    private DateUtil.DateToISO8601CalendarFactory isoBuiltInCalendarFactory;
    private Collator cachedCollator;
    private Writer out;
    private Macro.Context currentMacroContext;
    private LocalContextStack localContextStack;
    private final Namespace mainNamespace;
    private Namespace currentNamespace;
    private Namespace globalNamespace;
    private HashMap<String, Namespace> loadedLibs;
    private Configurable legacyParent;
    private boolean inAttemptBlock;
    private Throwable lastThrowable;
    private TemplateModel lastReturnValue;
    private Map<Object, Namespace> macroToNamespaceLookup;
    private TemplateNodeModel currentVisitorNode;
    private TemplateSequenceModel nodeNamespaces;
    private int nodeNamespaceIndex;
    private String currentNodeName;
    private String currentNodeNS;
    private String cachedURLEscapingCharset;
    private boolean cachedURLEscapingCharsetSet;
    private boolean fastInvalidReferenceExceptions;
    private TemplateProcessingTracer templateProcessingTracer;
    static final String COMPUTER_FORMAT_STRING = "computer";
    private static final int TERSE_MODE_INSTRUCTION_STACK_TRACE_LIMIT = 10;
    private IdentityHashMap<Object, Object> customStateVariables;
    private static final ThreadLocal threadEnv = new ThreadLocal();
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static final Logger ATTEMPT_LOGGER = Logger.getLogger("freemarker.runtime.attempt");
    private static final TemplateModel[] NO_OUT_ARGS = new TemplateModel[0];
    private static final Writer EMPTY_BODY_WRITER = new Writer() { // from class: freemarker.core.Environment.5
        @Override // java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (len > 0) {
                throw new IOException("This transform does not allow nested content.");
            }
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    };

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$InitializationStatus.class */
    private enum InitializationStatus {
        UNINITIALIZED,
        INITIALIZING,
        INITIALIZED,
        FAILED
    }

    public static Environment getCurrentEnvironment() {
        return (Environment) threadEnv.get();
    }

    static void setCurrentEnvironment(Environment env) {
        threadEnv.set(env);
    }

    public Environment(Template template, TemplateHashModel rootDataModel, Writer out) {
        super(template);
        this.instructionStack = new TemplateElement[16];
        this.instructionStackSize = 0;
        this.recoveredErrorStack = new ArrayList();
        this.macroToNamespaceLookup = new IdentityHashMap();
        this.configuration = template.getConfiguration();
        this.incompatibleImprovementsGE2328 = this.configuration.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_28;
        this.globalNamespace = new Namespace(null);
        Namespace namespace = new Namespace(template);
        this.mainNamespace = namespace;
        this.currentNamespace = namespace;
        this.out = out;
        this.rootDataModel = rootDataModel;
        importMacros(template);
    }

    @Deprecated
    public Template getTemplate() {
        return (Template) getParent();
    }

    Template getTemplate230() {
        Template legacyParent = (Template) this.legacyParent;
        return legacyParent != null ? legacyParent : getTemplate();
    }

    public Template getMainTemplate() {
        return this.mainNamespace.getTemplate();
    }

    @SuppressFBWarnings(value = {"RANGE_ARRAY_INDEX"}, justification = "False alarm")
    public Template getCurrentTemplate() {
        int ln = this.instructionStackSize;
        return ln == 0 ? getMainTemplate() : this.instructionStack[ln - 1].getTemplate();
    }

    @SuppressFBWarnings(value = {"RANGE_ARRAY_INDEX"}, justification = "False alarm")
    public DirectiveCallPlace getCurrentDirectiveCallPlace() {
        int ln = this.instructionStackSize;
        if (ln == 0) {
            return null;
        }
        TemplateElement te = this.instructionStack[ln - 1];
        if (te instanceof UnifiedCall) {
            return (UnifiedCall) te;
        }
        if ((te instanceof Macro) && ln > 1 && (this.instructionStack[ln - 2] instanceof UnifiedCall)) {
            return (UnifiedCall) this.instructionStack[ln - 2];
        }
        return null;
    }

    private void clearCachedValues() {
        this.cachedTemplateNumberFormats = null;
        this.cachedTemplateNumberFormat = null;
        this.cachedTempDateFormatArray = null;
        this.cachedTempDateFormatsByFmtStrArray = null;
        this.cachedCollator = null;
        this.cachedURLEscapingCharset = null;
        this.cachedURLEscapingCharsetSet = false;
    }

    public void process() throws TemplateException, IOException {
        Object savedEnv = threadEnv.get();
        threadEnv.set(this);
        try {
            try {
                doAutoImportsAndIncludes(this);
                visit(getTemplate().getRootTreeNode());
                if (getAutoFlush()) {
                    this.out.flush();
                }
                clearCachedValues();
                threadEnv.set(savedEnv);
            } finally {
                clearCachedValues();
            }
        } catch (Throwable th) {
            threadEnv.set(savedEnv);
            throw th;
        }
    }

    void visit(TemplateElement element) throws TemplateException, IOException {
        pushElement(element);
        try {
            try {
                TemplateElement[] templateElementsToVisit = element.accept(this);
                if (templateElementsToVisit != null) {
                    for (TemplateElement el : templateElementsToVisit) {
                        if (el == null) {
                            break;
                        }
                        visit(el);
                    }
                }
            } catch (TemplateException te) {
                handleTemplateException(te);
                popElement();
            }
        } finally {
            popElement();
        }
    }

    final void visit(TemplateElement[] elementBuffer) throws TemplateException, IOException {
        TemplateElement element;
        TemplateElement el;
        if (elementBuffer == null) {
            return;
        }
        int length = elementBuffer.length;
        for (int i = 0; i < length && (element = elementBuffer[i]) != null; i++) {
            pushElement(element);
            try {
                try {
                    TemplateElement[] templateElementsToVisit = element.accept(this);
                    if (templateElementsToVisit != null) {
                        int length2 = templateElementsToVisit.length;
                        for (int i2 = 0; i2 < length2 && (el = templateElementsToVisit[i2]) != null; i2++) {
                            visit(el);
                        }
                    }
                    popElement();
                } catch (TemplateException te) {
                    handleTemplateException(te);
                    popElement();
                }
            } catch (Throwable th) {
                popElement();
                throw th;
            }
        }
    }

    final void visit(TemplateElement[] elementBuffer, Writer out) throws TemplateException, IOException {
        Writer prevOut = this.out;
        this.out = out;
        try {
            visit(elementBuffer);
            this.out = prevOut;
        } catch (Throwable th) {
            this.out = prevOut;
            throw th;
        }
    }

    @SuppressFBWarnings(value = {"RANGE_ARRAY_INDEX"}, justification = "Not called when stack is empty")
    private TemplateElement replaceTopElement(TemplateElement element) {
        this.instructionStack[this.instructionStackSize - 1] = element;
        return element;
    }

    @Deprecated
    public void visit(TemplateElement element, TemplateDirectiveModel directiveModel, Map args, List bodyParameterNames) throws TemplateException, IOException {
        visit(new TemplateElement[]{element}, directiveModel, args, bodyParameterNames);
    }

    void visit(TemplateElement[] childBuffer, TemplateDirectiveModel directiveModel, Map args, final List bodyParameterNames) throws TemplateException, IOException {
        TemplateDirectiveBody nested;
        TemplateModel[] outArgs;
        if (childBuffer == null) {
            nested = null;
        } else {
            nested = new NestedElementTemplateDirectiveBody(childBuffer);
        }
        if (bodyParameterNames == null || bodyParameterNames.isEmpty()) {
            outArgs = NO_OUT_ARGS;
        } else {
            outArgs = new TemplateModel[bodyParameterNames.size()];
        }
        if (outArgs.length > 0) {
            final TemplateModel[] templateModelArr = outArgs;
            pushLocalContext(new LocalContext() { // from class: freemarker.core.Environment.1
                @Override // freemarker.core.LocalContext
                public TemplateModel getLocalVariable(String name) {
                    int index = bodyParameterNames.indexOf(name);
                    if (index != -1) {
                        return templateModelArr[index];
                    }
                    return null;
                }

                @Override // freemarker.core.LocalContext
                public Collection getLocalVariableNames() {
                    return bodyParameterNames;
                }
            });
        }
        try {
            try {
                try {
                    directiveModel.execute(this, args, outArgs, nested);
                    if (outArgs.length > 0) {
                        this.localContextStack.pop();
                    }
                } catch (TemplateException e) {
                    throw e;
                } catch (IOException e2) {
                    throw e2;
                }
            } catch (FlowControlException e3) {
                throw e3;
            } catch (Exception e4) {
                if (EvalUtil.shouldWrapUncheckedException(e4, this)) {
                    throw new _MiscTemplateException(e4, this, "Directive has thrown an unchecked exception; see the cause exception.");
                }
                if (e4 instanceof RuntimeException) {
                    throw ((RuntimeException) e4);
                }
                throw new UndeclaredThrowableException(e4);
            }
        } catch (Throwable th) {
            if (outArgs.length > 0) {
                this.localContextStack.pop();
            }
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:68:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void visitAndTransform(freemarker.core.TemplateElement[] r7, freemarker.template.TemplateTransformModel r8, java.util.Map r9) throws freemarker.template.TemplateException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 268
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.Environment.visitAndTransform(freemarker.core.TemplateElement[], freemarker.template.TemplateTransformModel, java.util.Map):void");
    }

    void visitAttemptRecover(AttemptBlock attemptBlock, TemplateElement attemptedSection, RecoveryBlock recoverySection) throws TemplateException, IOException {
        Writer prevOut = this.out;
        StringWriter sw = new StringWriter();
        this.out = sw;
        TemplateException thrownException = null;
        boolean lastFIRE = setFastInvalidReferenceExceptions(false);
        boolean lastInAttemptBlock = this.inAttemptBlock;
        try {
            this.inAttemptBlock = true;
            visit(attemptedSection);
            this.inAttemptBlock = lastInAttemptBlock;
            setFastInvalidReferenceExceptions(lastFIRE);
            this.out = prevOut;
        } catch (TemplateException te) {
            thrownException = te;
            this.inAttemptBlock = lastInAttemptBlock;
            setFastInvalidReferenceExceptions(lastFIRE);
            this.out = prevOut;
        } catch (Throwable th) {
            this.inAttemptBlock = lastInAttemptBlock;
            setFastInvalidReferenceExceptions(lastFIRE);
            this.out = prevOut;
            throw th;
        }
        if (thrownException != null) {
            if (ATTEMPT_LOGGER.isDebugEnabled()) {
                ATTEMPT_LOGGER.debug("Error in attempt block " + attemptBlock.getStartLocationQuoted(), thrownException);
            }
            try {
                this.recoveredErrorStack.add(thrownException);
                visit(recoverySection);
                this.recoveredErrorStack.remove(this.recoveredErrorStack.size() - 1);
                return;
            } catch (Throwable th2) {
                this.recoveredErrorStack.remove(this.recoveredErrorStack.size() - 1);
                throw th2;
            }
        }
        this.out.write(sw.toString());
    }

    String getCurrentRecoveredErrorMessage() throws TemplateException {
        if (this.recoveredErrorStack.isEmpty()) {
            throw new _MiscTemplateException(this, ".error is not available outside of a #recover block");
        }
        return ((Throwable) this.recoveredErrorStack.get(this.recoveredErrorStack.size() - 1)).getMessage();
    }

    public boolean isInAttemptBlock() {
        return this.inAttemptBlock;
    }

    void invokeNestedContent(BodyInstruction.Context bodyCtx) throws TemplateException, IOException {
        Macro.Context invokingMacroContext = getCurrentMacroContext();
        LocalContextStack prevLocalContextStack = this.localContextStack;
        TemplateObject callPlace = invokingMacroContext.callPlace;
        TemplateElement[] nestedContentBuffer = callPlace instanceof TemplateElement ? ((TemplateElement) callPlace).getChildBuffer() : null;
        if (nestedContentBuffer != null) {
            this.currentMacroContext = invokingMacroContext.prevMacroContext;
            this.currentNamespace = invokingMacroContext.nestedContentNamespace;
            boolean parentReplacementOn = isBeforeIcI2322();
            Configurable prevParent = getParent();
            if (parentReplacementOn) {
                setParent(this.currentNamespace.getTemplate());
            } else {
                this.legacyParent = this.currentNamespace.getTemplate();
            }
            this.localContextStack = invokingMacroContext.prevLocalContextStack;
            if (invokingMacroContext.nestedContentParameterNames != null) {
                pushLocalContext(bodyCtx);
            }
            try {
                visit(nestedContentBuffer);
                if (invokingMacroContext.nestedContentParameterNames != null) {
                    this.localContextStack.pop();
                }
                this.currentMacroContext = invokingMacroContext;
                this.currentNamespace = getMacroNamespace(invokingMacroContext.getMacro());
                if (parentReplacementOn) {
                    setParent(prevParent);
                } else {
                    this.legacyParent = prevParent;
                }
                this.localContextStack = prevLocalContextStack;
            } catch (Throwable th) {
                if (invokingMacroContext.nestedContentParameterNames != null) {
                    this.localContextStack.pop();
                }
                this.currentMacroContext = invokingMacroContext;
                this.currentNamespace = getMacroNamespace(invokingMacroContext.getMacro());
                if (parentReplacementOn) {
                    setParent(prevParent);
                } else {
                    this.legacyParent = prevParent;
                }
                this.localContextStack = prevLocalContextStack;
                throw th;
            }
        }
    }

    boolean visitIteratorBlock(IteratorBlock.IterationContext ictxt) throws TemplateException, IOException {
        pushLocalContext(ictxt);
        try {
            try {
                boolean zAccept = ictxt.accept(this);
                this.localContextStack.pop();
                return zAccept;
            } catch (TemplateException te) {
                handleTemplateException(te);
                this.localContextStack.pop();
                return true;
            }
        } catch (Throwable th) {
            this.localContextStack.pop();
            throw th;
        }
    }

    IteratorBlock.IterationContext findEnclosingIterationContextWithVisibleVariable(String loopVarName) {
        return findEnclosingIterationContext(loopVarName);
    }

    IteratorBlock.IterationContext findClosestEnclosingIterationContext() {
        return findEnclosingIterationContext(null);
    }

    private IteratorBlock.IterationContext findEnclosingIterationContext(String visibleLoopVarName) {
        LocalContextStack ctxStack = getLocalContextStack();
        if (ctxStack != null) {
            for (int i = ctxStack.size() - 1; i >= 0; i--) {
                Object ctx = ctxStack.get(i);
                if ((ctx instanceof IteratorBlock.IterationContext) && (visibleLoopVarName == null || ((IteratorBlock.IterationContext) ctx).hasVisibleLoopVar(visibleLoopVarName))) {
                    return (IteratorBlock.IterationContext) ctx;
                }
            }
            return null;
        }
        return null;
    }

    TemplateModel evaluateWithNewLocal(Expression exp, String lambdaArgName, TemplateModel lamdaArgValue) throws TemplateException {
        pushLocalContext(new LocalContextWithNewLocal(lambdaArgName, lamdaArgValue));
        try {
            TemplateModel templateModelEval = exp.eval(this);
            this.localContextStack.pop();
            return templateModelEval;
        } catch (Throwable th) {
            this.localContextStack.pop();
            throw th;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$LocalContextWithNewLocal.class */
    private static class LocalContextWithNewLocal implements LocalContext {
        private final String lambdaArgName;
        private final TemplateModel lambdaArgValue;

        public LocalContextWithNewLocal(String lambdaArgName, TemplateModel lambdaArgValue) {
            this.lambdaArgName = lambdaArgName;
            this.lambdaArgValue = lambdaArgValue;
        }

        @Override // freemarker.core.LocalContext
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            if (name.equals(this.lambdaArgName)) {
                return this.lambdaArgValue;
            }
            return null;
        }

        @Override // freemarker.core.LocalContext
        public Collection getLocalVariableNames() throws TemplateModelException {
            return Collections.singleton(this.lambdaArgName);
        }
    }

    void invokeNodeHandlerFor(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
        if (this.nodeNamespaces == null) {
            SimpleSequence ss = new SimpleSequence(1, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
            ss.add(this.currentNamespace);
            this.nodeNamespaces = ss;
        }
        int prevNodeNamespaceIndex = this.nodeNamespaceIndex;
        String prevNodeName = this.currentNodeName;
        String prevNodeNS = this.currentNodeNS;
        TemplateSequenceModel prevNodeNamespaces = this.nodeNamespaces;
        TemplateNodeModel prevVisitorNode = this.currentVisitorNode;
        this.currentVisitorNode = node;
        if (namespaces != null) {
            this.nodeNamespaces = namespaces;
        }
        try {
            TemplateModel macroOrTransform = getNodeProcessor(node);
            if (macroOrTransform instanceof Macro) {
                invokeMacro((Macro) macroOrTransform, null, null, null, null);
            } else if (macroOrTransform instanceof TemplateTransformModel) {
                visitAndTransform(null, (TemplateTransformModel) macroOrTransform, null);
            } else {
                String nodeType = node.getNodeType();
                if (nodeType != null) {
                    if (nodeType.equals("text") && (node instanceof TemplateScalarModel)) {
                        this.out.write(((TemplateScalarModel) node).getAsString());
                    } else if (nodeType.equals("document")) {
                        recurse(node, namespaces);
                    } else if (!nodeType.equals("pi") && !nodeType.equals("comment") && !nodeType.equals("document_type")) {
                        throw new _MiscTemplateException(this, noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), nodeType));
                    }
                } else {
                    throw new _MiscTemplateException(this, noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), "default"));
                }
            }
        } finally {
            this.currentVisitorNode = prevVisitorNode;
            this.nodeNamespaceIndex = prevNodeNamespaceIndex;
            this.currentNodeName = prevNodeName;
            this.currentNodeNS = prevNodeNS;
            this.nodeNamespaces = prevNodeNamespaces;
        }
    }

    private Object[] noNodeHandlerDefinedDescription(TemplateNodeModel node, String ns, String nodeType) throws TemplateModelException {
        String nsPrefix;
        if (ns != null) {
            if (ns.length() > 0) {
                nsPrefix = " and namespace ";
            } else {
                nsPrefix = " and no namespace";
            }
        } else {
            nsPrefix = "";
            ns = "";
        }
        return new Object[]{"No macro or directive is defined for node named ", new _DelayedJQuote(node.getNodeName()), nsPrefix, ns, ", and there is no fallback handler called @", nodeType, " either."};
    }

    void fallback() throws TemplateException, IOException {
        TemplateModel macroOrTransform = getNodeProcessor(this.currentNodeName, this.currentNodeNS, this.nodeNamespaceIndex);
        if (macroOrTransform instanceof Macro) {
            invokeMacro((Macro) macroOrTransform, null, null, null, null);
        } else if (macroOrTransform instanceof TemplateTransformModel) {
            visitAndTransform(null, (TemplateTransformModel) macroOrTransform, null);
        }
    }

    void invokeMacro(Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
        invokeMacroOrFunctionCommonPart(macro, namedArgs, positionalArgs, bodyParameterNames, callPlace);
    }

    TemplateModel invokeFunction(Environment env, Macro func, List<? extends Expression> argumentExps, TemplateObject callPlace) throws TemplateException {
        env.setLastReturnValue(null);
        if (!func.isFunction()) {
            throw new _MiscTemplateException(env, "A macro cannot be called in an expression. (Functions can be.)");
        }
        Writer prevOut = env.getOut();
        try {
            try {
                env.setOut(NullWriter.INSTANCE);
                env.invokeMacro(func, null, argumentExps, null, callPlace);
                env.setOut(prevOut);
                return env.getLastReturnValue();
            } catch (IOException e) {
                throw new TemplateException("Unexpected exception during function execution", (Exception) e, env);
            }
        } catch (Throwable th) {
            env.setOut(prevOut);
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    private void invokeMacroOrFunctionCommonPart(Macro macroOrFunction, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
        boolean elementPushed;
        if (macroOrFunction == Macro.DO_NOTHING_MACRO) {
            return;
        }
        if (this.incompatibleImprovementsGE2328) {
            elementPushed = false;
        } else {
            pushElement(macroOrFunction);
            elementPushed = true;
        }
        try {
            macroOrFunction.getClass();
            Macro.Context macroCtx = macroOrFunction.new Context(this, callPlace, bodyParameterNames);
            setMacroContextLocalsFromArguments(macroCtx, macroOrFunction, namedArgs, positionalArgs);
            if (!elementPushed) {
                pushElement(macroOrFunction);
                elementPushed = true;
            }
            Macro.Context prevMacroCtx = this.currentMacroContext;
            this.currentMacroContext = macroCtx;
            LocalContextStack prevLocalContextStack = this.localContextStack;
            this.localContextStack = null;
            Namespace prevNamespace = this.currentNamespace;
            this.currentNamespace = getMacroNamespace(macroOrFunction);
            try {
                try {
                    macroCtx.checkParamsSetAndApplyDefaults(this);
                    visit(macroOrFunction.getChildBuffer());
                    this.currentMacroContext = prevMacroCtx;
                    this.localContextStack = prevLocalContextStack;
                    this.currentNamespace = prevNamespace;
                } catch (Throwable th) {
                    this.currentMacroContext = prevMacroCtx;
                    this.localContextStack = prevLocalContextStack;
                    this.currentNamespace = prevNamespace;
                    throw th;
                }
            } catch (ReturnInstruction.Return e) {
                this.currentMacroContext = prevMacroCtx;
                this.localContextStack = prevLocalContextStack;
                this.currentNamespace = prevNamespace;
            } catch (TemplateException te) {
                handleTemplateException(te);
                this.currentMacroContext = prevMacroCtx;
                this.localContextStack = prevLocalContextStack;
                this.currentNamespace = prevNamespace;
            }
        } finally {
            if (elementPushed) {
                popElement();
            }
        }
    }

    private void setMacroContextLocalsFromArguments(Macro.Context macroCtx, Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs) throws TemplateException {
        String catchAllParamName = macro.getCatchAll();
        SimpleHash namedCatchAllParamValue = null;
        SimpleSequence positionalCatchAllParamValue = null;
        int nextPositionalArgToAssignIdx = 0;
        WithArgsState withArgsState = getWithArgState(macro);
        if (withArgsState != null) {
            TemplateHashModelEx byNameWithArgs = withArgsState.byName;
            TemplateSequenceModel byPositionWithArgs = withArgsState.byPosition;
            if (byNameWithArgs != null) {
                TemplateHashModelEx2.KeyValuePairIterator withArgsKVPIter = TemplateModelUtils.getKeyValuePairIterator(byNameWithArgs);
                while (withArgsKVPIter.hasNext()) {
                    TemplateHashModelEx2.KeyValuePair withArgKVP = withArgsKVPIter.next();
                    TemplateModel argNameTM = withArgKVP.getKey();
                    if (!(argNameTM instanceof TemplateScalarModel)) {
                        throw new _TemplateModelException("Expected string keys in the \"with args\" hash, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), ".");
                    }
                    String argName = EvalUtil.modelToString((TemplateScalarModel) argNameTM, null, null);
                    TemplateModel argValue = withArgKVP.getValue();
                    if (macro.hasArgNamed(argName)) {
                        macroCtx.setLocalVar(argName, argValue);
                    } else if (catchAllParamName != null) {
                        if (namedCatchAllParamValue == null) {
                            namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
                        }
                        if (!withArgsState.orderLast) {
                            namedCatchAllParamValue.put(argName, argValue);
                        } else {
                            List<NameValuePair> orderLastByNameCatchAll = withArgsState.orderLastByNameCatchAll;
                            if (orderLastByNameCatchAll == null) {
                                orderLastByNameCatchAll = new ArrayList<>();
                                withArgsState.orderLastByNameCatchAll = orderLastByNameCatchAll;
                            }
                            orderLastByNameCatchAll.add(new NameValuePair(argName, argValue));
                        }
                    } else {
                        throw newUndeclaredParamNameException(macro, argName);
                    }
                }
            } else if (byPositionWithArgs != null) {
                if (!withArgsState.orderLast) {
                    String[] argNames = macro.getArgumentNamesNoCopy();
                    int argsCnt = byPositionWithArgs.size();
                    if (argNames.length < argsCnt && catchAllParamName == null) {
                        throw newTooManyArgumentsException(macro, argNames, argsCnt);
                    }
                    for (int argIdx = 0; argIdx < argsCnt; argIdx++) {
                        TemplateModel argValue2 = byPositionWithArgs.get(argIdx);
                        try {
                            if (nextPositionalArgToAssignIdx < argNames.length) {
                                int i = nextPositionalArgToAssignIdx;
                                nextPositionalArgToAssignIdx++;
                                String argName2 = argNames[i];
                                macroCtx.setLocalVar(argName2, argValue2);
                            } else {
                                if (positionalCatchAllParamValue == null) {
                                    positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                                }
                                positionalCatchAllParamValue.add(argValue2);
                            }
                        } catch (RuntimeException re) {
                            throw new _MiscTemplateException(re, this);
                        }
                    }
                } else {
                    if (namedArgs != null && !namedArgs.isEmpty() && byPositionWithArgs.size() != 0) {
                        throw new _MiscTemplateException("Call can't pass parameters by name, as there's \"with args last\" in effect that specifies parameters by position.");
                    }
                    if (catchAllParamName == null) {
                        int totalPositionalArgCnt = (positionalArgs != null ? positionalArgs.size() : 0) + byPositionWithArgs.size();
                        if (totalPositionalArgCnt > macro.getArgumentNamesNoCopy().length) {
                            throw newTooManyArgumentsException(macro, macro.getArgumentNamesNoCopy(), totalPositionalArgCnt);
                        }
                    }
                }
            }
        }
        if (namedArgs != null) {
            if (catchAllParamName != null && namedCatchAllParamValue == null && positionalCatchAllParamValue == null) {
                if (namedArgs.isEmpty() && withArgsState != null && withArgsState.byPosition != null) {
                    positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                } else {
                    namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
                }
            }
            for (Map.Entry<String, ? extends Expression> argNameAndValExp : namedArgs.entrySet()) {
                String argName3 = argNameAndValExp.getKey();
                boolean isArgNameDeclared = macro.hasArgNamed(argName3);
                if (isArgNameDeclared || namedCatchAllParamValue != null) {
                    Expression argValueExp = argNameAndValExp.getValue();
                    TemplateModel argValue3 = argValueExp.eval(this);
                    if (isArgNameDeclared) {
                        macroCtx.setLocalVar(argName3, argValue3);
                    } else {
                        namedCatchAllParamValue.put(argName3, argValue3);
                    }
                } else {
                    if (positionalCatchAllParamValue != null) {
                        throw newBothNamedAndPositionalCatchAllParamsException(macro);
                    }
                    throw newUndeclaredParamNameException(macro, argName3);
                }
            }
        } else if (positionalArgs != null) {
            if (catchAllParamName != null && positionalCatchAllParamValue == null && namedCatchAllParamValue == null) {
                if (positionalArgs.isEmpty() && withArgsState != null && withArgsState.byName != null) {
                    namedCatchAllParamValue = initNamedCatchAllParameter(macroCtx, catchAllParamName);
                } else {
                    positionalCatchAllParamValue = initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                }
            }
            String[] argNames2 = macro.getArgumentNamesNoCopy();
            int argsCnt2 = positionalArgs.size();
            int argsWithWithArgsCnt = argsCnt2 + nextPositionalArgToAssignIdx;
            if (argNames2.length < argsWithWithArgsCnt && positionalCatchAllParamValue == null) {
                if (namedCatchAllParamValue != null) {
                    throw newBothNamedAndPositionalCatchAllParamsException(macro);
                }
                throw newTooManyArgumentsException(macro, argNames2, argsWithWithArgsCnt);
            }
            for (int srcPosArgIdx = 0; srcPosArgIdx < argsCnt2; srcPosArgIdx++) {
                Expression argValueExp2 = positionalArgs.get(srcPosArgIdx);
                try {
                    TemplateModel argValue4 = argValueExp2.eval(this);
                    if (nextPositionalArgToAssignIdx < argNames2.length) {
                        int i2 = nextPositionalArgToAssignIdx;
                        nextPositionalArgToAssignIdx++;
                        String argName4 = argNames2[i2];
                        macroCtx.setLocalVar(argName4, argValue4);
                    } else {
                        positionalCatchAllParamValue.add(argValue4);
                    }
                } catch (RuntimeException e) {
                    throw new _MiscTemplateException(e, this);
                }
            }
        }
        if (withArgsState == null || !withArgsState.orderLast) {
            return;
        }
        if (withArgsState.orderLastByNameCatchAll != null) {
            for (NameValuePair nameValuePair : withArgsState.orderLastByNameCatchAll) {
                if (!namedCatchAllParamValue.containsKey(nameValuePair.name)) {
                    namedCatchAllParamValue.put(nameValuePair.name, nameValuePair.value);
                }
            }
            return;
        }
        if (withArgsState.byPosition == null) {
            return;
        }
        TemplateSequenceModel byPosition = withArgsState.byPosition;
        int withArgCnt = byPosition.size();
        String[] argNames3 = macro.getArgumentNamesNoCopy();
        for (int withArgIdx = 0; withArgIdx < withArgCnt; withArgIdx++) {
            TemplateModel withArgValue = byPosition.get(withArgIdx);
            if (nextPositionalArgToAssignIdx < argNames3.length) {
                int i3 = nextPositionalArgToAssignIdx;
                nextPositionalArgToAssignIdx++;
                String argName5 = argNames3[i3];
                macroCtx.setLocalVar(argName5, withArgValue);
            } else {
                positionalCatchAllParamValue.add(withArgValue);
            }
        }
    }

    private static WithArgsState getWithArgState(Macro macro) {
        Macro.WithArgs withArgs = macro.getWithArgs();
        if (withArgs == null) {
            return null;
        }
        return new WithArgsState(withArgs.getByName(), withArgs.getByPosition(), withArgs.isOrderLast());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$WithArgsState.class */
    private static final class WithArgsState {
        private final TemplateHashModelEx byName;
        private final TemplateSequenceModel byPosition;
        private final boolean orderLast;
        private List<NameValuePair> orderLastByNameCatchAll;

        public WithArgsState(TemplateHashModelEx byName, TemplateSequenceModel byPosition, boolean orderLast) {
            this.byName = byName;
            this.byPosition = byPosition;
            this.orderLast = orderLast;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$NameValuePair.class */
    private static final class NameValuePair {
        private final String name;
        private final TemplateModel value;

        public NameValuePair(String name, TemplateModel value) {
            this.name = name;
            this.value = value;
        }
    }

    private _MiscTemplateException newTooManyArgumentsException(Macro macro, String[] argNames, int argsCnt) {
        Object[] objArr = new Object[7];
        objArr[0] = macro.isFunction() ? "Function " : "Macro ";
        objArr[1] = new _DelayedJQuote(macro.getName());
        objArr[2] = " only accepts ";
        objArr[3] = new _DelayedToString(argNames.length);
        objArr[4] = " parameters, but got ";
        objArr[5] = new _DelayedToString(argsCnt);
        objArr[6] = ".";
        return new _MiscTemplateException(this, objArr);
    }

    private static SimpleSequence initPositionalCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
        SimpleSequence positionalCatchAllParamValue = new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        macroCtx.setLocalVar(catchAllParamName, positionalCatchAllParamValue);
        return positionalCatchAllParamValue;
    }

    private static SimpleHash initNamedCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
        SimpleHash namedCatchAllParamValue = new SimpleHash(new LinkedHashMap(), _ObjectWrappers.SAFE_OBJECT_WRAPPER, 0);
        macroCtx.setLocalVar(catchAllParamName, namedCatchAllParamValue);
        return namedCatchAllParamValue;
    }

    private _MiscTemplateException newUndeclaredParamNameException(Macro macro, String argName) {
        Object[] objArr = new Object[6];
        objArr[0] = macro.isFunction() ? "Function " : "Macro ";
        objArr[1] = new _DelayedJQuote(macro.getName());
        objArr[2] = " has no parameter with name ";
        objArr[3] = new _DelayedJQuote(argName);
        objArr[4] = ". Valid parameter names are: ";
        objArr[5] = new _DelayedJoinWithComma(macro.getArgumentNamesNoCopy());
        return new _MiscTemplateException(this, objArr);
    }

    private _MiscTemplateException newBothNamedAndPositionalCatchAllParamsException(Macro macro) {
        Object[] objArr = new Object[3];
        objArr[0] = macro.isFunction() ? "Function " : "Macro ";
        objArr[1] = new _DelayedJQuote(macro.getName());
        objArr[2] = " call can't have both named and positional arguments that has to go into catch-all parameter.";
        return new _MiscTemplateException(this, objArr);
    }

    void visitMacroDef(Macro macro) {
        this.macroToNamespaceLookup.put(macro.getNamespaceLookupKey(), this.currentNamespace);
        this.currentNamespace.put(macro.getName(), macro);
    }

    Namespace getMacroNamespace(Macro macro) {
        return this.macroToNamespaceLookup.get(macro.getNamespaceLookupKey());
    }

    void recurse(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
        if (node == null) {
            node = getCurrentVisitorNode();
            if (node == null) {
                throw new _TemplateModelException("The target node of recursion is missing or null.");
            }
        }
        TemplateSequenceModel children = node.getChildNodes();
        if (children == null) {
            return;
        }
        int size = children.size();
        for (int i = 0; i < size; i++) {
            TemplateNodeModel child = (TemplateNodeModel) children.get(i);
            if (child != null) {
                invokeNodeHandlerFor(child, namespaces);
            }
        }
    }

    Macro.Context getCurrentMacroContext() {
        return this.currentMacroContext;
    }

    private void handleTemplateException(TemplateException templateException) throws TemplateException {
        if ((templateException instanceof TemplateModelException) && ((TemplateModelException) templateException).getReplaceWithCause() && (templateException.getCause() instanceof TemplateException)) {
            templateException = (TemplateException) templateException.getCause();
        }
        if (this.lastThrowable == templateException) {
            throw templateException;
        }
        this.lastThrowable = templateException;
        if (getLogTemplateExceptions() && LOG.isErrorEnabled() && !isInAttemptBlock()) {
            LOG.error("Error executing FreeMarker template", templateException);
        }
        try {
            if (templateException instanceof StopException) {
                throw templateException;
            }
            getTemplateExceptionHandler().handleTemplateException(templateException, this, this.out);
        } catch (TemplateException e) {
            if (isInAttemptBlock()) {
                getAttemptExceptionReporter().report(templateException, this);
            }
            throw e;
        }
    }

    @Override // freemarker.core.Configurable
    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        super.setTemplateExceptionHandler(templateExceptionHandler);
        this.lastThrowable = null;
    }

    @Override // freemarker.core.Configurable
    public void setLocale(Locale locale) {
        Locale prevLocale = getLocale();
        super.setLocale(locale);
        if (!locale.equals(prevLocale)) {
            this.cachedTemplateNumberFormats = null;
            if (this.cachedTemplateNumberFormat != null && this.cachedTemplateNumberFormat.isLocaleBound()) {
                this.cachedTemplateNumberFormat = null;
            }
            if (this.cachedTempDateFormatArray != null) {
                for (int i = 0; i < 16; i++) {
                    TemplateDateFormat f = this.cachedTempDateFormatArray[i];
                    if (f != null && f.isLocaleBound()) {
                        this.cachedTempDateFormatArray[i] = null;
                    }
                }
            }
            this.cachedTempDateFormatsByFmtStrArray = null;
            this.cachedCollator = null;
        }
    }

    @Override // freemarker.core.Configurable
    public void setTimeZone(TimeZone timeZone) {
        TimeZone prevTimeZone = getTimeZone();
        super.setTimeZone(timeZone);
        if (!timeZone.equals(prevTimeZone)) {
            if (this.cachedTempDateFormatArray != null) {
                for (int i = 0; i < 8; i++) {
                    TemplateDateFormat f = this.cachedTempDateFormatArray[i];
                    if (f != null && f.isTimeZoneBound()) {
                        this.cachedTempDateFormatArray[i] = null;
                    }
                }
            }
            if (this.cachedTempDateFormatsByFmtStrArray != null) {
                for (int i2 = 0; i2 < 8; i2++) {
                    this.cachedTempDateFormatsByFmtStrArray[i2] = null;
                }
            }
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
        }
    }

    @Override // freemarker.core.Configurable
    public void setSQLDateAndTimeTimeZone(TimeZone timeZone) {
        TimeZone prevTimeZone = getSQLDateAndTimeTimeZone();
        super.setSQLDateAndTimeTimeZone(timeZone);
        if (!nullSafeEquals(timeZone, prevTimeZone)) {
            if (this.cachedTempDateFormatArray != null) {
                for (int i = 8; i < 16; i++) {
                    TemplateDateFormat format = this.cachedTempDateFormatArray[i];
                    if (format != null && format.isTimeZoneBound()) {
                        this.cachedTempDateFormatArray[i] = null;
                    }
                }
            }
            if (this.cachedTempDateFormatsByFmtStrArray != null) {
                for (int i2 = 8; i2 < 16; i2++) {
                    this.cachedTempDateFormatsByFmtStrArray[i2] = null;
                }
            }
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
        }
    }

    private static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    boolean isSQLDateAndTimeTimeZoneSameAsNormal() {
        if (this.cachedSQLDateAndTimeTimeZoneSameAsNormal == null) {
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = Boolean.valueOf(getSQLDateAndTimeTimeZone() == null || getSQLDateAndTimeTimeZone().equals(getTimeZone()));
        }
        return this.cachedSQLDateAndTimeTimeZoneSameAsNormal.booleanValue();
    }

    @Override // freemarker.core.Configurable
    public void setURLEscapingCharset(String urlEscapingCharset) {
        this.cachedURLEscapingCharsetSet = false;
        super.setURLEscapingCharset(urlEscapingCharset);
    }

    @Override // freemarker.core.Configurable
    public void setOutputEncoding(String outputEncoding) {
        this.cachedURLEscapingCharsetSet = false;
        super.setOutputEncoding(outputEncoding);
    }

    String getEffectiveURLEscapingCharset() {
        if (!this.cachedURLEscapingCharsetSet) {
            this.cachedURLEscapingCharset = getURLEscapingCharset();
            if (this.cachedURLEscapingCharset == null) {
                this.cachedURLEscapingCharset = getOutputEncoding();
            }
            this.cachedURLEscapingCharsetSet = true;
        }
        return this.cachedURLEscapingCharset;
    }

    Collator getCollator() {
        if (this.cachedCollator == null) {
            this.cachedCollator = Collator.getInstance(getLocale());
        }
        return this.cachedCollator;
    }

    public boolean applyEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 1, rightValue, this);
    }

    public boolean applyEqualsOperatorLenient(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compareLenient(leftValue, 1, rightValue, this);
    }

    public boolean applyLessThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 3, rightValue, this);
    }

    public boolean applyLessThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 5, rightValue, this);
    }

    public boolean applyGreaterThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 4, rightValue, this);
    }

    public boolean applyWithGreaterThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 6, rightValue, this);
    }

    public void setOut(Writer out) {
        this.out = out;
    }

    public Writer getOut() {
        return this.out;
    }

    @Override // freemarker.core.Configurable
    public void setNumberFormat(String formatName) {
        super.setNumberFormat(formatName);
        this.cachedTemplateNumberFormat = null;
    }

    String formatNumberToPlainText(TemplateNumberModel number, Expression exp, boolean useTempModelExc) throws TemplateException {
        return formatNumberToPlainText(number, getTemplateNumberFormat(exp, useTempModelExc), exp, useTempModelExc);
    }

    String formatNumberToPlainText(TemplateNumberModel number, TemplateNumberFormat format, Expression exp, boolean useTempModelExc) throws TemplateException {
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(number));
        } catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatNumberException(format, exp, e, useTempModelExc);
        }
    }

    String formatNumberToPlainText(Number number, BackwardCompatibleTemplateNumberFormat format, Expression exp) throws _MiscTemplateException, TemplateModelException {
        try {
            return format.format(number);
        } catch (UnformattableValueException e) {
            throw new _MiscTemplateException(exp, e, this, "Failed to format number with ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage());
        }
    }

    public TemplateNumberFormat getTemplateNumberFormat() throws TemplateValueFormatException {
        TemplateNumberFormat format = this.cachedTemplateNumberFormat;
        if (format == null) {
            format = getTemplateNumberFormat(getNumberFormat(), false);
            this.cachedTemplateNumberFormat = format;
        }
        return format;
    }

    public TemplateNumberFormat getTemplateNumberFormat(String formatString) throws TemplateValueFormatException {
        return getTemplateNumberFormat(formatString, true);
    }

    public TemplateNumberFormat getTemplateNumberFormat(String formatString, Locale locale) throws TemplateValueFormatException {
        if (locale.equals(getLocale())) {
            getTemplateNumberFormat(formatString);
        }
        return getTemplateNumberFormatWithoutCache(formatString, locale);
    }

    TemplateNumberFormat getTemplateNumberFormat(Expression exp, boolean useTempModelExc) throws TemplateException {
        try {
            TemplateNumberFormat format = getTemplateNumberFormat();
            return format;
        } catch (TemplateValueFormatException e) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to get number format object for the current number format string, ", new _DelayedJQuote(getNumberFormat()), ": ", e.getMessage()).blame(exp);
            if (useTempModelExc) {
                throw new _TemplateModelException(e, this, desc);
            }
            throw new _MiscTemplateException(e, this, desc);
        }
    }

    TemplateNumberFormat getTemplateNumberFormat(String formatString, Expression exp, boolean useTempModelExc) throws TemplateException {
        try {
            TemplateNumberFormat format = getTemplateNumberFormat(formatString);
            return format;
        } catch (TemplateValueFormatException e) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to get number format object for the ", new _DelayedJQuote(formatString), " number format string: ", e.getMessage()).blame(exp);
            if (useTempModelExc) {
                throw new _TemplateModelException(e, this, desc);
            }
            throw new _MiscTemplateException(e, this, desc);
        }
    }

    private TemplateNumberFormat getTemplateNumberFormat(String formatString, boolean cacheResult) throws TemplateValueFormatException {
        if (this.cachedTemplateNumberFormats == null) {
            if (cacheResult) {
                this.cachedTemplateNumberFormats = new HashMap();
            }
        } else {
            TemplateNumberFormat format = this.cachedTemplateNumberFormats.get(formatString);
            if (format != null) {
                return format;
            }
        }
        TemplateNumberFormat format2 = getTemplateNumberFormatWithoutCache(formatString, getLocale());
        if (cacheResult) {
            this.cachedTemplateNumberFormats.put(formatString, format2);
        }
        return format2;
    }

    private TemplateNumberFormat getTemplateNumberFormatWithoutCache(String formatString, Locale locale) throws TemplateValueFormatException {
        char c;
        int formatStringLen = formatString.length();
        if (formatStringLen > 1 && formatString.charAt(0) == '@' && ((isIcI2324OrLater() || hasCustomFormats()) && Character.isLetter(formatString.charAt(1)))) {
            int endIdx = 1;
            while (endIdx < formatStringLen && (c = formatString.charAt(endIdx)) != ' ' && c != '_') {
                endIdx++;
            }
            String name = formatString.substring(1, endIdx);
            String params = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
            TemplateNumberFormatFactory formatFactory = getCustomNumberFormat(name);
            if (formatFactory == null) {
                throw new UndefinedCustomFormatException("No custom number format was defined with name " + StringUtil.jQuote(name));
            }
            return formatFactory.get(params, locale, this);
        }
        if (formatStringLen >= 1 && formatString.charAt(0) == 'c' && (formatStringLen == 1 || formatString.equals(COMPUTER_FORMAT_STRING))) {
            return getCTemplateNumberFormatWithPre2331IcIBug();
        }
        return JavaTemplateNumberFormatFactory.INSTANCE.get(formatString, locale, this);
    }

    @Deprecated
    public NumberFormat getCNumberFormat() {
        if (this.cNumberFormat == null) {
            CFormat cFormat = getCFormat();
            if (cFormat == LegacyCFormat.INSTANCE && this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_31) {
                this.cNumberFormat = ((LegacyCFormat) cFormat).getLegacyNumberFormat(_VersionInts.V_2_3_20);
            } else {
                this.cNumberFormat = cFormat.getLegacyNumberFormat(this);
            }
        }
        return this.cNumberFormat;
    }

    public TemplateNumberFormat getCTemplateNumberFormat() {
        if (this.cTemplateNumberFormat == null) {
            this.cTemplateNumberFormat = getCFormat().getTemplateNumberFormat(this);
        }
        return this.cTemplateNumberFormat;
    }

    private TemplateNumberFormat getCTemplateNumberFormatWithPre2331IcIBug() {
        if (this.cTemplateNumberFormatWithPre2331IcIBug == null) {
            CFormat cFormat = getCFormat();
            if (cFormat == LegacyCFormat.INSTANCE && this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_31) {
                this.cTemplateNumberFormatWithPre2331IcIBug = ((LegacyCFormat) cFormat).getTemplateNumberFormat(_VersionInts.V_2_3_20);
            } else {
                this.cTemplateNumberFormatWithPre2331IcIBug = cFormat.getTemplateNumberFormat(this);
            }
        }
        return this.cTemplateNumberFormatWithPre2331IcIBug;
    }

    @Override // freemarker.core.Configurable
    public void setCFormat(CFormat cFormat) {
        CFormat prevCFormat = getCFormat();
        super.setCFormat(cFormat);
        if (prevCFormat != cFormat) {
            this.cTemplateNumberFormat = null;
            this.cTemplateNumberFormatWithPre2331IcIBug = null;
            this.cNumberFormat = null;
            if (this.cachedTemplateNumberFormats != null) {
                this.cachedTemplateNumberFormats.remove("c");
                this.cachedTemplateNumberFormats.remove(COMPUTER_FORMAT_STRING);
            }
            clearCachedTrueAndFalseString();
        }
    }

    @Override // freemarker.core.Configurable
    public void setTimeFormat(String timeFormat) {
        String prevTimeFormat = getTimeFormat();
        super.setTimeFormat(timeFormat);
        if (!timeFormat.equals(prevTimeFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 1] = null;
            }
        }
    }

    @Override // freemarker.core.Configurable
    public void setDateFormat(String dateFormat) {
        String prevDateFormat = getDateFormat();
        super.setDateFormat(dateFormat);
        if (!dateFormat.equals(prevDateFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 2] = null;
            }
        }
    }

    @Override // freemarker.core.Configurable
    public void setDateTimeFormat(String dateTimeFormat) {
        String prevDateTimeFormat = getDateTimeFormat();
        super.setDateTimeFormat(dateTimeFormat);
        if (!dateTimeFormat.equals(prevDateTimeFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 3] = null;
            }
        }
    }

    @Override // freemarker.core.Configurable
    public void setBooleanFormat(String booleanFormat) {
        super.setBooleanFormat(booleanFormat);
        clearCachedTrueAndFalseString();
    }

    String formatBoolean(boolean value, boolean fallbackToTrueFalse) throws TemplateException {
        if (value) {
            String s = getTrueStringValue();
            if (s == null) {
                if (fallbackToTrueFalse) {
                    return "true";
                }
                throw new _MiscTemplateException(getNullBooleanFormatErrorDescription());
            }
            return s;
        }
        String s2 = getFalseStringValue();
        if (s2 == null) {
            if (fallbackToTrueFalse) {
                return "false";
            }
            throw new _MiscTemplateException(getNullBooleanFormatErrorDescription());
        }
        return s2;
    }

    private _ErrorDescriptionBuilder getNullBooleanFormatErrorDescription() {
        Object[] objArr = new Object[5];
        objArr[0] = "Can't convert boolean to string automatically, because the \"";
        objArr[1] = "boolean_format";
        objArr[2] = "\" setting was ";
        objArr[3] = new _DelayedJQuote(getBooleanFormat());
        objArr[4] = getBooleanFormat().equals("true,false") ? ", which is the legacy deprecated default, and we treat it as if no format was set. This is the default configuration; you should provide the format explicitly for each place where you print a boolean." : ".";
        return new _ErrorDescriptionBuilder(objArr).tips("Write something like myBool?string('yes', 'no') to specify boolean formatting in place.", new Object[]{"If you want \"true\"/\"false\" result as you are generating computer-language output (not for direct human consumption), then use \"?c\", like ${myBool?c}. (If you always generate computer-language output, then it's might be reasonable to set the \"", "boolean_format", "\" setting to \"c\" instead.)"}, new Object[]{"If you need the same two values on most places, the programmers can set the \"", "boolean_format", "\" setting to something like \"yes,no\". However, then it will be easy to unwillingly format booleans like that."});
    }

    String getTrueStringValue() {
        if (this.trueAndFalseStringsCachedForParent == getParent()) {
            return this.cachedTrueString;
        }
        cacheTrueAndFalseStrings();
        return this.cachedTrueString;
    }

    String getFalseStringValue() {
        if (this.trueAndFalseStringsCachedForParent == getParent()) {
            return this.cachedFalseString;
        }
        cacheTrueAndFalseStrings();
        return this.cachedFalseString;
    }

    private void clearCachedTrueAndFalseString() {
        this.trueAndFalseStringsCachedForParent = null;
        this.cachedTrueString = null;
        this.cachedFalseString = null;
    }

    private void cacheTrueAndFalseStrings() {
        String[] parsedBooleanFormat = parseBooleanFormat(getBooleanFormat());
        if (parsedBooleanFormat != null) {
            if (parsedBooleanFormat.length == 0) {
                CFormat cFormat = getCFormat();
                this.cachedTrueString = cFormat.getTrueString();
                this.cachedFalseString = cFormat.getFalseString();
            } else {
                this.cachedTrueString = parsedBooleanFormat[0];
                this.cachedFalseString = parsedBooleanFormat[1];
            }
        } else {
            this.cachedTrueString = null;
            this.cachedFalseString = null;
        }
        this.trueAndFalseStringsCachedForParent = getParent();
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    TemplateModel getLastReturnValue() {
        return this.lastReturnValue;
    }

    void setLastReturnValue(TemplateModel lastReturnValue) {
        this.lastReturnValue = lastReturnValue;
    }

    void clearLastReturnValue() {
        this.lastReturnValue = null;
    }

    String formatDateToPlainText(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateException {
        TemplateDateFormat format = getTemplateDateFormat(tdm, tdmSourceExpr, useTempModelExc);
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
        } catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatDateException(format, tdmSourceExpr, e, useTempModelExc);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    String formatDateToPlainText(TemplateDateModel tdm, String formatString, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
        Date date = EvalUtil.modelToDate(tdm, blamedDateSourceExp);
        TemplateDateFormat format = getTemplateDateFormat(formatString, tdm.getDateType(), (Class<? extends Date>) date.getClass(), blamedDateSourceExp, blamedFormatterExp, useTempModelExc);
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
        } catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatDateException(format, blamedDateSourceExp, e, useTempModelExc);
        }
    }

    public TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
        return getTemplateDateFormat(dateType, shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
        return getTemplateDateFormat(formatString, dateType, shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime, true);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
        boolean useSQLDTTZ = shouldUseSQLDTTimeZone(isSQLDateOrTime);
        return getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? getSQLDateAndTimeTimeZone() : getTimeZone(), isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale, TimeZone timeZone, TimeZone sqlDateAndTimeTimeZone) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = isSQLDateOrTimeClass(dateClass);
        boolean useSQLDTTZ = shouldUseSQLDTTimeZone(isSQLDateOrTime);
        return getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? sqlDateAndTimeTimeZone : timeZone, isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
        int equalCurrentTZ;
        Locale currentLocale = getLocale();
        if (locale.equals(currentLocale)) {
            TimeZone currentTimeZone = getTimeZone();
            if (timeZone.equals(currentTimeZone)) {
                equalCurrentTZ = 1;
            } else {
                TimeZone currentSQLDTTimeZone = getSQLDateAndTimeTimeZone();
                if (timeZone.equals(currentSQLDTTimeZone)) {
                    equalCurrentTZ = 2;
                } else {
                    equalCurrentTZ = 0;
                }
            }
            if (equalCurrentTZ != 0) {
                return getTemplateDateFormat(formatString, dateType, equalCurrentTZ == 2, zonelessInput, true);
            }
        }
        return getTemplateDateFormatWithoutCache(formatString, dateType, locale, timeZone, zonelessInput);
    }

    /* JADX WARN: Multi-variable type inference failed */
    TemplateDateFormat getTemplateDateFormat(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateException {
        Date date = EvalUtil.modelToDate(tdm, tdmSourceExpr);
        TemplateDateFormat format = getTemplateDateFormat(tdm.getDateType(), (Class<? extends Date>) date.getClass(), tdmSourceExpr, useTempModelExc);
        return format;
    }

    TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, boolean useTempModelExc) throws TemplateException {
        String settingName;
        String settingValue;
        try {
            return getTemplateDateFormat(dateType, dateClass);
        } catch (UnknownDateTypeFormattingUnsupportedException e) {
            throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
        } catch (TemplateValueFormatException e2) {
            switch (dateType) {
                case 1:
                    settingName = "time_format";
                    settingValue = getTimeFormat();
                    break;
                case 2:
                    settingName = "date_format";
                    settingValue = getDateFormat();
                    break;
                case 3:
                    settingName = "datetime_format";
                    settingValue = getDateTimeFormat();
                    break;
                default:
                    settingName = "???";
                    settingValue = "???";
                    break;
            }
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("The value of the \"", settingName, "\" FreeMarker configuration setting is a malformed date/time/datetime format string: ", new _DelayedJQuote(settingValue), ". Reason given: ", e2.getMessage());
            if (useTempModelExc) {
                throw new _TemplateModelException(e2, desc);
            }
            throw new _MiscTemplateException(e2, desc);
        }
    }

    TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
        try {
            return getTemplateDateFormat(formatString, dateType, dateClass);
        } catch (UnknownDateTypeFormattingUnsupportedException e) {
            throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
        } catch (TemplateValueFormatException e2) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Can't create date/time/datetime format based on format string ", new _DelayedJQuote(formatString), ". Reason given: ", e2.getMessage()).blame(blamedFormatterExp);
            if (useTempModelExc) {
                throw new _TemplateModelException(e2, desc);
            }
            throw new _MiscTemplateException(e2, desc);
        }
    }

    private TemplateDateFormat getTemplateDateFormat(int dateType, boolean useSQLDTTZ, boolean zonelessInput) throws TemplateValueFormatException {
        String formatString;
        if (dateType == 0) {
            throw new UnknownDateTypeFormattingUnsupportedException();
        }
        int cacheIdx = getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTZ);
        TemplateDateFormat[] cachedTemplateDateFormats = this.cachedTempDateFormatArray;
        if (cachedTemplateDateFormats == null) {
            cachedTemplateDateFormats = new TemplateDateFormat[16];
            this.cachedTempDateFormatArray = cachedTemplateDateFormats;
        }
        TemplateDateFormat format = cachedTemplateDateFormats[cacheIdx];
        if (format == null) {
            switch (dateType) {
                case 1:
                    formatString = getTimeFormat();
                    break;
                case 2:
                    formatString = getDateFormat();
                    break;
                case 3:
                    formatString = getDateTimeFormat();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid date type enum: " + Integer.valueOf(dateType));
            }
            format = getTemplateDateFormat(formatString, dateType, useSQLDTTZ, zonelessInput, false);
            cachedTemplateDateFormats[cacheIdx] = format;
        }
        return format;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x003c  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0068  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private freemarker.core.TemplateDateFormat getTemplateDateFormat(java.lang.String r8, int r9, boolean r10, boolean r11, boolean r12) throws freemarker.core.TemplateValueFormatException {
        /*
            r7 = this;
            r0 = r7
            java.util.HashMap<java.lang.String, freemarker.core.TemplateDateFormat>[] r0 = r0.cachedTempDateFormatsByFmtStrArray
            r14 = r0
            r0 = r14
            if (r0 != 0) goto L26
            r0 = r12
            if (r0 == 0) goto L20
            r0 = 16
            java.util.HashMap[] r0 = new java.util.HashMap[r0]
            r14 = r0
            r0 = r7
            r1 = r14
            r0.cachedTempDateFormatsByFmtStrArray = r1
            goto L26
        L20:
            r0 = 0
            r13 = r0
            goto L6b
        L26:
            r0 = r7
            r1 = r9
            r2 = r11
            r3 = r10
            int r0 = r0.getTemplateDateFormatCacheArrayIndex(r1, r2, r3)
            r16 = r0
            r0 = r14
            r1 = r16
            r0 = r0[r1]
            r13 = r0
            r0 = r13
            if (r0 != 0) goto L58
            r0 = r12
            if (r0 == 0) goto L6b
            java.util.HashMap r0 = new java.util.HashMap
            r1 = r0
            r2 = 4
            r1.<init>(r2)
            r13 = r0
            r0 = r14
            r1 = r16
            r2 = r13
            r0[r1] = r2
            r0 = 0
            r15 = r0
            goto L63
        L58:
            r0 = r13
            r1 = r8
            java.lang.Object r0 = r0.get(r1)
            freemarker.core.TemplateDateFormat r0 = (freemarker.core.TemplateDateFormat) r0
            r15 = r0
        L63:
            r0 = r15
            if (r0 == 0) goto L6b
            r0 = r15
            return r0
        L6b:
            r0 = r7
            r1 = r8
            r2 = r9
            r3 = r7
            java.util.Locale r3 = r3.getLocale()
            r4 = r10
            if (r4 == 0) goto L7d
            r4 = r7
            java.util.TimeZone r4 = r4.getSQLDateAndTimeTimeZone()
            goto L81
        L7d:
            r4 = r7
            java.util.TimeZone r4 = r4.getTimeZone()
        L81:
            r5 = r11
            freemarker.core.TemplateDateFormat r0 = r0.getTemplateDateFormatWithoutCache(r1, r2, r3, r4, r5)
            r14 = r0
            r0 = r12
            if (r0 == 0) goto L96
            r0 = r13
            r1 = r8
            r2 = r14
            java.lang.Object r0 = r0.put(r1, r2)
        L96:
            r0 = r14
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.Environment.getTemplateDateFormat(java.lang.String, int, boolean, boolean, boolean):freemarker.core.TemplateDateFormat");
    }

    private TemplateDateFormat getTemplateDateFormatWithoutCache(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
        String formatParams;
        TemplateDateFormatFactory formatFactory;
        char c;
        int formatStringLen = formatString.length();
        char firstChar = formatStringLen != 0 ? formatString.charAt(0) : (char) 0;
        if (firstChar == 'x' && formatStringLen > 1 && formatString.charAt(1) == 's') {
            formatFactory = XSTemplateDateFormatFactory.INSTANCE;
            formatParams = formatString;
        } else if (firstChar == 'i' && formatStringLen > 2 && formatString.charAt(1) == 's' && formatString.charAt(2) == 'o') {
            formatFactory = ISOTemplateDateFormatFactory.INSTANCE;
            formatParams = formatString;
        } else if (firstChar == '@' && formatStringLen > 1 && ((isIcI2324OrLater() || hasCustomFormats()) && Character.isLetter(formatString.charAt(1)))) {
            int endIdx = 1;
            while (endIdx < formatStringLen && (c = formatString.charAt(endIdx)) != ' ' && c != '_') {
                endIdx++;
            }
            String name = formatString.substring(1, endIdx);
            formatParams = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
            formatFactory = getCustomDateFormat(name);
            if (formatFactory == null) {
                throw new UndefinedCustomFormatException("No custom date format was defined with name " + StringUtil.jQuote(name));
            }
        } else {
            formatParams = formatString;
            formatFactory = JavaTemplateDateFormatFactory.INSTANCE;
        }
        return formatFactory.get(formatParams, dateType, locale, timeZone, zonelessInput, this);
    }

    boolean shouldUseSQLDTTZ(Class dateClass) {
        return (dateClass == Date.class || isSQLDateAndTimeTimeZoneSameAsNormal() || !isSQLDateOrTimeClass(dateClass)) ? false : true;
    }

    private boolean shouldUseSQLDTTimeZone(boolean sqlDateOrTime) {
        return sqlDateOrTime && !isSQLDateAndTimeTimeZoneSameAsNormal();
    }

    private static boolean isSQLDateOrTimeClass(Class dateClass) {
        return dateClass != Date.class && (dateClass == java.sql.Date.class || dateClass == Time.class || (dateClass != Timestamp.class && (java.sql.Date.class.isAssignableFrom(dateClass) || Time.class.isAssignableFrom(dateClass))));
    }

    private int getTemplateDateFormatCacheArrayIndex(int dateType, boolean zonelessInput, boolean sqlDTTZ) {
        return dateType + (zonelessInput ? 4 : 0) + (sqlDTTZ ? 8 : 0);
    }

    DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendarFactory() {
        if (this.isoBuiltInCalendarFactory == null) {
            this.isoBuiltInCalendarFactory = new DateUtil.TrivialDateToISO8601CalendarFactory();
        }
        return this.isoBuiltInCalendarFactory;
    }

    TemplateTransformModel getTransform(Expression exp) throws TemplateException {
        TemplateTransformModel ttm = null;
        TemplateModel tm = exp.eval(this);
        if (tm instanceof TemplateTransformModel) {
            ttm = (TemplateTransformModel) tm;
        } else if (exp instanceof Identifier) {
            TemplateModel tm2 = this.configuration.getSharedVariable(exp.toString());
            if (tm2 instanceof TemplateTransformModel) {
                ttm = (TemplateTransformModel) tm2;
            }
        }
        return ttm;
    }

    public TemplateModel getLocalVariable(String name) throws TemplateModelException {
        TemplateModel val = getNullableLocalVariable(name);
        if (val != TemplateNullModel.INSTANCE) {
            return val;
        }
        return null;
    }

    private final TemplateModel getNullableLocalVariable(String name) throws TemplateModelException {
        if (this.localContextStack != null) {
            for (int i = this.localContextStack.size() - 1; i >= 0; i--) {
                LocalContext lc = this.localContextStack.get(i);
                TemplateModel tm = lc.getLocalVariable(name);
                if (tm != null) {
                    return tm;
                }
            }
        }
        if (this.currentMacroContext == null) {
            return null;
        }
        return this.currentMacroContext.getLocalVariable(name);
    }

    public TemplateModel getVariable(String name) throws TemplateModelException {
        TemplateModel result = getNullableLocalVariable(name);
        if (result != null) {
            if (result != TemplateNullModel.INSTANCE) {
                return result;
            }
            return null;
        }
        TemplateModel result2 = this.currentNamespace.get(name);
        if (result2 != null) {
            return result2;
        }
        return getGlobalVariable(name);
    }

    public TemplateModel getGlobalVariable(String name) throws TemplateModelException {
        TemplateModel result = this.globalNamespace.get(name);
        if (result != null) {
            return result;
        }
        return getDataModelOrSharedVariable(name);
    }

    public TemplateModel getDataModelOrSharedVariable(String name) throws TemplateModelException {
        TemplateModel dataModelVal = this.rootDataModel.get(name);
        if (dataModelVal != null) {
            return dataModelVal;
        }
        return this.configuration.getSharedVariable(name);
    }

    public void setGlobalVariable(String name, TemplateModel value) {
        this.globalNamespace.put(name, value);
    }

    public void setVariable(String name, TemplateModel value) {
        this.currentNamespace.put(name, value);
    }

    public void setLocalVariable(String name, TemplateModel value) {
        if (this.currentMacroContext == null) {
            throw new IllegalStateException("Not executing macro body");
        }
        this.currentMacroContext.setLocalVar(name, value);
    }

    public Set getKnownVariableNames() throws TemplateModelException {
        Set set = this.configuration.getSharedVariableNames();
        if (this.rootDataModel instanceof TemplateHashModelEx) {
            TemplateModelIterator rootNames = ((TemplateHashModelEx) this.rootDataModel).keys().iterator();
            while (rootNames.hasNext()) {
                set.add(((TemplateScalarModel) rootNames.next()).getAsString());
            }
        }
        TemplateModelIterator tmi = this.globalNamespace.keys().iterator();
        while (tmi.hasNext()) {
            set.add(((TemplateScalarModel) tmi.next()).getAsString());
        }
        TemplateModelIterator tmi2 = this.currentNamespace.keys().iterator();
        while (tmi2.hasNext()) {
            set.add(((TemplateScalarModel) tmi2.next()).getAsString());
        }
        if (this.currentMacroContext != null) {
            set.addAll(this.currentMacroContext.getLocalVariableNames());
        }
        if (this.localContextStack != null) {
            for (int i = this.localContextStack.size() - 1; i >= 0; i--) {
                LocalContext lc = this.localContextStack.get(i);
                set.addAll(lc.getLocalVariableNames());
            }
        }
        return set;
    }

    public void outputInstructionStack(PrintWriter pw) throws IOException {
        outputInstructionStack(getInstructionStackSnapshot(), false, pw);
        pw.flush();
    }

    static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer w) throws IOException {
        PrintWriter pw = (PrintWriter) (w instanceof PrintWriter ? w : null);
        try {
            if (instructionStackSnapshot != null) {
                int totalFrames = instructionStackSnapshot.length;
                int i = (!terseMode || totalFrames <= 10) ? totalFrames : 9;
                int framesToPrint = i;
                boolean hideNestringRelatedFrames = terseMode && framesToPrint < totalFrames;
                int nestingRelatedFramesHidden = 0;
                int trailingFramesHidden = 0;
                int framesPrinted = 0;
                int frameIdx = 0;
                while (frameIdx < totalFrames) {
                    TemplateElement stackEl = instructionStackSnapshot[frameIdx];
                    boolean nestingRelatedElement = (frameIdx > 0 && (stackEl instanceof BodyInstruction)) || (frameIdx > 1 && (instructionStackSnapshot[frameIdx - 1] instanceof BodyInstruction));
                    if (framesPrinted < framesToPrint) {
                        if (!nestingRelatedElement || !hideNestringRelatedFrames) {
                            w.write(frameIdx == 0 ? "\t- Failed at: " : nestingRelatedElement ? "\t~ Reached through: " : "\t- Reached through: ");
                            w.write(instructionStackItemToString(stackEl));
                            if (pw != null) {
                                pw.println();
                            } else {
                                w.write(10);
                            }
                            framesPrinted++;
                        } else {
                            nestingRelatedFramesHidden++;
                        }
                    } else {
                        trailingFramesHidden++;
                    }
                    frameIdx++;
                }
                boolean hadClosingNotes = false;
                if (trailingFramesHidden > 0) {
                    w.write("\t... (Had ");
                    w.write(String.valueOf(trailingFramesHidden + nestingRelatedFramesHidden));
                    w.write(" more, hidden for tersenes)");
                    hadClosingNotes = true;
                }
                if (nestingRelatedFramesHidden > 0) {
                    if (hadClosingNotes) {
                        w.write(32);
                    } else {
                        w.write(9);
                    }
                    w.write("(Hidden " + nestingRelatedFramesHidden + " \"~\" lines for terseness)");
                    if (pw != null) {
                        pw.println();
                    } else {
                        w.write(10);
                    }
                    hadClosingNotes = true;
                }
                if (hadClosingNotes) {
                    if (pw != null) {
                        pw.println();
                    } else {
                        w.write(10);
                    }
                }
            } else {
                w.write("(The stack was empty)");
                if (pw != null) {
                    pw.println();
                } else {
                    w.write(10);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to print FTL stack trace", e);
        }
    }

    TemplateElement[] getInstructionStackSnapshot() {
        int requiredLength = 0;
        int ln = this.instructionStackSize;
        for (int i = 0; i < ln; i++) {
            TemplateElement stackEl = this.instructionStack[i];
            if (i == ln - 1 || stackEl.isShownInStackTrace()) {
                requiredLength++;
            }
        }
        if (requiredLength == 0) {
            return null;
        }
        TemplateElement[] result = new TemplateElement[requiredLength];
        int dstIdx = requiredLength - 1;
        for (int i2 = 0; i2 < ln; i2++) {
            TemplateElement stackEl2 = this.instructionStack[i2];
            if (i2 == ln - 1 || stackEl2.isShownInStackTrace()) {
                int i3 = dstIdx;
                dstIdx--;
                result[i3] = stackEl2;
            }
        }
        return result;
    }

    static String instructionStackItemToString(TemplateElement stackEl) {
        StringBuilder sb = new StringBuilder();
        appendInstructionStackItem(stackEl, sb);
        return sb.toString();
    }

    static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
        sb.append(_MessageUtil.shorten(stackEl.getDescription(), 40));
        sb.append("  [");
        Macro enclosingMacro = getEnclosingMacro(stackEl);
        if (enclosingMacro != null) {
            sb.append(_MessageUtil.formatLocationForEvaluationError(enclosingMacro, stackEl.beginLine, stackEl.beginColumn));
        } else {
            sb.append(_MessageUtil.formatLocationForEvaluationError(stackEl.getTemplate(), stackEl.beginLine, stackEl.beginColumn));
        }
        sb.append("]");
    }

    private static Macro getEnclosingMacro(TemplateElement stackEl) {
        while (stackEl != null) {
            if (stackEl instanceof Macro) {
                return (Macro) stackEl;
            }
            stackEl = stackEl.getParentElement();
        }
        return null;
    }

    private void pushLocalContext(LocalContext localContext) {
        if (this.localContextStack == null) {
            this.localContextStack = new LocalContextStack();
        }
        this.localContextStack.push(localContext);
    }

    LocalContextStack getLocalContextStack() {
        return this.localContextStack;
    }

    public Namespace getNamespace(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (this.loadedLibs != null) {
            return this.loadedLibs.get(name);
        }
        return null;
    }

    public Namespace getMainNamespace() {
        return this.mainNamespace;
    }

    public Namespace getCurrentNamespace() {
        return this.currentNamespace;
    }

    public Namespace getGlobalNamespace() {
        return this.globalNamespace;
    }

    public TemplateHashModel getDataModel() {
        return this.rootDataModel instanceof TemplateHashModelEx ? new TemplateHashModelEx() { // from class: freemarker.core.Environment.2
            @Override // freemarker.template.TemplateHashModel
            public boolean isEmpty() throws TemplateModelException {
                return false;
            }

            @Override // freemarker.template.TemplateHashModel
            public TemplateModel get(String key) throws TemplateModelException {
                return Environment.this.getDataModelOrSharedVariable(key);
            }

            @Override // freemarker.template.TemplateHashModelEx
            public TemplateCollectionModel values() throws TemplateModelException {
                return ((TemplateHashModelEx) Environment.this.rootDataModel).values();
            }

            @Override // freemarker.template.TemplateHashModelEx
            public TemplateCollectionModel keys() throws TemplateModelException {
                return ((TemplateHashModelEx) Environment.this.rootDataModel).keys();
            }

            @Override // freemarker.template.TemplateHashModelEx
            public int size() throws TemplateModelException {
                return ((TemplateHashModelEx) Environment.this.rootDataModel).size();
            }
        } : new TemplateHashModel() { // from class: freemarker.core.Environment.3
            @Override // freemarker.template.TemplateHashModel
            public boolean isEmpty() {
                return false;
            }

            @Override // freemarker.template.TemplateHashModel
            public TemplateModel get(String key) throws TemplateModelException {
                TemplateModel value = Environment.this.rootDataModel.get(key);
                return value != null ? value : Environment.this.configuration.getSharedVariable(key);
            }
        };
    }

    public TemplateHashModel getGlobalVariables() {
        return new TemplateHashModel() { // from class: freemarker.core.Environment.4
            @Override // freemarker.template.TemplateHashModel
            public boolean isEmpty() {
                return false;
            }

            @Override // freemarker.template.TemplateHashModel
            public TemplateModel get(String key) throws TemplateModelException {
                TemplateModel result = Environment.this.globalNamespace.get(key);
                if (result == null) {
                    result = Environment.this.rootDataModel.get(key);
                }
                if (result == null) {
                    result = Environment.this.configuration.getSharedVariable(key);
                }
                return result;
            }
        };
    }

    public void setTemplateProcessingTracer(TemplateProcessingTracer templateProcessingTracer) {
        this.templateProcessingTracer = templateProcessingTracer;
    }

    public TemplateProcessingTracer getTemplateProcessingTracer() {
        return this.templateProcessingTracer;
    }

    private void pushElement(TemplateElement element) {
        int newSize = this.instructionStackSize + 1;
        this.instructionStackSize = newSize;
        TemplateElement[] instructionStack = this.instructionStack;
        if (newSize > instructionStack.length) {
            TemplateElement[] newInstructionStack = new TemplateElement[newSize * 2];
            for (int i = 0; i < instructionStack.length; i++) {
                newInstructionStack[i] = instructionStack[i];
            }
            instructionStack = newInstructionStack;
            this.instructionStack = instructionStack;
        }
        instructionStack[newSize - 1] = element;
        if (this.templateProcessingTracer != null) {
            this.templateProcessingTracer.enterElement(this, element);
        }
    }

    private void popElement() {
        if (this.templateProcessingTracer != null) {
            TemplateElement element = this.instructionStack[this.instructionStackSize - 1];
            this.templateProcessingTracer.exitElement(this, element);
        }
        this.instructionStackSize--;
    }

    void replaceElementStackTop(TemplateElement instr) {
        this.instructionStack[this.instructionStackSize - 1] = instr;
    }

    public TemplateNodeModel getCurrentVisitorNode() {
        return this.currentVisitorNode;
    }

    public void setCurrentVisitorNode(TemplateNodeModel node) {
        this.currentVisitorNode = node;
    }

    TemplateModel getNodeProcessor(TemplateNodeModel node) throws TemplateException {
        String nodeName = node.getNodeName();
        if (nodeName == null) {
            throw new _MiscTemplateException(this, "Node name is null.");
        }
        TemplateModel result = getNodeProcessor(nodeName, node.getNodeNamespace(), 0);
        if (result == null) {
            String type = node.getNodeType();
            if (type == null) {
                type = "default";
            }
            result = getNodeProcessor("@" + type, (String) null, 0);
        }
        return result;
    }

    private TemplateModel getNodeProcessor(String nodeName, String nsURI, int startIndex) throws TemplateException {
        TemplateModel result = null;
        int size = this.nodeNamespaces.size();
        int i = startIndex;
        while (i < size) {
            try {
                Namespace ns = (Namespace) this.nodeNamespaces.get(i);
                result = getNodeProcessor(ns, nodeName, nsURI);
                if (result != null) {
                    break;
                }
                i++;
            } catch (ClassCastException e) {
                throw new _MiscTemplateException(this, "A \"using\" clause should contain a sequence of namespaces or strings that indicate the location of importable macro libraries.");
            }
        }
        if (result != null) {
            this.nodeNamespaceIndex = i + 1;
            this.currentNodeName = nodeName;
            this.currentNodeNS = nsURI;
        }
        return result;
    }

    private TemplateModel getNodeProcessor(Namespace ns, String localName, String nsURI) throws TemplateException {
        TemplateModel result = null;
        if (nsURI == null) {
            result = ns.get(localName);
            if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                result = null;
            }
        } else {
            Template template = ns.getTemplate();
            String prefix = template.getPrefixForNamespace(nsURI);
            if (prefix == null) {
                return null;
            }
            if (prefix.length() > 0) {
                result = ns.get(prefix + ":" + localName);
                if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                    result = null;
                }
            } else {
                if (nsURI.length() == 0) {
                    result = ns.get("N:" + localName);
                    if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                        result = null;
                    }
                }
                if (nsURI.equals(template.getDefaultNS())) {
                    result = ns.get("D:" + localName);
                    if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                        result = null;
                    }
                }
                if (result == null) {
                    result = ns.get(localName);
                    if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                        result = null;
                    }
                }
            }
        }
        return result;
    }

    public void include(String name, String encoding, boolean parse) throws TemplateException, IOException {
        include(getTemplateForInclusion(name, encoding, parse));
    }

    public Template getTemplateForInclusion(String name, String encoding, boolean parse) throws IOException {
        return getTemplateForInclusion(name, encoding, parse, false);
    }

    public Template getTemplateForInclusion(String name, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
        return this.configuration.getTemplate(name, getLocale(), getIncludedTemplateCustomLookupCondition(), encoding != null ? encoding : getIncludedTemplateEncoding(), parseAsFTL, ignoreMissing);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getIncludedTemplateCustomLookupCondition() {
        return getTemplate().getCustomLookupCondition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getIncludedTemplateEncoding() {
        String encoding = getTemplate().getEncoding();
        if (encoding == null) {
            encoding = this.configuration.getEncoding(getLocale());
        }
        return encoding;
    }

    public void include(Template includedTemplate) throws TemplateException, IOException {
        boolean parentReplacementOn = isBeforeIcI2322();
        Template prevTemplate = getTemplate();
        if (parentReplacementOn) {
            setParent(includedTemplate);
        } else {
            this.legacyParent = includedTemplate;
        }
        importMacros(includedTemplate);
        try {
            visit(includedTemplate.getRootTreeNode());
            if (parentReplacementOn) {
                setParent(prevTemplate);
            } else {
                this.legacyParent = prevTemplate;
            }
        } catch (Throwable th) {
            if (parentReplacementOn) {
                setParent(prevTemplate);
            } else {
                this.legacyParent = prevTemplate;
            }
            throw th;
        }
    }

    public Namespace importLib(String templateName, String targetNsVarName) throws TemplateException, IOException {
        return importLib(templateName, targetNsVarName, getLazyImports());
    }

    public Namespace importLib(Template loadedTemplate, String targetNsVarName) throws TemplateException, IOException {
        return importLib((String) null, loadedTemplate, targetNsVarName);
    }

    public Namespace importLib(String templateName, String targetNsVarName, boolean lazy) throws TemplateException, IOException {
        if (lazy) {
            return importLib(templateName, (Template) null, targetNsVarName);
        }
        return importLib((String) null, getTemplateForImporting(templateName), targetNsVarName);
    }

    public Template getTemplateForImporting(String name) throws IOException {
        return getTemplateForInclusion(name, null, true);
    }

    private Namespace importLib(String templateName, Template loadedTemplate, String targetNsVarName) throws TemplateException, IOException {
        boolean lazyImport;
        String templateName2;
        if (loadedTemplate != null) {
            lazyImport = false;
            templateName2 = loadedTemplate.getName();
        } else {
            lazyImport = true;
            TemplateNameFormat tnf = getConfiguration().getTemplateNameFormat();
            templateName2 = _CacheAPI.normalizeRootBasedName(tnf, templateName);
        }
        if (this.loadedLibs == null) {
            this.loadedLibs = new HashMap<>();
        }
        Namespace existingNamespace = this.loadedLibs.get(templateName2);
        if (existingNamespace != null) {
            if (targetNsVarName != null) {
                setVariable(targetNsVarName, existingNamespace);
                if (isIcI2324OrLater() && this.currentNamespace == this.mainNamespace) {
                    this.globalNamespace.put(targetNsVarName, existingNamespace);
                }
            }
            if (!lazyImport && (existingNamespace instanceof LazilyInitializedNamespace)) {
                ((LazilyInitializedNamespace) existingNamespace).ensureInitializedTME();
            }
        } else {
            Namespace newNamespace = lazyImport ? new LazilyInitializedNamespace(templateName2) : new Namespace(loadedTemplate);
            this.loadedLibs.put(templateName2, newNamespace);
            if (targetNsVarName != null) {
                setVariable(targetNsVarName, newNamespace);
                if (this.currentNamespace == this.mainNamespace) {
                    this.globalNamespace.put(targetNsVarName, newNamespace);
                }
            }
            if (!lazyImport) {
                initializeImportLibNamespace(newNamespace, loadedTemplate);
            }
        }
        return this.loadedLibs.get(templateName2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeImportLibNamespace(Namespace newNamespace, Template loadedTemplate) throws TemplateException, IOException {
        Namespace prevNamespace = this.currentNamespace;
        this.currentNamespace = newNamespace;
        Writer prevOut = this.out;
        this.out = NullWriter.INSTANCE;
        try {
            include(loadedTemplate);
            this.out = prevOut;
            this.currentNamespace = prevNamespace;
        } catch (Throwable th) {
            this.out = prevOut;
            this.currentNamespace = prevNamespace;
            throw th;
        }
    }

    public String toFullTemplateName(String baseName, String targetName) throws MalformedTemplateNameException {
        if (isClassicCompatible() || baseName == null) {
            return targetName;
        }
        return _CacheAPI.toRootBasedName(this.configuration.getTemplateNameFormat(), baseName, targetName);
    }

    public String rootBasedToAbsoluteTemplateName(String rootBasedName) throws MalformedTemplateNameException {
        return _CacheAPI.rootBasedNameToAbsoluteName(this.configuration.getTemplateNameFormat(), rootBasedName);
    }

    String renderElementToString(TemplateElement te) throws TemplateException, IOException {
        Writer prevOut = this.out;
        try {
            StringWriter sw = new StringWriter();
            this.out = sw;
            visit(te);
            String string = sw.toString();
            this.out = prevOut;
            return string;
        } catch (Throwable th) {
            this.out = prevOut;
            throw th;
        }
    }

    void importMacros(Template template) {
        Iterator it = template.getMacros().values().iterator();
        while (it.hasNext()) {
            visitMacroDef((Macro) it.next());
        }
    }

    public String getNamespaceForPrefix(String prefix) {
        return this.currentNamespace.getTemplate().getNamespaceForPrefix(prefix);
    }

    public String getPrefixForNamespace(String nsURI) {
        return this.currentNamespace.getTemplate().getPrefixForNamespace(nsURI);
    }

    public String getDefaultNS() {
        return this.currentNamespace.getTemplate().getDefaultNS();
    }

    public Object __getitem__(String key) throws TemplateModelException {
        return BeansWrapper.getDefaultInstance().unwrap(getVariable(key));
    }

    public void __setitem__(String key, Object o) throws TemplateException {
        setGlobalVariable(key, getObjectWrapper().wrap(o));
    }

    public Object getCustomState(Object identityKey) {
        if (this.customStateVariables == null) {
            return null;
        }
        return this.customStateVariables.get(identityKey);
    }

    public Object setCustomState(Object identityKey, Object value) {
        IdentityHashMap<Object, Object> customStateVariables = this.customStateVariables;
        if (customStateVariables == null) {
            customStateVariables = new IdentityHashMap<>();
            this.customStateVariables = customStateVariables;
        }
        return customStateVariables.put(identityKey, value);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$NestedElementTemplateDirectiveBody.class */
    final class NestedElementTemplateDirectiveBody implements TemplateDirectiveBody {
        private final TemplateElement[] childBuffer;

        private NestedElementTemplateDirectiveBody(TemplateElement[] childBuffer) {
            this.childBuffer = childBuffer;
        }

        @Override // freemarker.template.TemplateDirectiveBody
        public void render(Writer newOut) throws TemplateException, IOException {
            Writer prevOut = Environment.this.out;
            Environment.this.out = newOut;
            try {
                Environment.this.visit(this.childBuffer);
            } finally {
                Environment.this.out = prevOut;
            }
        }

        TemplateElement[] getChildrenBuffer() {
            return this.childBuffer;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$Namespace.class */
    public class Namespace extends SimpleHash {
        private Template template;

        Namespace() {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.template = Environment.this.getTemplate();
        }

        Namespace(Template template) {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.template = template;
        }

        public Template getTemplate() {
            return this.template == null ? Environment.this.getTemplate() : this.template;
        }

        void setTemplate(Template template) {
            this.template = template;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Environment$LazilyInitializedNamespace.class */
    class LazilyInitializedNamespace extends Namespace {
        private final String templateName;
        private final Locale locale;
        private final String encoding;
        private final Object customLookupCondition;
        private InitializationStatus status;

        private LazilyInitializedNamespace(String templateName) {
            super(null);
            this.status = InitializationStatus.UNINITIALIZED;
            this.templateName = templateName;
            this.locale = Environment.this.getLocale();
            this.encoding = Environment.this.getIncludedTemplateEncoding();
            this.customLookupCondition = Environment.this.getIncludedTemplateCustomLookupCondition();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void ensureInitializedTME() throws TemplateModelException {
            if (this.status != InitializationStatus.INITIALIZED && this.status != InitializationStatus.INITIALIZING) {
                try {
                    if (this.status == InitializationStatus.FAILED) {
                        throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has already failed earlier; won't retry it.");
                    }
                    try {
                        this.status = InitializationStatus.INITIALIZING;
                        initialize();
                        this.status = InitializationStatus.INITIALIZED;
                        if (this.status != InitializationStatus.INITIALIZED) {
                            this.status = InitializationStatus.FAILED;
                        }
                    } catch (Exception e) {
                        throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has failed; see cause exception", e);
                    }
                } catch (Throwable th) {
                    if (this.status != InitializationStatus.INITIALIZED) {
                        this.status = InitializationStatus.FAILED;
                    }
                    throw th;
                }
            }
        }

        private void ensureInitializedRTE() {
            try {
                ensureInitializedTME();
            } catch (TemplateModelException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }

        private void initialize() throws TemplateException, IOException {
            setTemplate(Environment.this.configuration.getTemplate(this.templateName, this.locale, this.customLookupCondition, this.encoding, true, false));
            Locale lastLocale = Environment.this.getLocale();
            try {
                Environment.this.setLocale(this.locale);
                Environment.this.initializeImportLibNamespace(this, getTemplate());
            } finally {
                Environment.this.setLocale(lastLocale);
            }
        }

        @Override // freemarker.template.SimpleHash
        protected Map copyMap(Map map) {
            ensureInitializedRTE();
            return super.copyMap(map);
        }

        @Override // freemarker.core.Environment.Namespace
        public Template getTemplate() {
            ensureInitializedRTE();
            return super.getTemplate();
        }

        @Override // freemarker.template.SimpleHash
        public void put(String key, Object value) {
            ensureInitializedRTE();
            super.put(key, value);
        }

        @Override // freemarker.template.SimpleHash
        public void put(String key, boolean b) {
            ensureInitializedRTE();
            super.put(key, b);
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModel
        public TemplateModel get(String key) throws TemplateModelException {
            ensureInitializedTME();
            return super.get(key);
        }

        @Override // freemarker.template.SimpleHash
        public boolean containsKey(String key) {
            ensureInitializedRTE();
            return super.containsKey(key);
        }

        @Override // freemarker.template.SimpleHash
        public void remove(String key) {
            ensureInitializedRTE();
            super.remove(key);
        }

        @Override // freemarker.template.SimpleHash
        public void putAll(Map m) {
            ensureInitializedRTE();
            super.putAll(m);
        }

        @Override // freemarker.template.SimpleHash
        public Map toMap() throws TemplateModelException {
            ensureInitializedTME();
            return super.toMap();
        }

        @Override // freemarker.template.SimpleHash
        public String toString() {
            ensureInitializedRTE();
            return super.toString();
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public int size() {
            ensureInitializedRTE();
            return super.size();
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModel
        public boolean isEmpty() {
            ensureInitializedRTE();
            return super.isEmpty();
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel keys() {
            ensureInitializedRTE();
            return super.keys();
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel values() {
            ensureInitializedRTE();
            return super.values();
        }

        @Override // freemarker.template.SimpleHash, freemarker.template.TemplateHashModelEx2
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
            ensureInitializedRTE();
            return super.keyValuePairIterator();
        }
    }

    private boolean isBeforeIcI2322() {
        return this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_22;
    }

    boolean isIcI2324OrLater() {
        return this.configuration.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_24;
    }

    boolean getFastInvalidReferenceExceptions() {
        return this.fastInvalidReferenceExceptions;
    }

    boolean setFastInvalidReferenceExceptions(boolean b) {
        boolean res = this.fastInvalidReferenceExceptions;
        this.fastInvalidReferenceExceptions = b;
        return res;
    }
}
