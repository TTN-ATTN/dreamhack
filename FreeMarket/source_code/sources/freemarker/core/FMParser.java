package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import freemarker.core.BuiltInsForCallables;
import freemarker.core.LocalLambdaExpression;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.tomcat.jni.File;
import org.apache.tomcat.jni.SSL;
import org.springframework.asm.Opcodes;
import org.springframework.context.expression.StandardBeanExpressionResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FMParser.class */
public class FMParser implements FMParserConstants {
    private static final int ITERATOR_BLOCK_KIND_LIST = 0;
    private static final int ITERATOR_BLOCK_KIND_FOREACH = 1;
    private static final int ITERATOR_BLOCK_KIND_ITEMS = 2;
    private static final int ITERATOR_BLOCK_KIND_USER_DIRECTIVE = 3;
    private Template template;
    private boolean stripWhitespace;
    private boolean stripText;
    private boolean preventStrippings;
    private int incompatibleImprovements;
    private OutputFormat outputFormat;
    private int autoEscapingPolicy;
    private boolean autoEscaping;
    private ParserConfiguration pCfg;
    private List<ParserIteratorBlockContext> iteratorBlockContexts;
    private int breakableDirectiveNesting;
    private int continuableDirectiveNesting;
    private boolean inMacro;
    private boolean inFunction;
    private boolean requireArgsSpecialVariable;
    private LinkedList escapes;
    private int mixedContentNesting;
    public FMParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private static int[] jj_la1_4;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private static final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    private boolean trace_enabled;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FMParser$ParserIteratorBlockContext.class */
    private static class ParserIteratorBlockContext {
        private String loopVarName;
        private String loopVar2Name;
        private int kind;
        private boolean hashListing;

        private ParserIteratorBlockContext() {
        }
    }

    public static FMParser createExpressionParser(String s) {
        SimpleCharStream scs = new SimpleCharStream(new StringReader(s), 1, 1, s.length());
        FMParserTokenManager token_source = new FMParserTokenManager(scs);
        token_source.SwitchTo(2);
        FMParser parser = new FMParser(token_source);
        token_source.setParser(parser);
        return parser;
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace) {
        this(template, reader, strictSyntaxMode, stripWhitespace, 0);
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax) {
        this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, Configuration.PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS);
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax, int incompatibleImprovements) {
        this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, 10, incompatibleImprovements);
    }

    public FMParser(String template) {
        this(dummyTemplate(), (Reader) new StringReader(template), true, true);
    }

    private static Template dummyTemplate() {
        try {
            return new Template((String) null, new StringReader(""), Configuration.getDefaultConfiguration());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create dummy template", e);
        }
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int namingConvention, int incompatibleImprovements) {
        this(template, reader, new LegacyConstructorParserConfiguration(strictSyntaxMode, whitespaceStripping, tagSyntax, 20, namingConvention, Integer.valueOf(template != null ? template.getParserConfiguration().getAutoEscapingPolicy() : 21), template != null ? template.getParserConfiguration().getOutputFormat() : null, template != null ? Boolean.valueOf(template.getParserConfiguration().getRecognizeStandardFileExtensions()) : null, template != null ? Integer.valueOf(template.getParserConfiguration().getTabSize()) : null, new Version(incompatibleImprovements), template != null ? template.getArithmeticEngine() : null));
    }

    public FMParser(Template template, Reader reader, ParserConfiguration pCfg) {
        this(template, true, readerToTokenManager(reader, pCfg), pCfg);
    }

    private static FMParserTokenManager readerToTokenManager(Reader reader, ParserConfiguration pCfg) {
        SimpleCharStream simpleCharStream = new SimpleCharStream(reader, 1, 1);
        simpleCharStream.setTabSize(pCfg.getTabSize());
        return new FMParserTokenManager(simpleCharStream);
    }

    public FMParser(Template template, boolean newTemplate, FMParserTokenManager tkMan, ParserConfiguration pCfg) {
        OutputFormat outputFormatFromExt;
        this(tkMan);
        NullArgumentException.check(pCfg);
        this.pCfg = pCfg;
        NullArgumentException.check(template);
        this.template = template;
        if (pCfg instanceof LegacyConstructorParserConfiguration) {
            LegacyConstructorParserConfiguration lpCfg = (LegacyConstructorParserConfiguration) pCfg;
            lpCfg.setArithmeticEngineIfNotSet(template.getArithmeticEngine());
            lpCfg.setAutoEscapingPolicyIfNotSet(template.getConfiguration().getAutoEscapingPolicy());
            lpCfg.setOutputFormatIfNotSet(template.getOutputFormat());
            lpCfg.setRecognizeStandardFileExtensionsIfNotSet(template.getParserConfiguration().getRecognizeStandardFileExtensions());
            lpCfg.setTabSizeIfNotSet(template.getParserConfiguration().getTabSize());
        }
        int incompatibleImprovements = pCfg.getIncompatibleImprovements().intValue();
        this.token_source.incompatibleImprovements = incompatibleImprovements;
        this.incompatibleImprovements = incompatibleImprovements;
        if (!pCfg.getRecognizeStandardFileExtensions() || (outputFormatFromExt = getFormatFromStdFileExt()) == null) {
            this.autoEscapingPolicy = pCfg.getAutoEscapingPolicy();
            this.outputFormat = pCfg.getOutputFormat();
        } else {
            this.autoEscapingPolicy = 21;
            this.outputFormat = outputFormatFromExt;
        }
        recalculateAutoEscapingField();
        this.token_source.setParser(this);
        this.token_source.strictSyntaxMode = pCfg.getStrictSyntaxMode();
        int tagSyntax = pCfg.getTagSyntax();
        switch (tagSyntax) {
            case 0:
                this.token_source.autodetectTagSyntax = true;
                break;
            case 1:
                this.token_source.squBracTagSyntax = false;
                break;
            case 2:
                this.token_source.squBracTagSyntax = true;
                break;
            default:
                throw new IllegalArgumentException("Illegal argument for tagSyntax: " + tagSyntax);
        }
        this.token_source.interpolationSyntax = pCfg.getInterpolationSyntax();
        int namingConvention = pCfg.getNamingConvention();
        switch (namingConvention) {
            case 10:
            case 11:
            case 12:
                this.token_source.initialNamingConvention = namingConvention;
                this.token_source.namingConvention = namingConvention;
                this.stripWhitespace = pCfg.getWhitespaceStripping();
                if (newTemplate) {
                    _TemplateAPI.setAutoEscaping(template, this.autoEscaping);
                    _TemplateAPI.setOutputFormat(template, this.outputFormat);
                    return;
                }
                return;
            default:
                throw new IllegalArgumentException("Illegal argument for namingConvention: " + namingConvention);
        }
    }

    void setupStringLiteralMode(FMParser parentParser, OutputFormat outputFormat) {
        FMParserTokenManager parentTokenSource = parentParser.token_source;
        this.token_source.initialNamingConvention = parentTokenSource.initialNamingConvention;
        this.token_source.namingConvention = parentTokenSource.namingConvention;
        this.token_source.namingConventionEstabilisher = parentTokenSource.namingConventionEstabilisher;
        this.token_source.SwitchTo(1);
        this.outputFormat = outputFormat;
        recalculateAutoEscapingField();
        if (this.incompatibleImprovements < _VersionInts.V_2_3_24) {
            this.incompatibleImprovements = _VersionInts.V_2_3_0;
        }
        this.iteratorBlockContexts = parentParser.iteratorBlockContexts;
    }

    void tearDownStringLiteralMode(FMParser parentParser) {
        FMParserTokenManager parentTokenSource = parentParser.token_source;
        parentTokenSource.namingConvention = this.token_source.namingConvention;
        parentTokenSource.namingConventionEstabilisher = this.token_source.namingConventionEstabilisher;
    }

    void setPreventStrippings(boolean preventStrippings) {
        this.preventStrippings = preventStrippings;
    }

    private OutputFormat getFormatFromStdFileExt() {
        int ln;
        String sourceName = this.template.getSourceName();
        if (sourceName == null || (ln = sourceName.length()) < 5 || sourceName.charAt(ln - 5) != '.') {
            return null;
        }
        char c = sourceName.charAt(ln - 4);
        if (c != 'f' && c != 'F') {
            return null;
        }
        char c2 = sourceName.charAt(ln - 3);
        if (c2 != 't' && c2 != 'T') {
            return null;
        }
        char c3 = sourceName.charAt(ln - 2);
        if (c3 != 'l' && c3 != 'L') {
            return null;
        }
        char c4 = sourceName.charAt(ln - 1);
        try {
            if (c4 == 'h' || c4 == 'H') {
                return this.template.getConfiguration().getOutputFormat(HTMLOutputFormat.INSTANCE.getName());
            }
            if (c4 == 'x' || c4 == 'X') {
                return this.template.getConfiguration().getOutputFormat(XMLOutputFormat.INSTANCE.getName());
            }
            return null;
        } catch (UnregisteredOutputFormatException e) {
            throw new BugException("Unregistered std format", e);
        }
    }

    private void recalculateAutoEscapingField() {
        if (this.outputFormat instanceof MarkupOutputFormat) {
            if (this.autoEscapingPolicy == 21) {
                this.autoEscaping = ((MarkupOutputFormat) this.outputFormat).isAutoEscapedByDefault();
                return;
            }
            if (this.autoEscapingPolicy == 22 || this.autoEscapingPolicy == 23) {
                this.autoEscaping = true;
                return;
            } else {
                if (this.autoEscapingPolicy == 20) {
                    this.autoEscaping = false;
                    return;
                }
                throw new IllegalStateException("Unhandled autoEscaping ENUM: " + this.autoEscapingPolicy);
            }
        }
        this.autoEscaping = false;
    }

    MarkupOutputFormat getMarkupOutputFormat() {
        if (this.outputFormat instanceof MarkupOutputFormat) {
            return (MarkupOutputFormat) this.outputFormat;
        }
        return null;
    }

    public int _getLastTagSyntax() {
        return this.token_source.squBracTagSyntax ? 2 : 1;
    }

    public int _getLastNamingConvention() {
        return this.token_source.namingConvention;
    }

    private void notStringLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof StringLiteral) {
            throw new ParseException("Found string literal: " + exp + ". Expecting: " + expected, exp);
        }
    }

    private void notNumberLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof NumberLiteral) {
            throw new ParseException("Found number literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void notBooleanLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof BooleanLiteral) {
            throw new ParseException("Found: " + exp.getCanonicalForm() + " literal. Expecting " + expected, exp);
        }
    }

    private void notHashLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof HashLiteral) {
            throw new ParseException("Found hash literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void notListLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof ListLiteral) {
            throw new ParseException("Found list literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void numberLiteralOnly(Expression exp) throws ParseException {
        notStringLiteral(exp, "number");
        notListLiteral(exp, "number");
        notHashLiteral(exp, "number");
        notBooleanLiteral(exp, "number");
    }

    private void stringLiteralOnly(Expression exp) throws ParseException {
        notNumberLiteral(exp, "string");
        notListLiteral(exp, "string");
        notHashLiteral(exp, "string");
        notBooleanLiteral(exp, "string");
    }

    private void booleanLiteralOnly(Expression exp) throws ParseException {
        notStringLiteral(exp, "boolean (true/false)");
        notListLiteral(exp, "boolean (true/false)");
        notHashLiteral(exp, "boolean (true/false)");
        notNumberLiteral(exp, "boolean (true/false)");
    }

    private Expression escapedExpression(Expression exp) throws ParseException {
        if (!this.escapes.isEmpty()) {
            return ((EscapeBlock) this.escapes.getFirst()).doEscape(exp);
        }
        return exp;
    }

    private boolean getBoolean(Expression exp, boolean legacyCompat) throws ParseException {
        try {
            TemplateModel tm = exp.eval(null);
            if (tm instanceof TemplateBooleanModel) {
                try {
                    return ((TemplateBooleanModel) tm).getAsBoolean();
                } catch (TemplateModelException e) {
                }
            }
            if (legacyCompat && (tm instanceof TemplateScalarModel)) {
                try {
                    return StringUtil.getYesNo(((TemplateScalarModel) tm).getAsString());
                } catch (Exception e2) {
                    throw new ParseException(e2.getMessage() + "\nExpecting boolean (true/false), found: " + exp.getCanonicalForm(), exp);
                }
            }
            throw new ParseException("Expecting boolean (true/false) parameter", exp);
        } catch (Exception e3) {
            throw new ParseException(e3.getMessage() + "\nCould not evaluate expression: " + exp.getCanonicalForm(), exp, e3);
        }
    }

    void checkCurrentOutputFormatCanEscape(Token start) throws ParseException {
        if (!(this.outputFormat instanceof MarkupOutputFormat)) {
            throw new ParseException("The current output format can't do escaping: " + this.outputFormat, this.template, start);
        }
    }

    private static String forcedAutoEscapingPolicyExceptionMessage(OutputFormat outputFormat) {
        return forcedAutoEscapingPolicyExceptionMessage("Non-markup output format " + outputFormat);
    }

    private static String forcedAutoEscapingPolicyExceptionMessage(String whatCanNotBeUsed) {
        return whatCanNotBeUsed + " can't be used when the \"auto_escaping_policy\" configuration setting was set to \"force\" (FORCE_AUTO_ESCAPING_POLICY).";
    }

    private ParserIteratorBlockContext pushIteratorBlockContext() {
        if (this.iteratorBlockContexts == null) {
            this.iteratorBlockContexts = new ArrayList(4);
        }
        ParserIteratorBlockContext newCtx = new ParserIteratorBlockContext();
        this.iteratorBlockContexts.add(newCtx);
        return newCtx;
    }

    private void popIteratorBlockContext() {
        this.iteratorBlockContexts.remove(this.iteratorBlockContexts.size() - 1);
    }

    private ParserIteratorBlockContext peekIteratorBlockContext() {
        int size = this.iteratorBlockContexts != null ? this.iteratorBlockContexts.size() : 0;
        if (size != 0) {
            return this.iteratorBlockContexts.get(size - 1);
        }
        return null;
    }

    private void checkLoopVariableBuiltInLHO(String loopVarName, Expression lhoExp, Token biName) throws ParseException {
        int size = this.iteratorBlockContexts != null ? this.iteratorBlockContexts.size() : 0;
        for (int i = size - 1; i >= 0; i--) {
            ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(i);
            if (loopVarName.equals(ctx.loopVarName) || loopVarName.equals(ctx.loopVar2Name)) {
                if (ctx.kind == 3) {
                    throw new ParseException("The left hand operand of ?" + biName.image + " can't be the loop variable of an user defined directive: " + loopVarName, lhoExp);
                }
                return;
            }
        }
        throw new ParseException("The left hand operand of ?" + biName.image + " must be a loop variable, but there's no loop variable in scope with this name: " + loopVarName, lhoExp);
    }

    private String forEachDirectiveSymbol() {
        return this.token_source.namingConvention == 12 ? "#forEach" : "#foreach";
    }

    public final Expression Expression() throws ParseException {
        Expression exp = OrExpression();
        if ("" != 0) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final freemarker.core.Expression PrimaryExpression() throws freemarker.core.ParseException {
        /*
            r4 = this;
            r0 = r4
            freemarker.core.Expression r0 = r0.AtomicExpression()
            r5 = r0
        L5:
            r0 = r4
            int r0 = r0.jj_ntk
            r1 = -1
            if (r0 != r1) goto L14
            r0 = r4
            int r0 = r0.jj_ntk_f()
            goto L18
        L14:
            r0 = r4
            int r0 = r0.jj_ntk
        L18:
            switch(r0) {
                case 99: goto L5c;
                case 103: goto L5c;
                case 104: goto L5c;
                case 129: goto L5c;
                case 133: goto L5c;
                case 135: goto L5c;
                case 153: goto L5c;
                default: goto L5f;
            }
        L5c:
            goto L6c
        L5f:
            r0 = r4
            int[] r0 = r0.jj_la1
            r1 = 0
            r2 = r4
            int r2 = r2.jj_gen
            r0[r1] = r2
            goto L10e
        L6c:
            r0 = r4
            int r0 = r0.jj_ntk
            r1 = -1
            if (r0 != r1) goto L7b
            r0 = r4
            int r0 = r0.jj_ntk_f()
            goto L7f
        L7b:
            r0 = r4
            int r0 = r0.jj_ntk
        L7f:
            switch(r0) {
                case 99: goto Lc0;
                case 103: goto Ldb;
                case 104: goto Led;
                case 129: goto Le4;
                case 133: goto Lc9;
                case 135: goto Ld2;
                case 153: goto Le4;
                default: goto Lf6;
            }
        Lc0:
            r0 = r4
            r1 = r5
            freemarker.core.Expression r0 = r0.DotVariable(r1)
            r5 = r0
            goto L5
        Lc9:
            r0 = r4
            r1 = r5
            freemarker.core.Expression r0 = r0.DynamicKey(r1)
            r5 = r0
            goto L5
        Ld2:
            r0 = r4
            r1 = r5
            freemarker.core.MethodCall r0 = r0.MethodArgs(r1)
            r5 = r0
            goto L5
        Ldb:
            r0 = r4
            r1 = r5
            freemarker.core.Expression r0 = r0.BuiltIn(r1)
            r5 = r0
            goto L5
        Le4:
            r0 = r4
            r1 = r5
            freemarker.core.Expression r0 = r0.DefaultTo(r1)
            r5 = r0
            goto L5
        Led:
            r0 = r4
            r1 = r5
            freemarker.core.Expression r0 = r0.Exists(r1)
            r5 = r0
            goto L5
        Lf6:
            r0 = r4
            int[] r0 = r0.jj_la1
            r1 = 1
            r2 = r4
            int r2 = r2.jj_gen
            r0[r1] = r2
            r0 = r4
            r1 = -1
            freemarker.core.Token r0 = r0.jj_consume_token(r1)
            freemarker.core.ParseException r0 = new freemarker.core.ParseException
            r1 = r0
            r1.<init>()
            throw r0
        L10e:
            java.lang.String r0 = ""
            if (r0 == 0) goto L115
            r0 = r5
            return r0
        L115:
            java.lang.Error r0 = new java.lang.Error
            r1 = r0
            java.lang.String r2 = "Missing return statement in function"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.PrimaryExpression():freemarker.core.Expression");
    }

    public final Expression AtomicExpression() throws ParseException {
        Expression exp;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
                exp = StringLiteral(true);
                break;
            case 95:
            case 96:
                exp = BooleanLiteral();
                break;
            case 97:
            case 98:
                exp = NumberLiteral();
                break;
            case 99:
                exp = BuiltinVariable();
                break;
            case 133:
                exp = ListLiteral();
                break;
            case 135:
                exp = Parenthesis();
                break;
            case 137:
                exp = HashLiteral();
                break;
            case 142:
                exp = Identifier();
                break;
            default:
                this.jj_la1[2] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if ("" != 0) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression Parenthesis() throws ParseException {
        Token start = jj_consume_token(135);
        Expression exp = Expression();
        Token end = jj_consume_token(136);
        Expression result = new ParentheticalExpression(exp);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression UnaryExpression() throws ParseException {
        Expression result;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 133:
            case 135:
            case 137:
            case 142:
                result = PrimaryExpression();
                break;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 130:
            case 131:
            case 132:
            case 134:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            default:
                this.jj_la1[3] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 120:
            case 121:
                result = UnaryPlusMinusExpression();
                break;
            case 129:
                result = NotExpression();
                break;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression NotExpression() throws ParseException {
        Expression result = null;
        ArrayList nots = new ArrayList();
        while (true) {
            Token t = jj_consume_token(129);
            nots.add(t);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 129:
                default:
                    this.jj_la1[4] = this.jj_gen;
                    Expression exp = PrimaryExpression();
                    for (int i = 0; i < nots.size(); i++) {
                        result = new NotExpression(exp);
                        Token tok = (Token) nots.get((nots.size() - i) - 1);
                        result.setLocation(this.template, tok, exp);
                        exp = result;
                    }
                    if ("" != 0) {
                        return result;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final Expression UnaryPlusMinusExpression() throws ParseException {
        Token t;
        boolean isMinus = false;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 120:
                t = jj_consume_token(120);
                break;
            case 121:
                t = jj_consume_token(121);
                isMinus = true;
                break;
            default:
                this.jj_la1[5] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        Expression exp = PrimaryExpression();
        Expression result = new UnaryPlusMinusExpression(exp, isMinus);
        result.setLocation(this.template, t, exp);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression AdditiveExpression() throws ParseException {
        boolean plus;
        Expression arithmeticExpression;
        Expression lhs = MultiplicativeExpression();
        Expression result = lhs;
        while (jj_2_1(Integer.MAX_VALUE)) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 120:
                    jj_consume_token(120);
                    plus = true;
                    break;
                case 121:
                    jj_consume_token(121);
                    plus = false;
                    break;
                default:
                    this.jj_la1[6] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            Expression rhs = MultiplicativeExpression();
            if (plus) {
                arithmeticExpression = new AddConcatExpression(lhs, rhs);
            } else {
                numberLiteralOnly(lhs);
                numberLiteralOnly(rhs);
                arithmeticExpression = new ArithmeticExpression(lhs, rhs, 0);
            }
            result = arithmeticExpression;
            result.setLocation(this.template, lhs, rhs);
            lhs = result;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression MultiplicativeExpression() throws ParseException {
        int i;
        Expression lhs = UnaryExpression();
        Expression result = lhs;
        while (jj_2_2(Integer.MAX_VALUE)) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 122:
                    jj_consume_token(122);
                    i = 1;
                    break;
                case 123:
                case 124:
                default:
                    this.jj_la1[7] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
                case 125:
                    jj_consume_token(125);
                    i = 2;
                    break;
                case 126:
                    jj_consume_token(126);
                    i = 3;
                    break;
            }
            int operation = i;
            Expression rhs = UnaryExpression();
            numberLiteralOnly(lhs);
            numberLiteralOnly(rhs);
            result = new ArithmeticExpression(lhs, rhs, operation);
            result.setLocation(this.template, lhs, rhs);
            lhs = result;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression EqualityExpression() throws ParseException {
        Token t;
        Expression lhs = RelationalExpression();
        Expression result = lhs;
        if (jj_2_3(Integer.MAX_VALUE)) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 105:
                    t = jj_consume_token(105);
                    break;
                case 106:
                    t = jj_consume_token(106);
                    break;
                case 107:
                    t = jj_consume_token(107);
                    break;
                default:
                    this.jj_la1[8] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            Expression rhs = RelationalExpression();
            notHashLiteral(lhs, "different type for equality check");
            notHashLiteral(rhs, "different type for equality check");
            notListLiteral(lhs, "different type for equality check");
            notListLiteral(rhs, "different type for equality check");
            result = new ComparisonExpression(lhs, rhs, t.image);
            result.setLocation(this.template, lhs, rhs);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression RelationalExpression() throws ParseException {
        Token t;
        Expression lhs = RangeExpression();
        Expression result = lhs;
        if (jj_2_4(Integer.MAX_VALUE)) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 115:
                    t = jj_consume_token(115);
                    break;
                case 116:
                    t = jj_consume_token(116);
                    break;
                case 117:
                    t = jj_consume_token(117);
                    break;
                case 118:
                    t = jj_consume_token(118);
                    break;
                case 150:
                    t = jj_consume_token(150);
                    break;
                case 151:
                    t = jj_consume_token(151);
                    break;
                default:
                    this.jj_la1[9] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            Expression rhs = RangeExpression();
            numberLiteralOnly(lhs);
            numberLiteralOnly(rhs);
            result = new ComparisonExpression(lhs, rhs, t.image);
            result.setLocation(this.template, lhs, rhs);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression RangeExpression() throws ParseException {
        int endType;
        Expression rhs = null;
        Token dotDot = null;
        Expression lhs = AdditiveExpression();
        Expression result = lhs;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 100:
            case 101:
            case 102:
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 100:
                        dotDot = jj_consume_token(100);
                        endType = 2;
                        if (jj_2_5(Integer.MAX_VALUE)) {
                            rhs = AdditiveExpression();
                            endType = 0;
                            break;
                        }
                        break;
                    case 101:
                    case 102:
                        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                            case 101:
                                jj_consume_token(101);
                                endType = 1;
                                break;
                            case 102:
                                jj_consume_token(102);
                                endType = 3;
                                break;
                            default:
                                this.jj_la1[10] = this.jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                        }
                        rhs = AdditiveExpression();
                        break;
                    default:
                        this.jj_la1[11] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                numberLiteralOnly(lhs);
                if (rhs != null) {
                    numberLiteralOnly(rhs);
                }
                Range range = new Range(lhs, rhs, endType);
                if (rhs != null) {
                    range.setLocation(this.template, lhs, rhs);
                } else {
                    range.setLocation(this.template, lhs, dotDot);
                }
                result = range;
                break;
            default:
                this.jj_la1[12] = this.jj_gen;
                break;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression AndExpression() throws ParseException {
        Expression lhs = EqualityExpression();
        Expression result = lhs;
        while (jj_2_6(Integer.MAX_VALUE)) {
            jj_consume_token(127);
            Expression rhs = EqualityExpression();
            booleanLiteralOnly(lhs);
            booleanLiteralOnly(rhs);
            result = new AndExpression(lhs, rhs);
            result.setLocation(this.template, lhs, rhs);
            lhs = result;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression OrExpression() throws ParseException {
        Expression lhs = AndExpression();
        Expression result = lhs;
        while (jj_2_7(Integer.MAX_VALUE)) {
            jj_consume_token(128);
            Expression rhs = AndExpression();
            booleanLiteralOnly(lhs);
            booleanLiteralOnly(rhs);
            result = new OrExpression(lhs, rhs);
            result.setLocation(this.template, lhs, rhs);
            lhs = result;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ListLiteral ListLiteral() throws ParseException {
        new ArrayList();
        Token begin = jj_consume_token(133);
        ArrayList values = PositionalArgs();
        Token end = jj_consume_token(134);
        ListLiteral result = new ListLiteral(values);
        result.setLocation(this.template, begin, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression NumberLiteral() throws ParseException {
        Token t;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 97:
                t = jj_consume_token(97);
                break;
            case 98:
                t = jj_consume_token(98);
                break;
            default:
                this.jj_la1[13] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        String s = t.image;
        Expression result = new NumberLiteral(this.pCfg.getArithmeticEngine().toNumber(s));
        Token startToken = 0 != 0 ? null : t;
        result.setLocation(this.template, startToken, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Identifier Identifier() throws ParseException {
        Token t = jj_consume_token(142);
        Identifier id = new Identifier(t.image);
        id.setLocation(this.template, t, t);
        if ("" != 0) {
            return id;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression IdentifierOrStringLiteral() throws ParseException {
        Expression exp;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
                exp = StringLiteral(false);
                break;
            case 142:
                exp = Identifier();
                break;
            default:
                this.jj_la1[14] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if ("" != 0) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final BuiltinVariable BuiltinVariable() throws ParseException {
        TemplateModel parseTimeValue;
        Token dot = jj_consume_token(99);
        Token name = jj_consume_token(142);
        this.token_source.checkNamingConvention(name);
        String nameStr = name.image;
        if (nameStr.equals("output_format") || nameStr.equals(Configuration.OUTPUT_FORMAT_KEY_CAMEL_CASE)) {
            parseTimeValue = new SimpleScalar(this.outputFormat.getName());
        } else if (nameStr.equals("auto_esc") || nameStr.equals("autoEsc")) {
            parseTimeValue = this.autoEscaping ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        } else if (nameStr.equals("args")) {
            if (!this.inMacro && !this.inFunction) {
                throw new ParseException("The \"args\" special variable must be inside a macro or function in the template source code.", this.template, name);
            }
            this.requireArgsSpecialVariable = true;
            parseTimeValue = null;
        } else {
            parseTimeValue = null;
        }
        BuiltinVariable result = new BuiltinVariable(name, this.token_source, parseTimeValue);
        result.setLocation(this.template, dot, name);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DefaultTo(Expression exp) throws ParseException {
        Token t;
        Expression rhs = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 129:
                t = jj_consume_token(129);
                if (jj_2_8(Integer.MAX_VALUE)) {
                    rhs = Expression();
                    break;
                }
                break;
            case 153:
                t = jj_consume_token(153);
                break;
            default:
                this.jj_la1[15] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        DefaultToExpression result = new DefaultToExpression(exp, rhs);
        if (rhs == null) {
            result.setLocation(this.template, exp.beginColumn, exp.beginLine, t.beginColumn, t.beginLine);
        } else {
            result.setLocation(this.template, exp, rhs);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression Exists(Expression exp) throws ParseException {
        Token t = jj_consume_token(104);
        ExistsExpression result = new ExistsExpression(exp);
        result.setLocation(this.template, exp, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression BuiltIn(Expression lhoExp) throws ParseException {
        jj_consume_token(103);
        Token t = jj_consume_token(142);
        this.token_source.checkNamingConvention(t);
        BuiltIn result = BuiltIn.newBuiltIn(this.incompatibleImprovements, lhoExp, t, this.token_source);
        result.setLocation(this.template, lhoExp, t);
        if (!(result instanceof SpecialBuiltIn) && "" != 0) {
            return result;
        }
        if (result instanceof BuiltInForLoopVariable) {
            if (!(lhoExp instanceof Identifier)) {
                throw new ParseException("Expression used as the left hand operand of ?" + t.image + " must be a simple loop variable name.", lhoExp);
            }
            String loopVarName = ((Identifier) lhoExp).getName();
            checkLoopVariableBuiltInLHO(loopVarName, lhoExp, t);
            ((BuiltInForLoopVariable) result).bindToLoopVariable(loopVarName);
            if ("" != 0) {
                return result;
            }
        }
        if (result instanceof BuiltInBannedWhenAutoEscaping) {
            if ((this.outputFormat instanceof MarkupOutputFormat) && this.autoEscaping) {
                throw new ParseException("Using ?" + t.image + " (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat.getName() + "), to avoid double-escaping mistakes.", this.template, t);
            }
            if ("" != 0) {
                return result;
            }
        }
        if ((result instanceof BuiltInBannedWhenForcedAutoEscaping) && this.autoEscapingPolicy == 23) {
            throw new ParseException(forcedAutoEscapingPolicyExceptionMessage("The ?" + t.image + " expression"), this.template, t);
        }
        if (result instanceof MarkupOutputFormatBoundBuiltIn) {
            if (!(this.outputFormat instanceof MarkupOutputFormat)) {
                throw new ParseException(CallerData.NA + t.image + " can't be used here, as the current output format isn't a markup (escaping) format: " + this.outputFormat, this.template, t);
            }
            ((MarkupOutputFormatBoundBuiltIn) result).bindToMarkupOutputFormat((MarkupOutputFormat) this.outputFormat);
            if ("" != 0) {
                return result;
            }
        }
        if (result instanceof OutputFormatBoundBuiltIn) {
            ((OutputFormatBoundBuiltIn) result).bindToOutputFormat(this.outputFormat, this.autoEscapingPolicy);
            if ("" != 0) {
                return result;
            }
        }
        if ((result instanceof BuiltInWithParseTimeParameters) && !((BuiltInWithParseTimeParameters) result).isLocalLambdaParameterSupported()) {
            Token openParen = jj_consume_token(135);
            ArrayList<Expression> args = PositionalArgs();
            Token closeParen = jj_consume_token(136);
            result.setLocation(this.template, lhoExp, closeParen);
            ((BuiltInWithParseTimeParameters) result).bindToParameters(args, openParen, closeParen);
            if ("" != 0) {
                return result;
            }
        }
        if ((result instanceof BuiltInWithParseTimeParameters) && ((BuiltInWithParseTimeParameters) result).isLocalLambdaParameterSupported()) {
            Token openParen2 = jj_consume_token(135);
            ArrayList<Expression> args2 = PositionalMaybeLambdaArgs();
            Token closeParen2 = jj_consume_token(136);
            result.setLocation(this.template, lhoExp, closeParen2);
            ((BuiltInWithParseTimeParameters) result).bindToParameters(args2, openParen2, closeParen2);
            if ("" != 0) {
                return result;
            }
        }
        if (jj_2_9(Integer.MAX_VALUE) && (result instanceof BuiltInWithDirectCallOptimization)) {
            MethodCall methodCall = MethodArgs(result);
            ((BuiltInWithDirectCallOptimization) result).setDirectlyCalled();
            if ("" != 0) {
                return methodCall;
            }
        }
        if (!(result instanceof BuiltInWithDirectCallOptimization) || "" == 0) {
            throw new AssertionError("Unhandled " + SpecialBuiltIn.class.getName() + " subclass: " + result.getClass());
        }
        return result;
    }

    public final Expression LocalLambdaExpression() throws ParseException {
        Expression result;
        if (jj_2_10(Integer.MAX_VALUE)) {
            LocalLambdaExpression.LambdaParameterList lhs = LambdaExpressionParameterList();
            jj_consume_token(119);
            Expression rhs = OrExpression();
            result = new LocalLambdaExpression(lhs, rhs);
            if (lhs.getOpeningParenthesis() != null) {
                result.setLocation(this.template, lhs.getOpeningParenthesis(), rhs);
            } else {
                result.setLocation(this.template, lhs.getParameters().get(0), rhs);
            }
        } else {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 120:
                case 121:
                case 129:
                case 133:
                case 135:
                case 137:
                case 142:
                    result = OrExpression();
                    break;
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 122:
                case 123:
                case 124:
                case 125:
                case 126:
                case 127:
                case 128:
                case 130:
                case 131:
                case 132:
                case 134:
                case 136:
                case 138:
                case 139:
                case 140:
                case 141:
                default:
                    this.jj_la1[16] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final LocalLambdaExpression.LambdaParameterList LambdaExpressionParameterList() throws ParseException {
        Token openParen = null;
        Token closeParen = null;
        List<Identifier> params = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 135:
                openParen = jj_consume_token(135);
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 142:
                        Identifier param = Identifier();
                        params = new ArrayList<>(4);
                        params.add(param);
                        while (true) {
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 130:
                                    jj_consume_token(130);
                                    Identifier param2 = Identifier();
                                    params.add(param2);
                                default:
                                    this.jj_la1[17] = this.jj_gen;
                                    break;
                            }
                        }
                    default:
                        this.jj_la1[18] = this.jj_gen;
                        break;
                }
                closeParen = jj_consume_token(136);
                break;
            case 142:
                Identifier param3 = Identifier();
                params = Collections.singletonList(param3);
                break;
            default:
                this.jj_la1[19] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if ("" != 0) {
            return new LocalLambdaExpression.LambdaParameterList(openParen, params != null ? params : Collections.emptyList(), closeParen);
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DotVariable(Expression exp) throws ParseException {
        Token t;
        jj_consume_token(99);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 95:
            case 96:
            case 115:
            case 116:
            case 117:
            case 118:
            case 139:
            case 140:
            case 141:
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 95:
                        t = jj_consume_token(95);
                        break;
                    case 96:
                        t = jj_consume_token(96);
                        break;
                    case 115:
                        t = jj_consume_token(115);
                        break;
                    case 116:
                        t = jj_consume_token(116);
                        break;
                    case 117:
                        t = jj_consume_token(117);
                        break;
                    case 118:
                        t = jj_consume_token(118);
                        break;
                    case 139:
                        t = jj_consume_token(139);
                        break;
                    case 140:
                        t = jj_consume_token(140);
                        break;
                    case 141:
                        t = jj_consume_token(141);
                        break;
                    default:
                        this.jj_la1[20] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                if (!Character.isLetter(t.image.charAt(0))) {
                    throw new ParseException(t.image + " is not a valid identifier.", this.template, t);
                }
                break;
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 119:
            case 120:
            case 121:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            default:
                this.jj_la1[21] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 122:
                t = jj_consume_token(122);
                break;
            case 123:
                t = jj_consume_token(123);
                break;
            case 142:
                t = jj_consume_token(142);
                break;
        }
        notListLiteral(exp, "hash");
        notStringLiteral(exp, "hash");
        notBooleanLiteral(exp, "hash");
        Dot dot = new Dot(exp, t.image);
        dot.setLocation(this.template, exp, t);
        if ("" != 0) {
            return dot;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DynamicKey(Expression exp) throws ParseException {
        jj_consume_token(133);
        Expression arg = Expression();
        Token t = jj_consume_token(134);
        notBooleanLiteral(exp, "list or hash");
        notNumberLiteral(exp, "list or hash");
        DynamicKeyName dkn = new DynamicKeyName(exp, arg);
        dkn.setLocation(this.template, exp, t);
        if ("" != 0) {
            return dkn;
        }
        throw new Error("Missing return statement in function");
    }

    public final MethodCall MethodArgs(Expression exp) throws ParseException {
        new ArrayList();
        jj_consume_token(135);
        ArrayList args = PositionalArgs();
        Token end = jj_consume_token(136);
        args.trimToSize();
        if (args.isEmpty()) {
            if (exp instanceof Dot) {
                exp = new DotBeforeMethodCall((Dot) exp);
            } else if (exp instanceof DynamicKeyName) {
                exp = new DynamicKeyNameBeforeMethodCall((DynamicKeyName) exp);
            }
        }
        MethodCall result = new MethodCall(exp, args);
        result.setLocation(this.template, exp, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final StringLiteral StringLiteral(boolean interpolate) throws ParseException {
        Token t;
        String s;
        int interpolationSyntax;
        boolean raw = false;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
                t = jj_consume_token(93);
                break;
            case 94:
                t = jj_consume_token(94);
                raw = true;
                break;
            default:
                this.jj_la1[22] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if (raw) {
            s = t.image.substring(2, t.image.length() - 1);
        } else {
            try {
                s = StringUtil.FTLStringLiteralDec(t.image.substring(1, t.image.length() - 1));
            } catch (ParseException pe) {
                pe.lineNumber = t.beginLine;
                pe.columnNumber = t.beginColumn;
                pe.endLineNumber = t.endLine;
                pe.endColumnNumber = t.endColumn;
                throw pe;
            }
        }
        StringLiteral result = new StringLiteral(s);
        result.setLocation(this.template, t, t);
        if (interpolate && !raw && ((((interpolationSyntax = this.pCfg.getInterpolationSyntax()) == 20 || interpolationSyntax == 21) && t.image.indexOf("${") != -1) || ((interpolationSyntax == 20 && t.image.indexOf(StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX) != -1) || (interpolationSyntax == 22 && t.image.indexOf("[=") != -1)))) {
            result.parseValue(this, this.outputFormat);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression BooleanLiteral() throws ParseException {
        Token t;
        Expression result;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 95:
                t = jj_consume_token(95);
                result = new BooleanLiteral(false);
                break;
            case 96:
                t = jj_consume_token(96);
                result = new BooleanLiteral(true);
                break;
            default:
                this.jj_la1[23] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        result.setLocation(this.template, t, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final HashLiteral HashLiteral() throws ParseException {
        ArrayList<Expression> keys = new ArrayList<>();
        ArrayList<Expression> values = new ArrayList<>();
        Token begin = jj_consume_token(137);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 120:
            case 121:
            case 129:
            case 133:
            case 135:
            case 137:
            case 142:
                Expression key = Expression();
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 130:
                        jj_consume_token(130);
                        break;
                    case 132:
                        jj_consume_token(132);
                        break;
                    default:
                        this.jj_la1[24] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                Expression value = Expression();
                stringLiteralOnly(key);
                keys.add(key);
                values.add(value);
                while (true) {
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 130:
                            jj_consume_token(130);
                            Expression key2 = Expression();
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 130:
                                    jj_consume_token(130);
                                    break;
                                case 132:
                                    jj_consume_token(132);
                                    break;
                                default:
                                    this.jj_la1[26] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            Expression value2 = Expression();
                            stringLiteralOnly(key2);
                            keys.add(key2);
                            values.add(value2);
                        default:
                            this.jj_la1[25] = this.jj_gen;
                            break;
                    }
                }
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 130:
            case 131:
            case 132:
            case 134:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            default:
                this.jj_la1[27] = this.jj_gen;
                break;
        }
        Token end = jj_consume_token(138);
        keys.trimToSize();
        values.trimToSize();
        HashLiteral result = new HashLiteral(keys, values);
        result.setLocation(this.template, begin, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final DollarVariable StringOutput() throws ParseException {
        Token begin;
        Expression exp;
        Token end;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 82:
                begin = jj_consume_token(82);
                exp = Expression();
                end = jj_consume_token(138);
                break;
            case 84:
                begin = jj_consume_token(84);
                exp = Expression();
                end = jj_consume_token(134);
                break;
            default:
                this.jj_la1[28] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        notHashLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
        notListLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
        DollarVariable result = new DollarVariable(exp, escapedExpression(exp), this.outputFormat, this.autoEscaping);
        result.setLocation(this.template, begin, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NumericalOutput NumericalOutput() throws ParseException, NumberFormatException {
        NumericalOutput result;
        Token fmt = null;
        Token begin = jj_consume_token(83);
        Expression exp = Expression();
        numberLiteralOnly(exp);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 131:
                jj_consume_token(131);
                fmt = jj_consume_token(142);
                break;
            default:
                this.jj_la1[29] = this.jj_gen;
                break;
        }
        Token end = jj_consume_token(138);
        MarkupOutputFormat<?> autoEscOF = (this.autoEscaping && (this.outputFormat instanceof MarkupOutputFormat)) ? (MarkupOutputFormat) this.outputFormat : null;
        if (fmt != null) {
            int minFrac = -1;
            int maxFrac = -1;
            StringTokenizer st = new StringTokenizer(fmt.image, "mM", true);
            char type = '-';
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (type != '-') {
                    try {
                        switch (type) {
                            case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
                                if (maxFrac == -1) {
                                    maxFrac = Integer.parseInt(token);
                                    break;
                                } else {
                                    throw new ParseException("Invalid formatting string", this.template, fmt);
                                }
                            case 'm':
                                if (minFrac == -1) {
                                    minFrac = Integer.parseInt(token);
                                    break;
                                } else {
                                    throw new ParseException("Invalid formatting string", this.template, fmt);
                                }
                            default:
                                throw new ParseException("Invalid formatting string", this.template, fmt);
                        }
                        type = '-';
                    } catch (ParseException e) {
                        throw new ParseException("Invalid format specifier " + fmt.image, this.template, fmt);
                    } catch (NumberFormatException e2) {
                        throw new ParseException("Invalid number in the format specifier " + fmt.image, this.template, fmt);
                    }
                } else if (token.equals(ANSIConstants.ESC_END)) {
                    type = 'm';
                } else if (token.equals("M")) {
                    type = 'M';
                } else {
                    throw new ParseException();
                }
            }
            if (maxFrac == -1) {
                if (minFrac == -1) {
                    throw new ParseException("Invalid format specification, at least one of m and M must be specified!", this.template, fmt);
                }
                maxFrac = minFrac;
            } else if (minFrac == -1) {
                minFrac = 0;
            }
            if (minFrac > maxFrac) {
                throw new ParseException("Invalid format specification, min cannot be greater than max!", this.template, fmt);
            }
            if (minFrac > 50 || maxFrac > 50) {
                throw new ParseException("Cannot specify more than 50 fraction digits", this.template, fmt);
            }
            result = new NumericalOutput(exp, minFrac, maxFrac, autoEscOF);
        } else {
            result = new NumericalOutput(exp, autoEscOF);
        }
        result.setLocation(this.template, begin, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement If() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(8);
        Expression condition = Expression();
        Token end = jj_consume_token(148);
        TemplateElements children = MixedContentElements();
        ConditionalBlock cblock = new ConditionalBlock(condition, children, 0);
        cblock.setLocation(this.template, start, end, children);
        IfBlock ifBlock = new IfBlock(cblock);
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 9:
                    Token t = jj_consume_token(9);
                    Expression condition2 = Expression();
                    Token end2 = LooseDirectiveEnd();
                    TemplateElements children2 = MixedContentElements();
                    ConditionalBlock cblock2 = new ConditionalBlock(condition2, children2, 2);
                    cblock2.setLocation(this.template, t, end2, children2);
                    ifBlock.addBlock(cblock2);
                default:
                    this.jj_la1[30] = this.jj_gen;
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 54:
                            Token t2 = jj_consume_token(54);
                            TemplateElements children3 = MixedContentElements();
                            ConditionalBlock cblock3 = new ConditionalBlock(null, children3, 1);
                            cblock3.setLocation(this.template, t2, t2, children3);
                            ifBlock.addBlock(cblock3);
                            break;
                        default:
                            this.jj_la1[31] = this.jj_gen;
                            break;
                    }
                    Token end3 = jj_consume_token(36);
                    ifBlock.setLocation(this.template, start, end3);
                    if ("" != 0) {
                        return ifBlock;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final AttemptBlock Attempt() throws ParseException, NumberFormatException {
        Token end;
        Token start = jj_consume_token(6);
        TemplateElements children = MixedContentElements();
        RecoveryBlock recoveryBlock = Recover();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 40:
                end = jj_consume_token(40);
                break;
            case 41:
                end = jj_consume_token(41);
                break;
            default:
                this.jj_la1[32] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        AttemptBlock result = new AttemptBlock(children, recoveryBlock);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final RecoveryBlock Recover() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(7);
        TemplateElements children = MixedContentElements();
        RecoveryBlock result = new RecoveryBlock(children);
        result.setLocation(this.template, start, start, children);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement List() throws ParseException, NumberFormatException {
        TemplateElement result;
        Token loopVar = null;
        Token loopVar2 = null;
        ElseOfList elseOfList = null;
        Token start = jj_consume_token(10);
        Expression exp = Expression();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 140:
                jj_consume_token(140);
                loopVar = jj_consume_token(142);
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 130:
                        jj_consume_token(130);
                        loopVar2 = jj_consume_token(142);
                        break;
                    default:
                        this.jj_la1[33] = this.jj_gen;
                        break;
                }
            default:
                this.jj_la1[34] = this.jj_gen;
                break;
        }
        jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = pushIteratorBlockContext();
        if (loopVar != null) {
            iterCtx.loopVarName = loopVar.image;
            this.breakableDirectiveNesting++;
            this.continuableDirectiveNesting++;
            if (loopVar2 != null) {
                iterCtx.loopVar2Name = loopVar2.image;
                iterCtx.hashListing = true;
                if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
                    throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx.loopVarName, this.template, start);
                }
            }
        }
        TemplateElements childrendBeforeElse = MixedContentElements();
        if (loopVar == null) {
            if (iterCtx.kind != 2) {
                throw new ParseException("#list must have either \"as loopVar\" parameter or nested #items that belongs to it.", this.template, start);
            }
        } else {
            this.breakableDirectiveNesting--;
            this.continuableDirectiveNesting--;
        }
        popIteratorBlockContext();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 54:
                elseOfList = ElseOfList();
                break;
            default:
                this.jj_la1[35] = this.jj_gen;
                break;
        }
        Token end = jj_consume_token(37);
        IteratorBlock list = new IteratorBlock(exp, loopVar != null ? loopVar.image : null, loopVar2 != null ? loopVar2.image : null, childrendBeforeElse, iterCtx.hashListing, false);
        list.setLocation(this.template, start, end);
        if (elseOfList == null) {
            result = list;
        } else {
            result = new ListElseContainer(list, elseOfList);
            result.setLocation(this.template, start, end);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ElseOfList ElseOfList() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(54);
        TemplateElements children = MixedContentElements();
        ElseOfList result = new ElseOfList(children);
        result.setLocation(this.template, start, start, children);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final IteratorBlock ForEach() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(13);
        Token loopVar = jj_consume_token(142);
        jj_consume_token(139);
        Expression exp = Expression();
        jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = pushIteratorBlockContext();
        iterCtx.loopVarName = loopVar.image;
        iterCtx.kind = 1;
        this.breakableDirectiveNesting++;
        this.continuableDirectiveNesting++;
        TemplateElements children = MixedContentElements();
        Token end = jj_consume_token(42);
        this.breakableDirectiveNesting--;
        this.continuableDirectiveNesting--;
        popIteratorBlockContext();
        IteratorBlock result = new IteratorBlock(exp, loopVar.image, null, children, false, true);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Items Items() throws ParseException, NumberFormatException {
        String msg;
        Token loopVar2 = null;
        Token start = jj_consume_token(11);
        Token loopVar = jj_consume_token(142);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 130:
                jj_consume_token(130);
                loopVar2 = jj_consume_token(142);
                break;
            default:
                this.jj_la1[36] = this.jj_gen;
                break;
        }
        jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = peekIteratorBlockContext();
        if (iterCtx != null) {
            if (iterCtx.loopVarName != null) {
                if (iterCtx.kind != 1) {
                    if (iterCtx.kind == 2) {
                        msg = "Can't nest #items into each other when they belong to the same #list.";
                    } else {
                        msg = "The parent #list of the #items must not have \"as loopVar\" parameter.";
                    }
                } else {
                    msg = forEachDirectiveSymbol() + " doesn't support nested #items.";
                }
                throw new ParseException(msg, this.template, start);
            }
            iterCtx.kind = 2;
            iterCtx.loopVarName = loopVar.image;
            if (loopVar2 != null) {
                iterCtx.loopVar2Name = loopVar2.image;
                iterCtx.hashListing = true;
                if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
                    throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx.loopVarName, this.template, start);
                }
            }
            this.breakableDirectiveNesting++;
            this.continuableDirectiveNesting++;
            TemplateElements children = MixedContentElements();
            Token end = jj_consume_token(38);
            this.breakableDirectiveNesting--;
            this.continuableDirectiveNesting--;
            iterCtx.loopVarName = null;
            iterCtx.loopVar2Name = null;
            Items result = new Items(loopVar.image, loopVar2 != null ? loopVar2.image : null, children);
            result.setLocation(this.template, start, end);
            if ("" != 0) {
                return result;
            }
            throw new Error("Missing return statement in function");
        }
        throw new ParseException("#items must be inside a #list block.", this.template, start);
    }

    public final Sep Sep() throws ParseException, NumberFormatException {
        Token end = null;
        Token start = jj_consume_token(12);
        if (peekIteratorBlockContext() == null) {
            throw new ParseException("#sep must be inside a #list (or " + forEachDirectiveSymbol() + ") block.", this.template, start);
        }
        TemplateElements children = MixedContentElements();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 39:
                end = jj_consume_token(39);
                break;
            default:
                this.jj_la1[37] = this.jj_gen;
                break;
        }
        Sep result = new Sep(children);
        if (end != null) {
            result.setLocation(this.template, start, end);
        } else {
            result.setLocation(this.template, start, start, children);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final VisitNode Visit() throws ParseException {
        Expression namespaces = null;
        Token start = jj_consume_token(24);
        Expression targetNode = Expression();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 141:
                jj_consume_token(141);
                namespaces = Expression();
                break;
            default:
                this.jj_la1[38] = this.jj_gen;
                break;
        }
        Token end = LooseDirectiveEnd();
        VisitNode result = new VisitNode(targetNode, namespaces);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final RecurseNode Recurse() throws ParseException {
        Token start;
        Token end = null;
        Expression node = null;
        Expression namespaces = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 67:
                start = jj_consume_token(67);
                break;
            case 68:
                start = jj_consume_token(68);
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 99:
                    case 120:
                    case 121:
                    case 129:
                    case 133:
                    case 135:
                    case 137:
                    case 142:
                        node = Expression();
                        break;
                    case 100:
                    case 101:
                    case 102:
                    case 103:
                    case 104:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                    case 114:
                    case 115:
                    case 116:
                    case 117:
                    case 118:
                    case 119:
                    case 122:
                    case 123:
                    case 124:
                    case 125:
                    case 126:
                    case 127:
                    case 128:
                    case 130:
                    case 131:
                    case 132:
                    case 134:
                    case 136:
                    case 138:
                    case 139:
                    case 140:
                    case 141:
                    default:
                        this.jj_la1[39] = this.jj_gen;
                        break;
                }
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 141:
                        jj_consume_token(141);
                        namespaces = Expression();
                        break;
                    default:
                        this.jj_la1[40] = this.jj_gen;
                        break;
                }
                end = LooseDirectiveEnd();
                break;
            default:
                this.jj_la1[41] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if (end == null) {
            end = start;
        }
        RecurseNode result = new RecurseNode(node, namespaces);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final FallbackInstruction FallBack() throws ParseException {
        Token tok = jj_consume_token(69);
        if (!this.inMacro) {
            throw new ParseException("Cannot fall back outside a macro.", this.template, tok);
        }
        FallbackInstruction result = new FallbackInstruction();
        result.setLocation(this.template, tok, tok);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final BreakInstruction Break() throws ParseException {
        Token start = jj_consume_token(55);
        if (this.breakableDirectiveNesting < 1) {
            throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items, #switch (or the deprecated " + forEachDirectiveSymbol() + ")", this.template, start);
        }
        BreakInstruction result = new BreakInstruction();
        result.setLocation(this.template, start, start);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ContinueInstruction Continue() throws ParseException {
        Token start = jj_consume_token(56);
        if (this.continuableDirectiveNesting < 1) {
            throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items (or the deprecated " + forEachDirectiveSymbol() + ")", this.template, start);
        }
        ContinueInstruction result = new ContinueInstruction();
        result.setLocation(this.template, start, start);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ReturnInstruction Return() throws ParseException {
        Token start;
        Token end;
        Expression exp = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 26:
                start = jj_consume_token(26);
                exp = Expression();
                end = LooseDirectiveEnd();
                break;
            case 57:
                start = jj_consume_token(57);
                end = start;
                break;
            default:
                this.jj_la1[42] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if (this.inMacro) {
            if (exp != null) {
                throw new ParseException("A macro cannot return a value", this.template, start);
            }
        } else if (this.inFunction) {
            if (exp == null) {
                throw new ParseException("A function must return a value", this.template, start);
            }
        } else if (exp == null) {
            throw new ParseException("A return instruction can only occur inside a macro or function", this.template, start);
        }
        ReturnInstruction result = new ReturnInstruction(exp);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final StopInstruction Stop() throws ParseException {
        Token start;
        Expression exp = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 25:
                start = jj_consume_token(25);
                exp = Expression();
                LooseDirectiveEnd();
                break;
            case 58:
                start = jj_consume_token(58);
                break;
            default:
                this.jj_la1[43] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        StopInstruction result = new StopInstruction(exp);
        result.setLocation(this.template, start, start);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Nested() throws ParseException {
        Token t;
        BodyInstruction result;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 65:
                t = jj_consume_token(65);
                result = new BodyInstruction(null);
                result.setLocation(this.template, t, t);
                break;
            case 66:
                t = jj_consume_token(66);
                ArrayList bodyParameters = PositionalArgs();
                Token end = LooseDirectiveEnd();
                result = new BodyInstruction(bodyParameters);
                result.setLocation(this.template, t, end);
                break;
            default:
                this.jj_la1[44] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if (!this.inMacro) {
            throw new ParseException("Cannot use a " + t.image + " instruction outside a macro.", this.template, t);
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Flush() throws ParseException {
        Token t = jj_consume_token(59);
        FlushInstruction result = new FlushInstruction();
        result.setLocation(this.template, t, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Trim() throws ParseException {
        Token t;
        TrimInstruction result;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 60:
                t = jj_consume_token(60);
                result = new TrimInstruction(true, true);
                break;
            case 61:
                t = jj_consume_token(61);
                result = new TrimInstruction(true, false);
                break;
            case 62:
                t = jj_consume_token(62);
                result = new TrimInstruction(false, true);
                break;
            case 63:
                t = jj_consume_token(63);
                result = new TrimInstruction(false, false);
                break;
            default:
                this.jj_la1[45] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        result.setLocation(this.template, t, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Assign() throws ParseException, NumberFormatException {
        Token start;
        int scope;
        String name;
        Token end;
        Token equalsOp;
        Expression exp;
        String name2;
        Token equalsOp2;
        Expression exp2;
        Expression nsExp = null;
        ArrayList assignments = new ArrayList();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 16:
                start = jj_consume_token(16);
                scope = 1;
                break;
            case 17:
                start = jj_consume_token(17);
                scope = 3;
                break;
            case 18:
                start = jj_consume_token(18);
                scope = 2;
                if (!this.inMacro && !this.inFunction) {
                    throw new ParseException("Local variable assigned outside a macro.", this.template, start);
                }
                break;
            default:
                this.jj_la1[46] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        Expression nameExp = IdentifierOrStringLiteral();
        if (nameExp instanceof StringLiteral) {
            name = ((StringLiteral) nameExp).getAsString();
        } else {
            name = ((Identifier) nameExp).getName();
        }
        String varName = name;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 105:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                            case 105:
                                jj_consume_token(105);
                                break;
                            case 106:
                            case 107:
                            default:
                                this.jj_la1[47] = this.jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                            case 108:
                                jj_consume_token(108);
                                break;
                            case 109:
                                jj_consume_token(109);
                                break;
                            case 110:
                                jj_consume_token(110);
                                break;
                            case 111:
                                jj_consume_token(111);
                                break;
                            case 112:
                                jj_consume_token(112);
                                break;
                        }
                        equalsOp = this.token;
                        exp = Expression();
                        break;
                    case 106:
                    case 107:
                    default:
                        this.jj_la1[49] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                    case 113:
                    case 114:
                        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                            case 113:
                                jj_consume_token(113);
                                break;
                            case 114:
                                jj_consume_token(114);
                                break;
                            default:
                                this.jj_la1[48] = this.jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                        }
                        equalsOp = this.token;
                        exp = null;
                        break;
                }
                Assignment ass = new Assignment(varName, equalsOp.kind, exp, scope);
                if (exp != null) {
                    ass.setLocation(this.template, nameExp, exp);
                } else {
                    ass.setLocation(this.template, nameExp, equalsOp);
                }
                assignments.add(ass);
                while (jj_2_11(Integer.MAX_VALUE)) {
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 130:
                            jj_consume_token(130);
                            break;
                        default:
                            this.jj_la1[50] = this.jj_gen;
                            break;
                    }
                    Expression nameExp2 = IdentifierOrStringLiteral();
                    if (nameExp2 instanceof StringLiteral) {
                        name2 = ((StringLiteral) nameExp2).getAsString();
                    } else {
                        name2 = ((Identifier) nameExp2).getName();
                    }
                    String varName2 = name2;
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 105:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 105:
                                    jj_consume_token(105);
                                    break;
                                case 106:
                                case 107:
                                default:
                                    this.jj_la1[51] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                                case 108:
                                    jj_consume_token(108);
                                    break;
                                case 109:
                                    jj_consume_token(109);
                                    break;
                                case 110:
                                    jj_consume_token(110);
                                    break;
                                case 111:
                                    jj_consume_token(111);
                                    break;
                                case 112:
                                    jj_consume_token(112);
                                    break;
                            }
                            equalsOp2 = this.token;
                            exp2 = Expression();
                            break;
                        case 106:
                        case 107:
                        default:
                            this.jj_la1[53] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                        case 113:
                        case 114:
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 113:
                                    jj_consume_token(113);
                                    break;
                                case 114:
                                    jj_consume_token(114);
                                    break;
                                default:
                                    this.jj_la1[52] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            equalsOp2 = this.token;
                            exp2 = null;
                            break;
                    }
                    Assignment ass2 = new Assignment(varName2, equalsOp2.kind, exp2, scope);
                    if (exp2 != null) {
                        ass2.setLocation(this.template, nameExp2, exp2);
                    } else {
                        ass2.setLocation(this.template, nameExp2, equalsOp2);
                    }
                    assignments.add(ass2);
                }
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 139:
                        Token id = jj_consume_token(139);
                        nsExp = Expression();
                        if (scope != 1) {
                            throw new ParseException("Cannot assign to namespace here.", this.template, id);
                        }
                        break;
                    default:
                        this.jj_la1[54] = this.jj_gen;
                        break;
                }
                Token end2 = LooseDirectiveEnd();
                if (assignments.size() == 1) {
                    Assignment a = (Assignment) assignments.get(0);
                    a.setNamespaceExp(nsExp);
                    a.setLocation(this.template, start, end2);
                    if ("" != 0) {
                        return a;
                    }
                } else {
                    AssignmentInstruction ai = new AssignmentInstruction(scope);
                    for (int i = 0; i < assignments.size(); i++) {
                        ai.addAssignment((Assignment) assignments.get(i));
                    }
                    ai.setNamespaceExp(nsExp);
                    ai.setLocation(this.template, start, end2);
                    if ("" != 0) {
                        return ai;
                    }
                }
                break;
            case 139:
            case 148:
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 139:
                        Token id2 = jj_consume_token(139);
                        nsExp = Expression();
                        if (scope != 1) {
                            throw new ParseException("Cannot assign to namespace here.", this.template, id2);
                        }
                        break;
                    default:
                        this.jj_la1[55] = this.jj_gen;
                        break;
                }
                jj_consume_token(148);
                TemplateElements children = MixedContentElements();
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 43:
                        end = jj_consume_token(43);
                        if (scope != 2) {
                            throw new ParseException("Mismatched assignment tags.", this.template, end);
                        }
                        break;
                    case 44:
                        end = jj_consume_token(44);
                        if (scope != 3) {
                            throw new ParseException("Mismatched assignment tags", this.template, end);
                        }
                        break;
                    case 45:
                        end = jj_consume_token(45);
                        if (scope != 1) {
                            throw new ParseException("Mismatched assignment tags.", this.template, end);
                        }
                        break;
                    default:
                        this.jj_la1[56] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                BlockAssignment ba = new BlockAssignment(children, varName, scope, nsExp, getMarkupOutputFormat());
                ba.setLocation(this.template, start, end);
                if ("" != 0) {
                    return ba;
                }
                break;
            default:
                this.jj_la1[57] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        throw new Error("Missing return statement in function");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final freemarker.core.Include Include() throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 372
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.Include():freemarker.core.Include");
    }

    public final LibraryLoad Import() throws ParseException {
        Token start = jj_consume_token(20);
        Expression nameExp = Expression();
        jj_consume_token(140);
        Token ns = jj_consume_token(142);
        Token end = LooseDirectiveEnd();
        LibraryLoad result = new LibraryLoad(this.template, nameExp, ns.image);
        result.setLocation(this.template, start, end);
        this.template.addImport(result);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:101)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final freemarker.core.Macro Macro() throws freemarker.core.ParseException, java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 964
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.Macro():freemarker.core.Macro");
    }

    public final CompressedBlock Compress() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(32);
        TemplateElements children = MixedContentElements();
        Token end = jj_consume_token(51);
        CompressedBlock cb = new CompressedBlock(children);
        cb.setLocation(this.template, start, end);
        if ("" != 0) {
            return cb;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement UnifiedMacroTransform() throws ParseException, NumberFormatException {
        Expression startTagNameExp;
        TemplateElements children;
        Token end;
        HashMap namedArgs = null;
        ArrayList positionalArgs = null;
        ArrayList bodyParameters = null;
        int pushedCtxCount = 0;
        Token start = jj_consume_token(74);
        Expression exp = Expression();
        Expression cleanedExp = exp;
        if (cleanedExp instanceof MethodCall) {
            Expression methodCallTarget = ((MethodCall) cleanedExp).getTarget();
            if (methodCallTarget instanceof BuiltInsForCallables.with_argsBI) {
                cleanedExp = ((BuiltInsForCallables.with_argsBI) methodCallTarget).target;
            }
        }
        if ((cleanedExp instanceof Identifier) || ((cleanedExp instanceof Dot) && ((Dot) cleanedExp).onlyHasIdentifiers())) {
            startTagNameExp = cleanedExp;
        } else {
            startTagNameExp = null;
        }
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 152:
                jj_consume_token(152);
                break;
            default:
                this.jj_la1[68] = this.jj_gen;
                break;
        }
        if (jj_2_12(Integer.MAX_VALUE)) {
            namedArgs = NamedArgs();
        } else {
            positionalArgs = PositionalArgs();
        }
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 131:
                jj_consume_token(131);
                bodyParameters = new ArrayList(4);
                switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                    case 142:
                    case 152:
                        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                            case 152:
                                jj_consume_token(152);
                                break;
                            default:
                                this.jj_la1[69] = this.jj_gen;
                                break;
                        }
                        Token t = jj_consume_token(142);
                        bodyParameters.add(t.image);
                        while (true) {
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 130:
                                case 152:
                                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                        case 152:
                                            jj_consume_token(152);
                                            break;
                                        default:
                                            this.jj_la1[71] = this.jj_gen;
                                            break;
                                    }
                                    jj_consume_token(130);
                                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                        case 152:
                                            jj_consume_token(152);
                                            break;
                                        default:
                                            this.jj_la1[72] = this.jj_gen;
                                            break;
                                    }
                                    Token t2 = jj_consume_token(142);
                                    bodyParameters.add(t2.image);
                                default:
                                    this.jj_la1[70] = this.jj_gen;
                                    break;
                            }
                        }
                    default:
                        this.jj_la1[73] = this.jj_gen;
                        break;
                }
            default:
                this.jj_la1[74] = this.jj_gen;
                break;
        }
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 148:
                jj_consume_token(148);
                if (bodyParameters != null && this.iteratorBlockContexts != null && !this.iteratorBlockContexts.isEmpty()) {
                    int ctxsLen = this.iteratorBlockContexts.size();
                    int bodyParsLen = bodyParameters.size();
                    for (int bodyParIdx = 0; bodyParIdx < bodyParsLen; bodyParIdx++) {
                        String bodyParName = (String) bodyParameters.get(bodyParIdx);
                        int ctxIdx = ctxsLen - 1;
                        while (true) {
                            if (ctxIdx >= 0) {
                                ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(ctxIdx);
                                if (ctx.loopVarName == null || !ctx.loopVarName.equals(bodyParName)) {
                                    ctxIdx--;
                                } else if (ctx.kind != 3) {
                                    ParserIteratorBlockContext shadowingCtx = pushIteratorBlockContext();
                                    shadowingCtx.loopVarName = bodyParName;
                                    shadowingCtx.kind = 3;
                                    pushedCtxCount++;
                                }
                            }
                        }
                    }
                }
                children = MixedContentElements();
                end = jj_consume_token(75);
                for (int i = 0; i < pushedCtxCount; i++) {
                    popIteratorBlockContext();
                }
                String endTagName = end.image.substring(3, end.image.length() - 1).trim();
                if (endTagName.length() > 0) {
                    if (startTagNameExp == null) {
                        throw new ParseException("Expecting </@>", this.template, end);
                    }
                    String startTagName = startTagNameExp.getCanonicalForm();
                    if (!endTagName.equals(startTagName)) {
                        throw new ParseException("Expecting </@> or </@" + startTagName + ">", this.template, end);
                    }
                }
                break;
            case 149:
                end = jj_consume_token(149);
                children = TemplateElements.EMPTY;
                break;
            default:
                this.jj_la1[75] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        TemplateElement result = positionalArgs != null ? new UnifiedCall(exp, positionalArgs, children, bodyParameters) : new UnifiedCall(exp, namedArgs, children, bodyParameters);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Call() throws ParseException {
        UnifiedCall result;
        HashMap namedArgs = null;
        ArrayList positionalArgs = null;
        Token start = jj_consume_token(27);
        Token id = jj_consume_token(142);
        Identifier macroName = new Identifier(id.image);
        macroName.setLocation(this.template, id, id);
        if (jj_2_14(Integer.MAX_VALUE)) {
            namedArgs = NamedArgs();
        } else {
            if (jj_2_13(Integer.MAX_VALUE)) {
                jj_consume_token(135);
            }
            positionalArgs = PositionalArgs();
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 136:
                    jj_consume_token(136);
                    break;
                default:
                    this.jj_la1[76] = this.jj_gen;
                    break;
            }
        }
        Token end = LooseDirectiveEnd();
        if (positionalArgs != null) {
            result = new UnifiedCall(macroName, positionalArgs, TemplateElements.EMPTY, (List<String>) null);
        } else {
            result = new UnifiedCall(macroName, namedArgs, TemplateElements.EMPTY, (List<String>) null);
        }
        result.legacySyntax = true;
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final HashMap NamedArgs() throws ParseException {
        HashMap result = new HashMap();
        while (true) {
            Token t = jj_consume_token(142);
            jj_consume_token(105);
            FMParserTokenManager fMParserTokenManager = this.token_source;
            FMParserTokenManager fMParserTokenManager2 = this.token_source;
            fMParserTokenManager.SwitchTo(4);
            this.token_source.inInvocation = true;
            Expression exp = Expression();
            result.put(t.image, exp);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 142:
                default:
                    this.jj_la1[77] = this.jj_gen;
                    this.token_source.inInvocation = false;
                    if ("" != 0) {
                        return result;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final ArrayList PositionalArgs() throws ParseException {
        ArrayList result = new ArrayList();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 120:
            case 121:
            case 129:
            case 133:
            case 135:
            case 137:
            case 142:
                Expression arg = Expression();
                result.add(arg);
                while (true) {
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 93:
                        case 94:
                        case 95:
                        case 96:
                        case 97:
                        case 98:
                        case 99:
                        case 120:
                        case 121:
                        case 129:
                        case 130:
                        case 133:
                        case 135:
                        case 137:
                        case 142:
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 130:
                                    jj_consume_token(130);
                                    break;
                                default:
                                    this.jj_la1[79] = this.jj_gen;
                                    break;
                            }
                            Expression arg2 = Expression();
                            result.add(arg2);
                        case 100:
                        case 101:
                        case 102:
                        case 103:
                        case 104:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 122:
                        case 123:
                        case 124:
                        case 125:
                        case 126:
                        case 127:
                        case 128:
                        case 131:
                        case 132:
                        case 134:
                        case 136:
                        case 138:
                        case 139:
                        case 140:
                        case 141:
                        default:
                            this.jj_la1[78] = this.jj_gen;
                            break;
                    }
                }
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 130:
            case 131:
            case 132:
            case 134:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            default:
                this.jj_la1[80] = this.jj_gen;
                break;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ArrayList PositionalMaybeLambdaArgs() throws ParseException {
        ArrayList result = new ArrayList();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 120:
            case 121:
            case 129:
            case 133:
            case 135:
            case 137:
            case 142:
                Expression arg = LocalLambdaExpression();
                result.add(arg);
                while (true) {
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 93:
                        case 94:
                        case 95:
                        case 96:
                        case 97:
                        case 98:
                        case 99:
                        case 120:
                        case 121:
                        case 129:
                        case 130:
                        case 133:
                        case 135:
                        case 137:
                        case 142:
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 130:
                                    jj_consume_token(130);
                                    break;
                                default:
                                    this.jj_la1[82] = this.jj_gen;
                                    break;
                            }
                            Expression arg2 = LocalLambdaExpression();
                            result.add(arg2);
                        case 100:
                        case 101:
                        case 102:
                        case 103:
                        case 104:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 122:
                        case 123:
                        case 124:
                        case 125:
                        case 126:
                        case 127:
                        case 128:
                        case 131:
                        case 132:
                        case 134:
                        case 136:
                        case 138:
                        case 139:
                        case 140:
                        case 141:
                        default:
                            this.jj_la1[81] = this.jj_gen;
                            break;
                    }
                }
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 130:
            case 131:
            case 132:
            case 134:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            default:
                this.jj_la1[83] = this.jj_gen;
                break;
        }
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Comment Comment() throws ParseException {
        Token start;
        StringBuilder buf = new StringBuilder();
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 33:
                start = jj_consume_token(33);
                break;
            case 34:
                start = jj_consume_token(34);
                break;
            default:
                this.jj_la1[84] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        Token end = UnparsedContent(start, buf);
        Comment result = new Comment(buf.toString());
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TextBlock NoParse() throws ParseException {
        StringBuilder buf = new StringBuilder();
        Token start = jj_consume_token(35);
        Token end = UnparsedContent(start, buf);
        TextBlock result = new TextBlock(buf.toString(), true);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final freemarker.core.TransformBlock Transform() throws freemarker.core.ParseException, java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 332
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.Transform():freemarker.core.TransformBlock");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final SwitchBlock Switch() throws ParseException, NumberFormatException {
        MixedContent ignoredSectionBeforeFirstCase = null;
        boolean defaultFound = false;
        Token start = jj_consume_token(14);
        Expression switchExp = Expression();
        jj_consume_token(148);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 33:
            case 34:
            case 79:
                ignoredSectionBeforeFirstCase = WhitespaceAndComments();
                break;
            default:
                this.jj_la1[88] = this.jj_gen;
                break;
        }
        this.breakableDirectiveNesting++;
        SwitchBlock switchBlock = new SwitchBlock(switchExp, ignoredSectionBeforeFirstCase);
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 15:
            case 64:
                while (true) {
                    Case caseIns = Case();
                    if (caseIns.condition == null) {
                        if (defaultFound) {
                            throw new ParseException("You can only have one default case in a switch statement", this.template, start);
                        }
                        defaultFound = true;
                    }
                    switchBlock.addCase(caseIns);
                    switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                        case 15:
                        case 64:
                        default:
                            this.jj_la1[89] = this.jj_gen;
                            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                                case 79:
                                    jj_consume_token(79);
                                    break;
                                default:
                                    this.jj_la1[90] = this.jj_gen;
                                    break;
                            }
                    }
                }
            default:
                this.jj_la1[91] = this.jj_gen;
                break;
        }
        Token end = jj_consume_token(53);
        this.breakableDirectiveNesting--;
        switchBlock.setLocation(this.template, start, end);
        if ("" != 0) {
            return switchBlock;
        }
        throw new Error("Missing return statement in function");
    }

    public final Case Case() throws ParseException, NumberFormatException {
        Token start;
        Expression exp;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 15:
                start = jj_consume_token(15);
                exp = Expression();
                jj_consume_token(148);
                break;
            case 64:
                start = jj_consume_token(64);
                exp = null;
                break;
            default:
                this.jj_la1[92] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        TemplateElements children = MixedContentElements();
        Case result = new Case(exp, children);
        result.setLocation(this.template, start, start, children);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final EscapeBlock Escape() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(70);
        if ((this.outputFormat instanceof MarkupOutputFormat) && this.autoEscaping) {
            throw new ParseException("Using the \"escape\" directive (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat.getName() + "), to avoid confusion and double-escaping mistakes.", this.template, start);
        }
        Token variable = jj_consume_token(142);
        jj_consume_token(140);
        Expression escapeExpr = Expression();
        jj_consume_token(148);
        EscapeBlock result = new EscapeBlock(variable.image, escapeExpr, escapedExpression(escapeExpr));
        this.escapes.addFirst(result);
        TemplateElements children = MixedContentElements();
        result.setContent(children);
        this.escapes.removeFirst();
        Token end = jj_consume_token(71);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NoEscapeBlock NoEscape() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(72);
        if (this.escapes.isEmpty()) {
            throw new ParseException("#noescape with no matching #escape encountered.", this.template, start);
        }
        Object escape = this.escapes.removeFirst();
        TemplateElements children = MixedContentElements();
        Token end = jj_consume_token(73);
        this.escapes.addFirst(escape);
        NoEscapeBlock result = new NoEscapeBlock(children);
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final OutputFormatBlock OutputFormat() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(29);
        Expression paramExp = Expression();
        jj_consume_token(148);
        if (!paramExp.isLiteral()) {
            throw new ParseException("Parameter expression must be parse-time evaluable (constant): " + paramExp.getCanonicalForm(), paramExp);
        }
        try {
            TemplateModel paramTM = paramExp.eval(null);
            if (paramTM instanceof TemplateScalarModel) {
                try {
                    String paramStr = ((TemplateScalarModel) paramTM).getAsString();
                    OutputFormat lastOutputFormat = this.outputFormat;
                    try {
                        if (paramStr.startsWith("{")) {
                            if (!paramStr.endsWith("}")) {
                                throw new ParseException("Output format name that starts with '{' must end with '}': " + paramStr, this.template, start);
                            }
                            OutputFormat innerOutputFormat = this.template.getConfiguration().getOutputFormat(paramStr.substring(1, paramStr.length() - 1));
                            if (!(innerOutputFormat instanceof MarkupOutputFormat)) {
                                throw new ParseException("The output format inside the {...} must be a markup format, but was: " + innerOutputFormat, this.template, start);
                            }
                            if (!(this.outputFormat instanceof MarkupOutputFormat)) {
                                throw new ParseException("The current output format must be a markup format when using {...}, but was: " + this.outputFormat, this.template, start);
                            }
                            this.outputFormat = new CombinedMarkupOutputFormat((MarkupOutputFormat) this.outputFormat, (MarkupOutputFormat) innerOutputFormat);
                        } else {
                            this.outputFormat = this.template.getConfiguration().getOutputFormat(paramStr);
                        }
                        if (!(this.outputFormat instanceof MarkupOutputFormat) && this.autoEscapingPolicy == 23) {
                            throw new ParseException(forcedAutoEscapingPolicyExceptionMessage(this.outputFormat), this.template, start);
                        }
                        recalculateAutoEscapingField();
                        TemplateElements children = MixedContentElements();
                        Token end = jj_consume_token(48);
                        OutputFormatBlock result = new OutputFormatBlock(children, paramExp);
                        result.setLocation(this.template, start, end);
                        this.outputFormat = lastOutputFormat;
                        recalculateAutoEscapingField();
                        if ("" != 0) {
                            return result;
                        }
                        throw new Error("Missing return statement in function");
                    } catch (UnregisteredOutputFormatException e) {
                        throw new ParseException(e.getMessage(), this.template, start, e.getCause());
                    } catch (IllegalArgumentException e2) {
                        throw new ParseException("Invalid format name: " + e2.getMessage(), this.template, start, e2.getCause());
                    }
                } catch (TemplateModelException e3) {
                    throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp.getCanonicalForm() + "\nUnderlying cause: " + e3, paramExp, e3);
                }
            }
            throw new ParseException("Parameter must be a string, but was: " + ClassUtil.getFTLTypeDescription(paramTM), paramExp);
        } catch (Exception e4) {
            throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp.getCanonicalForm() + "\nUnderlying cause: " + e4, paramExp, e4);
        }
    }

    public final AutoEscBlock AutoEsc() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(30);
        checkCurrentOutputFormatCanEscape(start);
        int lastAutoEscapingPolicy = this.autoEscapingPolicy;
        this.autoEscapingPolicy = 22;
        recalculateAutoEscapingField();
        TemplateElements children = MixedContentElements();
        Token end = jj_consume_token(49);
        AutoEscBlock result = new AutoEscBlock(children);
        result.setLocation(this.template, start, end);
        this.autoEscapingPolicy = lastAutoEscapingPolicy;
        recalculateAutoEscapingField();
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NoAutoEscBlock NoAutoEsc() throws ParseException, NumberFormatException {
        Token start = jj_consume_token(31);
        if (this.autoEscapingPolicy == 23) {
            throw new ParseException(forcedAutoEscapingPolicyExceptionMessage("<#" + (this.token_source.namingConvention == 12 ? "noAutoEsc" : "noautoesc") + ">"), this.template, start);
        }
        int lastAutoEscapingPolicy = this.autoEscapingPolicy;
        this.autoEscapingPolicy = 20;
        recalculateAutoEscapingField();
        TemplateElements children = MixedContentElements();
        Token end = jj_consume_token(50);
        NoAutoEscBlock result = new NoAutoEscBlock(children);
        result.setLocation(this.template, start, end);
        this.autoEscapingPolicy = lastAutoEscapingPolicy;
        recalculateAutoEscapingField();
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Token LooseDirectiveEnd() throws ParseException {
        Token t;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 148:
                t = jj_consume_token(148);
                break;
            case 149:
                t = jj_consume_token(149);
                break;
            default:
                this.jj_la1[93] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        if ("" != 0) {
            return t;
        }
        throw new Error("Missing return statement in function");
    }

    public final PropertySetting Setting() throws ParseException {
        Token start = jj_consume_token(28);
        Token key = jj_consume_token(142);
        jj_consume_token(105);
        Expression value = Expression();
        Token end = LooseDirectiveEnd();
        this.token_source.checkNamingConvention(key);
        PropertySetting result = new PropertySetting(key, this.token_source, value, this.template.getConfiguration());
        result.setLocation(this.template, start, end);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement FreemarkerDirective() throws ParseException, NumberFormatException {
        TemplateElement tp;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 6:
                tp = Attempt();
                break;
            case 7:
            case 9:
            case 15:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 64:
            case 71:
            case 73:
            default:
                this.jj_la1[94] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 8:
                tp = If();
                break;
            case 10:
                tp = List();
                break;
            case 11:
                tp = Items();
                break;
            case 12:
                tp = Sep();
                break;
            case 13:
                tp = ForEach();
                break;
            case 14:
                tp = Switch();
                break;
            case 16:
            case 17:
            case 18:
                tp = Assign();
                break;
            case 19:
                tp = Include();
                break;
            case 20:
                tp = Import();
                break;
            case 21:
            case 22:
                tp = Macro();
                break;
            case 23:
                tp = Transform();
                break;
            case 24:
                tp = Visit();
                break;
            case 25:
            case 58:
                tp = Stop();
                break;
            case 26:
            case 57:
                tp = Return();
                break;
            case 27:
                tp = Call();
                break;
            case 28:
                tp = Setting();
                break;
            case 29:
                tp = OutputFormat();
                break;
            case 30:
                tp = AutoEsc();
                break;
            case 31:
                tp = NoAutoEsc();
                break;
            case 32:
                tp = Compress();
                break;
            case 33:
            case 34:
                tp = Comment();
                break;
            case 35:
                tp = NoParse();
                break;
            case 55:
                tp = Break();
                break;
            case 56:
                tp = Continue();
                break;
            case 59:
                tp = Flush();
                break;
            case 60:
            case 61:
            case 62:
            case 63:
                tp = Trim();
                break;
            case 65:
            case 66:
                tp = Nested();
                break;
            case 67:
            case 68:
                tp = Recurse();
                break;
            case 69:
                tp = FallBack();
                break;
            case 70:
                tp = Escape();
                break;
            case 72:
                tp = NoEscape();
                break;
            case 74:
                tp = UnifiedMacroTransform();
                break;
        }
        if ("" != 0) {
            return tp;
        }
        throw new Error("Missing return statement in function");
    }

    public final TextBlock PCData() throws ParseException {
        Token t;
        StringBuilder buf = new StringBuilder();
        Token start = null;
        Token prevToken = null;
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 79:
                    t = jj_consume_token(79);
                    break;
                case 80:
                    t = jj_consume_token(80);
                    break;
                case 81:
                    t = jj_consume_token(81);
                    break;
                default:
                    this.jj_la1[95] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            buf.append(t.image);
            if (start == null) {
                start = t;
            }
            if (prevToken != null) {
                prevToken.next = null;
            }
            prevToken = t;
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 79:
                case 80:
                case 81:
                default:
                    this.jj_la1[96] = this.jj_gen;
                    if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != 0) {
                        return null;
                    }
                    TextBlock result = new TextBlock(buf.toString(), false);
                    result.setLocation(this.template, start, t);
                    if ("" != 0) {
                        return result;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final TextBlock WhitespaceText() throws ParseException {
        Token t = jj_consume_token(79);
        if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != 0) {
            return null;
        }
        TextBlock result = new TextBlock(t.image, false);
        result.setLocation(this.template, t, t);
        if ("" != 0) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Token UnparsedContent(Token start, StringBuilder buf) throws ParseException {
        Token t;
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 154:
                    t = jj_consume_token(154);
                    break;
                case 155:
                    t = jj_consume_token(155);
                    break;
                case 156:
                    t = jj_consume_token(156);
                    break;
                case 157:
                    t = jj_consume_token(157);
                    break;
                default:
                    this.jj_la1[97] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            buf.append(t.image);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 154:
                case 155:
                case 156:
                case 157:
                default:
                    this.jj_la1[98] = this.jj_gen;
                    buf.setLength(buf.length() - t.image.length());
                    if (!t.image.endsWith(";") && _TemplateAPI.getTemplateLanguageVersionAsInt(this.template) >= _VersionInts.V_2_3_21) {
                        throw new ParseException("Unclosed \"" + start.image + "\"", this.template, start);
                    }
                    if ("" != 0) {
                        return t;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final freemarker.core.TemplateElements MixedContentElements() throws freemarker.core.ParseException, java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 905
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.MixedContentElements():freemarker.core.TemplateElements");
    }

    public final MixedContent MixedContent() throws ParseException, NumberFormatException {
        TemplateElement elem;
        MixedContent mixedContent = new MixedContent();
        TemplateElement begin = null;
        this.mixedContentNesting++;
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 6:
                case 8:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 72:
                case 74:
                    elem = FreemarkerDirective();
                    break;
                case 7:
                case 9:
                case 15:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 64:
                case 71:
                case 73:
                case 75:
                case 76:
                case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
                case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
                default:
                    this.jj_la1[101] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
                case 79:
                case 80:
                case 81:
                    elem = PCData();
                    break;
                case 82:
                case 84:
                    elem = StringOutput();
                    break;
                case 83:
                    elem = NumericalOutput();
                    break;
            }
            if (begin == null) {
                begin = elem;
            }
            mixedContent.addElement(elem);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 6:
                case 8:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 72:
                case 74:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 7:
                case 9:
                case 15:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 64:
                case 71:
                case 73:
                case 75:
                case 76:
                case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
                case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
                default:
                    this.jj_la1[102] = this.jj_gen;
                    this.mixedContentNesting--;
                    mixedContent.setLocation(this.template, begin, elem);
                    if ("" != 0) {
                        return mixedContent;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final TemplateElement OptionalBlock() throws ParseException, NumberFormatException {
        TemplateElement tp = null;
        switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
            case 6:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 72:
            case 74:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
                tp = MixedContent();
                break;
            case 7:
            case 9:
            case 15:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 64:
            case 71:
            case 73:
            case 75:
            case 76:
            case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
            case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
            default:
                this.jj_la1[103] = this.jj_gen;
                break;
        }
        if ("" != 0) {
            return tp != null ? tp : new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false);
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement FreeMarkerText() throws ParseException, NumberFormatException {
        TemplateElement elem;
        MixedContent nodes = new MixedContent();
        TemplateElement begin = null;
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 79:
                case 80:
                case 81:
                    elem = PCData();
                    break;
                case 82:
                case 84:
                    elem = StringOutput();
                    break;
                case 83:
                    elem = NumericalOutput();
                    break;
                default:
                    this.jj_la1[104] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            if (begin == null) {
                begin = elem;
            }
            nodes.addChild(elem);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                default:
                    this.jj_la1[105] = this.jj_gen;
                    nodes.setLocation(this.template, begin, elem);
                    if ("" != 0) {
                        return nodes;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    public final MixedContent WhitespaceAndComments() throws ParseException {
        TemplateElement elem;
        MixedContent nodes = new MixedContent();
        TemplateElement begin = null;
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 33:
                case 34:
                    elem = Comment();
                    break;
                case 79:
                    elem = WhitespaceText();
                    break;
                default:
                    this.jj_la1[106] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            if (elem != null) {
                if (begin == null) {
                    begin = elem;
                }
                nodes.addChild(elem);
            }
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 33:
                case 34:
                case 79:
                default:
                    this.jj_la1[107] = this.jj_gen;
                    if ((begin == null || (this.stripWhitespace && !this.preventStrippings && nodes.getChildCount() == 1 && (nodes.getChild(0) instanceof TextBlock))) && "" != 0) {
                        return null;
                    }
                    nodes.setLocation(this.template, begin, elem);
                    if ("" != 0) {
                        return nodes;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.processFallThroughCases(SwitchRegionMaker.java:105)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    /* JADX WARN: Multi-variable type inference failed */
    public final void HeaderElement() throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 1331
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.HeaderElement():void");
    }

    public final Map ParamList() throws ParseException {
        Map result = new HashMap();
        while (true) {
            Identifier id = Identifier();
            jj_consume_token(105);
            Expression exp = Expression();
            result.put(id.toString(), exp);
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 130:
                    jj_consume_token(130);
                    break;
                default:
                    this.jj_la1[111] = this.jj_gen;
                    break;
            }
            switch (this.jj_ntk == -1 ? jj_ntk_f() : this.jj_ntk) {
                case 142:
                default:
                    this.jj_la1[112] = this.jj_gen;
                    if ("" != 0) {
                        return result;
                    }
                    throw new Error("Missing return statement in function");
            }
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:281)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:64)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final java.util.List<java.lang.Object> StaticTextAndInterpolations() throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 434
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.FMParser.StaticTextAndInterpolations():java.util.List");
    }

    public final TemplateElement Root() throws ParseException, NumberFormatException {
        if (jj_2_17(Integer.MAX_VALUE)) {
            HeaderElement();
        }
        if (!(this.outputFormat instanceof MarkupOutputFormat) && this.autoEscapingPolicy == 23) {
            throw new IllegalArgumentException(forcedAutoEscapingPolicyExceptionMessage(this.outputFormat));
        }
        TemplateElements children = MixedContentElements();
        jj_consume_token(0);
        TemplateElement root = children.asSingleElement();
        root.setFieldsForRootElement();
        if (!this.preventStrippings) {
            root = root.postParseCleanup(this.stripWhitespace);
        }
        root.setFieldsForRootElement();
        if ("" != 0) {
            return root;
        }
        throw new Error("Missing return statement in function");
    }

    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_1();
            jj_save(0, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(0, xla);
            return true;
        } catch (Throwable th) {
            jj_save(0, xla);
            throw th;
        }
    }

    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_2();
            jj_save(1, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(1, xla);
            return true;
        } catch (Throwable th) {
            jj_save(1, xla);
            throw th;
        }
    }

    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_3();
            jj_save(2, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(2, xla);
            return true;
        } catch (Throwable th) {
            jj_save(2, xla);
            throw th;
        }
    }

    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_4();
            jj_save(3, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(3, xla);
            return true;
        } catch (Throwable th) {
            jj_save(3, xla);
            throw th;
        }
    }

    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_5();
            jj_save(4, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(4, xla);
            return true;
        } catch (Throwable th) {
            jj_save(4, xla);
            throw th;
        }
    }

    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_6();
            jj_save(5, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(5, xla);
            return true;
        } catch (Throwable th) {
            jj_save(5, xla);
            throw th;
        }
    }

    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_7();
            jj_save(6, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(6, xla);
            return true;
        } catch (Throwable th) {
            jj_save(6, xla);
            throw th;
        }
    }

    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_8();
            jj_save(7, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(7, xla);
            return true;
        } catch (Throwable th) {
            jj_save(7, xla);
            throw th;
        }
    }

    private boolean jj_2_9(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_9();
            jj_save(8, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(8, xla);
            return true;
        } catch (Throwable th) {
            jj_save(8, xla);
            throw th;
        }
    }

    private boolean jj_2_10(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_10();
            jj_save(9, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(9, xla);
            return true;
        } catch (Throwable th) {
            jj_save(9, xla);
            throw th;
        }
    }

    private boolean jj_2_11(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_11();
            jj_save(10, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(10, xla);
            return true;
        } catch (Throwable th) {
            jj_save(10, xla);
            throw th;
        }
    }

    private boolean jj_2_12(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_12();
            jj_save(11, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(11, xla);
            return true;
        } catch (Throwable th) {
            jj_save(11, xla);
            throw th;
        }
    }

    private boolean jj_2_13(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_13();
            jj_save(12, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(12, xla);
            return true;
        } catch (Throwable th) {
            jj_save(12, xla);
            throw th;
        }
    }

    private boolean jj_2_14(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_14();
            jj_save(13, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(13, xla);
            return true;
        } catch (Throwable th) {
            jj_save(13, xla);
            throw th;
        }
    }

    private boolean jj_2_15(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_15();
            jj_save(14, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(14, xla);
            return true;
        } catch (Throwable th) {
            jj_save(14, xla);
            throw th;
        }
    }

    private boolean jj_2_16(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_16();
            jj_save(15, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(15, xla);
            return true;
        } catch (Throwable th) {
            jj_save(15, xla);
            throw th;
        }
    }

    private boolean jj_2_17(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_17();
            jj_save(16, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(16, xla);
            return true;
        } catch (Throwable th) {
            jj_save(16, xla);
            throw th;
        }
    }

    private boolean jj_3R_AtomicExpression_1713_5_60() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_AtomicExpression_1714_9_64()) {
            this.jj_scanpos = xsp;
            if (jj_3R_AtomicExpression_1716_9_65()) {
                this.jj_scanpos = xsp;
                if (jj_3R_AtomicExpression_1718_9_66()) {
                    this.jj_scanpos = xsp;
                    if (jj_3R_AtomicExpression_1720_9_67()) {
                        this.jj_scanpos = xsp;
                        if (jj_3R_AtomicExpression_1722_9_68()) {
                            this.jj_scanpos = xsp;
                            if (jj_3R_AtomicExpression_1724_9_69()) {
                                this.jj_scanpos = xsp;
                                if (jj_3R_AtomicExpression_1726_9_70()) {
                                    this.jj_scanpos = xsp;
                                    return jj_3R_AtomicExpression_1728_9_71();
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_BuiltIn_2280_9_101() {
        return jj_scan_token(135) || jj_3R_PositionalMaybeLambdaArgs_3798_5_109() || jj_scan_token(136);
    }

    private boolean jj_3R_EqualityExpression_1897_9_57() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(107)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(105)) {
                this.jj_scanpos = xsp;
                if (jj_scan_token(106)) {
                    return true;
                }
            }
        }
        return jj_3R_RelationalExpression_1926_5_56();
    }

    private boolean jj_3R_Identifier_2092_5_43() {
        return jj_scan_token(142);
    }

    private boolean jj_3R_PositionalMaybeLambdaArgs_3801_13_114() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        return jj_3R_LocalLambdaExpression_2321_5_113();
    }

    private boolean jj_3R_MethodArgs_2465_9_88() {
        return jj_scan_token(135) || jj_3R_PositionalArgs_3776_5_98() || jj_scan_token(136);
    }

    private boolean jj_3R_EqualityExpression_1895_5_50() {
        if (jj_3R_RelationalExpression_1926_5_56()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_EqualityExpression_1897_9_57()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_PositionalMaybeLambdaArgs_3799_9_112() {
        Token xsp;
        if (jj_3R_LocalLambdaExpression_2321_5_113()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_PositionalMaybeLambdaArgs_3801_13_114());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_PrimaryExpression_1696_9_77() {
        return jj_3R_Exists_2184_5_91();
    }

    private boolean jj_3R_PrimaryExpression_1694_9_76() {
        return jj_3R_DefaultTo_2156_5_90();
    }

    private boolean jj_3R_PositionalMaybeLambdaArgs_3798_5_109() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_PositionalMaybeLambdaArgs_3799_9_112()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_PrimaryExpression_1692_9_75() {
        return jj_3R_BuiltIn_2202_5_89();
    }

    private boolean jj_3R_BuiltIn_2264_9_100() {
        return jj_scan_token(135) || jj_3R_PositionalArgs_3776_5_98() || jj_scan_token(136);
    }

    private boolean jj_3R_PrimaryExpression_1690_9_74() {
        return jj_3R_MethodArgs_2465_9_88();
    }

    private boolean jj_3R_PrimaryExpression_1688_9_73() {
        return jj_3R_DynamicKey_2444_5_87();
    }

    private boolean jj_3R_MultiplicativeExpression_1871_17_49() {
        return jj_scan_token(126);
    }

    private boolean jj_3R_NumberLiteral_2073_5_79() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(97)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(98);
        }
        return false;
    }

    private boolean jj_3R_PrimaryExpression_1686_9_61() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_PrimaryExpression_1686_9_72()) {
            this.jj_scanpos = xsp;
            if (jj_3R_PrimaryExpression_1688_9_73()) {
                this.jj_scanpos = xsp;
                if (jj_3R_PrimaryExpression_1690_9_74()) {
                    this.jj_scanpos = xsp;
                    if (jj_3R_PrimaryExpression_1692_9_75()) {
                        this.jj_scanpos = xsp;
                        if (jj_3R_PrimaryExpression_1694_9_76()) {
                            this.jj_scanpos = xsp;
                            return jj_3R_PrimaryExpression_1696_9_77();
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_PrimaryExpression_1686_9_72() {
        return jj_3R_DotVariable_2395_9_86();
    }

    private boolean jj_3R_MultiplicativeExpression_1869_17_48() {
        return jj_scan_token(125);
    }

    private boolean jj_3R_MultiplicativeExpression_1867_17_47() {
        return jj_scan_token(122);
    }

    private boolean jj_3_16() {
        return jj_scan_token(83);
    }

    private boolean jj_3_2() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(122)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(125)) {
                this.jj_scanpos = xsp;
                return jj_scan_token(126);
            }
            return false;
        }
        return false;
    }

    private boolean jj_3_15() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(82)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(84);
        }
        return false;
    }

    private boolean jj_3R_PositionalArgs_3779_13_111() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        return jj_3R_Expression_1668_5_29();
    }

    private boolean jj_3R_PrimaryExpression_1684_5_55() {
        Token xsp;
        if (jj_3R_AtomicExpression_1713_5_60()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_PrimaryExpression_1686_9_61());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_PositionalArgs_3777_9_108() {
        Token xsp;
        if (jj_3R_Expression_1668_5_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_PositionalArgs_3779_13_111());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_DynamicKey_2444_5_87() {
        return jj_scan_token(133) || jj_3R_Expression_1668_5_29() || jj_scan_token(134);
    }

    private boolean jj_3R_MultiplicativeExpression_1864_9_37() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_MultiplicativeExpression_1867_17_47()) {
            this.jj_scanpos = xsp;
            if (jj_3R_MultiplicativeExpression_1869_17_48()) {
                this.jj_scanpos = xsp;
                if (jj_3R_MultiplicativeExpression_1871_17_49()) {
                    return true;
                }
            }
        }
        return jj_3R_UnaryExpression_1762_5_36();
    }

    private boolean jj_3R_ListLiteral_2058_5_83() {
        return jj_scan_token(133) || jj_3R_PositionalArgs_3776_5_98() || jj_scan_token(134);
    }

    private boolean jj_3R_PositionalArgs_3776_5_98() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_PositionalArgs_3777_9_108()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_MultiplicativeExpression_1862_5_31() {
        Token xsp;
        if (jj_3R_UnaryExpression_1762_5_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_MultiplicativeExpression_1864_9_37());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Expression_1668_5_29() {
        return jj_3R_OrExpression_2034_5_33();
    }

    private boolean jj_3_7() {
        return jj_scan_token(128);
    }

    private boolean jj_3R_OrExpression_2036_9_41() {
        return jj_scan_token(128) || jj_3R_AndExpression_2011_5_40();
    }

    private boolean jj_3R_OrExpression_2034_5_33() {
        Token xsp;
        if (jj_3R_AndExpression_2011_5_40()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_OrExpression_2036_9_41());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_AdditiveExpression_1829_17_39() {
        return jj_scan_token(121);
    }

    private boolean jj_3R_AdditiveExpression_1827_17_38() {
        return jj_scan_token(120);
    }

    private boolean jj_3_1() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(120)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(121);
        }
        return false;
    }

    private boolean jj_3_6() {
        return jj_scan_token(127);
    }

    private boolean jj_3R_DotVariable_2399_13_99() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(115)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(116)) {
                this.jj_scanpos = xsp;
                if (jj_scan_token(117)) {
                    this.jj_scanpos = xsp;
                    if (jj_scan_token(118)) {
                        this.jj_scanpos = xsp;
                        if (jj_scan_token(95)) {
                            this.jj_scanpos = xsp;
                            if (jj_scan_token(96)) {
                                this.jj_scanpos = xsp;
                                if (jj_scan_token(139)) {
                                    this.jj_scanpos = xsp;
                                    if (jj_scan_token(140)) {
                                        this.jj_scanpos = xsp;
                                        return jj_scan_token(141);
                                    }
                                    return false;
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3_13() {
        return jj_scan_token(135);
    }

    private boolean jj_3R_AdditiveExpression_1824_9_32() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_AdditiveExpression_1827_17_38()) {
            this.jj_scanpos = xsp;
            if (jj_3R_AdditiveExpression_1829_17_39()) {
                return true;
            }
        }
        return jj_3R_MultiplicativeExpression_1862_5_31();
    }

    private boolean jj_3R_DotVariable_2395_9_86() {
        if (jj_scan_token(99)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(142)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(122)) {
                this.jj_scanpos = xsp;
                if (jj_scan_token(123)) {
                    this.jj_scanpos = xsp;
                    return jj_3R_DotVariable_2399_13_99();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_AndExpression_2013_9_51() {
        return jj_scan_token(127) || jj_3R_EqualityExpression_1895_5_50();
    }

    private boolean jj_3_14() {
        return jj_scan_token(142) || jj_scan_token(105);
    }

    private boolean jj_3R_AdditiveExpression_1822_5_28() {
        Token xsp;
        if (jj_3R_MultiplicativeExpression_1862_5_31()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_AdditiveExpression_1824_9_32());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_BuiltIn_2202_5_89() {
        if (jj_scan_token(103) || jj_scan_token(142)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_BuiltIn_2264_9_100()) {
            this.jj_scanpos = xsp;
        }
        Token xsp2 = this.jj_scanpos;
        if (jj_3R_BuiltIn_2280_9_101()) {
            this.jj_scanpos = xsp2;
        }
        Token xsp3 = this.jj_scanpos;
        if (jj_3R_BuiltIn_2294_9_102()) {
            this.jj_scanpos = xsp3;
            return false;
        }
        return false;
    }

    private boolean jj_3R_AndExpression_2011_5_40() {
        Token xsp;
        if (jj_3R_EqualityExpression_1895_5_50()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_AndExpression_2013_9_51());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_5() {
        return jj_3R_AdditiveExpression_1822_5_28();
    }

    private boolean jj_3R_HashLiteral_2566_13_107() {
        if (jj_scan_token(130) || jj_3R_Expression_1668_5_29()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(130)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(132)) {
                return true;
            }
        }
        return jj_3R_Expression_1668_5_29();
    }

    private boolean jj_3R_UnaryPlusMinusExpression_1806_9_58() {
        return jj_scan_token(121);
    }

    private boolean jj_3R_LambdaExpressionParameterList_2364_21_52() {
        return jj_scan_token(130) || jj_3R_Identifier_2092_5_43();
    }

    private boolean jj_3R_LambdaExpressionParameterList_2374_9_35() {
        return jj_3R_Identifier_2092_5_43();
    }

    private boolean jj_3R_RangeExpression_1978_21_106() {
        return jj_3R_AdditiveExpression_1822_5_28();
    }

    private boolean jj_3R_UnaryPlusMinusExpression_1803_5_53() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(120)) {
            this.jj_scanpos = xsp;
            if (jj_3R_UnaryPlusMinusExpression_1806_9_58()) {
                return true;
            }
        }
        return jj_3R_PrimaryExpression_1684_5_55();
    }

    private boolean jj_3R_Exists_2184_5_91() {
        return jj_scan_token(104);
    }

    private boolean jj_3_8() {
        return jj_3R_Expression_1668_5_29();
    }

    private boolean jj_3R_HashLiteral_2557_9_94() {
        Token xsp;
        if (jj_3R_Expression_1668_5_29()) {
            return true;
        }
        Token xsp2 = this.jj_scanpos;
        if (jj_scan_token(130)) {
            this.jj_scanpos = xsp2;
            if (jj_scan_token(132)) {
                return true;
            }
        }
        if (jj_3R_Expression_1668_5_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_HashLiteral_2566_13_107());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_LambdaExpressionParameterList_2358_17_42() {
        Token xsp;
        if (jj_3R_Identifier_2092_5_43()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_LambdaExpressionParameterList_2364_21_52());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_RangeExpression_1970_21_105() {
        return jj_scan_token(102);
    }

    private boolean jj_3R_RangeExpression_1968_21_104() {
        return jj_scan_token(101);
    }

    private boolean jj_3R_DefaultTo_2162_17_110() {
        return jj_3R_Expression_1668_5_29();
    }

    private boolean jj_3R_RangeExpression_1975_13_93() {
        if (jj_scan_token(100)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_RangeExpression_1978_21_106()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_HashLiteral_2555_5_80() {
        if (jj_scan_token(137)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_HashLiteral_2557_9_94()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(138);
    }

    private boolean jj_3R_NotExpression_1782_9_59() {
        return jj_scan_token(129);
    }

    private boolean jj_3R_LambdaExpressionParameterList_2355_9_34() {
        if (jj_scan_token(135)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_LambdaExpressionParameterList_2358_17_42()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(136);
    }

    private boolean jj_3R_RangeExpression_1966_13_92() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_RangeExpression_1968_21_104()) {
            this.jj_scanpos = xsp;
            if (jj_3R_RangeExpression_1970_21_105()) {
                return true;
            }
        }
        return jj_3R_AdditiveExpression_1822_5_28();
    }

    private boolean jj_3_11() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        Token xsp2 = this.jj_scanpos;
        if (jj_scan_token(142)) {
            this.jj_scanpos = xsp2;
            if (jj_scan_token(93)) {
                return true;
            }
        }
        Token xsp3 = this.jj_scanpos;
        if (jj_scan_token(105)) {
            this.jj_scanpos = xsp3;
            if (jj_scan_token(108)) {
                this.jj_scanpos = xsp3;
                if (jj_scan_token(109)) {
                    this.jj_scanpos = xsp3;
                    if (jj_scan_token(110)) {
                        this.jj_scanpos = xsp3;
                        if (jj_scan_token(111)) {
                            this.jj_scanpos = xsp3;
                            if (jj_scan_token(112)) {
                                this.jj_scanpos = xsp3;
                                if (jj_scan_token(113)) {
                                    this.jj_scanpos = xsp3;
                                    return jj_scan_token(114);
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_DefaultTo_2159_9_103() {
        if (jj_scan_token(129)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_DefaultTo_2162_17_110()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_NotExpression_1781_5_54() {
        Token xsp;
        if (jj_3R_NotExpression_1782_9_59()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_NotExpression_1782_9_59());
        this.jj_scanpos = xsp;
        return jj_3R_PrimaryExpression_1684_5_55();
    }

    private boolean jj_3R_LambdaExpressionParameterList_2354_5_30() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_LambdaExpressionParameterList_2355_9_34()) {
            this.jj_scanpos = xsp;
            return jj_3R_LambdaExpressionParameterList_2374_9_35();
        }
        return false;
    }

    private boolean jj_3R_BooleanLiteral_2538_9_97() {
        return jj_scan_token(96);
    }

    private boolean jj_3R_RangeExpression_1964_9_78() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_RangeExpression_1966_13_92()) {
            this.jj_scanpos = xsp;
            return jj_3R_RangeExpression_1975_13_93();
        }
        return false;
    }

    private boolean jj_3R_BooleanLiteral_2536_9_96() {
        return jj_scan_token(95);
    }

    private boolean jj_3R_DefaultTo_2156_5_90() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(153)) {
            this.jj_scanpos = xsp;
            return jj_3R_DefaultTo_2159_9_103();
        }
        return false;
    }

    private boolean jj_3R_BooleanLiteral_2535_5_82() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_BooleanLiteral_2536_9_96()) {
            this.jj_scanpos = xsp;
            return jj_3R_BooleanLiteral_2538_9_97();
        }
        return false;
    }

    private boolean jj_3R_UnaryExpression_1767_9_46() {
        return jj_3R_PrimaryExpression_1684_5_55();
    }

    private boolean jj_3R_RangeExpression_1962_5_62() {
        if (jj_3R_AdditiveExpression_1822_5_28()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_RangeExpression_1964_9_78()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3R_LocalLambdaExpression_2339_9_116() {
        return jj_3R_OrExpression_2034_5_33();
    }

    private boolean jj_3R_UnaryExpression_1765_9_45() {
        return jj_3R_NotExpression_1781_5_54();
    }

    private boolean jj_3R_UnaryExpression_1763_9_44() {
        return jj_3R_UnaryPlusMinusExpression_1803_5_53();
    }

    private boolean jj_3_10() {
        return jj_3R_LambdaExpressionParameterList_2354_5_30() || jj_scan_token(119);
    }

    private boolean jj_3R_UnaryExpression_1762_5_36() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_UnaryExpression_1763_9_44()) {
            this.jj_scanpos = xsp;
            if (jj_3R_UnaryExpression_1765_9_45()) {
                this.jj_scanpos = xsp;
                return jj_3R_UnaryExpression_1767_9_46();
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_LocalLambdaExpression_2322_9_115() {
        return jj_3R_LambdaExpressionParameterList_2354_5_30() || jj_scan_token(119) || jj_3R_OrExpression_2034_5_33();
    }

    private boolean jj_3_4() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(151)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(118)) {
                this.jj_scanpos = xsp;
                if (jj_scan_token(150)) {
                    this.jj_scanpos = xsp;
                    if (jj_scan_token(117)) {
                        this.jj_scanpos = xsp;
                        if (jj_scan_token(116)) {
                            this.jj_scanpos = xsp;
                            if (jj_scan_token(116)) {
                                this.jj_scanpos = xsp;
                                return jj_scan_token(115);
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_LocalLambdaExpression_2321_5_113() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_LocalLambdaExpression_2322_9_115()) {
            this.jj_scanpos = xsp;
            return jj_3R_LocalLambdaExpression_2339_9_116();
        }
        return false;
    }

    private boolean jj_3R_Parenthesis_1741_5_84() {
        return jj_scan_token(135) || jj_3R_Expression_1668_5_29() || jj_scan_token(136);
    }

    private boolean jj_3R_RelationalExpression_1928_9_63() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(151)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(118)) {
                this.jj_scanpos = xsp;
                if (jj_scan_token(150)) {
                    this.jj_scanpos = xsp;
                    if (jj_scan_token(117)) {
                        this.jj_scanpos = xsp;
                        if (jj_scan_token(116)) {
                            this.jj_scanpos = xsp;
                            if (jj_scan_token(115)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return jj_3R_RangeExpression_1962_5_62();
    }

    private boolean jj_3R_BuiltinVariable_2120_5_85() {
        return jj_scan_token(99) || jj_scan_token(142);
    }

    private boolean jj_3R_RelationalExpression_1926_5_56() {
        if (jj_3R_RangeExpression_1962_5_62()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_RelationalExpression_1928_9_63()) {
            this.jj_scanpos = xsp;
            return false;
        }
        return false;
    }

    private boolean jj_3_9() {
        return jj_scan_token(135);
    }

    private boolean jj_3_12() {
        return jj_scan_token(142) || jj_scan_token(105);
    }

    private boolean jj_3R_StringLiteral_2492_9_95() {
        return jj_scan_token(94);
    }

    private boolean jj_3R_AtomicExpression_1728_9_71() {
        return jj_3R_BuiltinVariable_2120_5_85();
    }

    private boolean jj_3R_AtomicExpression_1726_9_70() {
        return jj_3R_Parenthesis_1741_5_84();
    }

    private boolean jj_3R_AtomicExpression_1724_9_69() {
        return jj_3R_Identifier_2092_5_43();
    }

    private boolean jj_3R_AtomicExpression_1722_9_68() {
        return jj_3R_ListLiteral_2058_5_83();
    }

    private boolean jj_3R_BuiltIn_2294_9_102() {
        return jj_3R_MethodArgs_2465_9_88();
    }

    private boolean jj_3R_StringLiteral_2489_5_81() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(93)) {
            this.jj_scanpos = xsp;
            return jj_3R_StringLiteral_2492_9_95();
        }
        return false;
    }

    private boolean jj_3R_AtomicExpression_1720_9_67() {
        return jj_3R_BooleanLiteral_2535_5_82();
    }

    private boolean jj_3R_AtomicExpression_1718_9_66() {
        return jj_3R_StringLiteral_2489_5_81();
    }

    private boolean jj_3R_AtomicExpression_1716_9_65() {
        return jj_3R_HashLiteral_2555_5_80();
    }

    private boolean jj_3_3() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(107)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(105)) {
                this.jj_scanpos = xsp;
                return jj_scan_token(106);
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_AtomicExpression_1714_9_64() {
        return jj_3R_NumberLiteral_2073_5_79();
    }

    private boolean jj_3_17() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(79)) {
            this.jj_scanpos = xsp;
        }
        Token xsp2 = this.jj_scanpos;
        if (jj_scan_token(77)) {
            this.jj_scanpos = xsp2;
            return jj_scan_token(76);
        }
        return false;
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
        jj_la1_init_3();
        jj_la1_init_4();
        jj_ls = new LookaheadSuccess();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, SSL.SSL_OP_NO_TLSv1, 33554432, 0, 0, Opcodes.ASM7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6291456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32768, 0, 32768, 32768, 0, -33472, 0, 0, 0, 0, -33472, -33472, -33472, -33472, -33472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4194304, 768, 0, 0, 4194304, 0, 128, 0, 0, 0, 0, 33554432, SSL.SSL_OP_NO_TLSv1, 0, -268435456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14336, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 6, 0, 0, 0, 0, 0, -8388593, 0, 0, 0, 0, -8388593, -8388593, -8388593, -8388593, -8388593, 0, 0, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{0, 0, -536870912, -536870912, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1610612736, 0, -536870912, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 1610612736, Integer.MIN_VALUE, 0, 0, 0, -536870912, 1310720, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, 24, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, -536870912, -536870912, 0, -536870912, 0, 0, 0, 0, 32768, 1, 32768, 1, 1, 0, 1406, 229376, 229376, 0, 0, 2065790, 2065790, 2065790, 2065790, 2065790, 2064384, 2064384, 32768, 32768, 32768, 0, File.APR_FINFO_IDENT, 0, 0, 2064384, 229376, 2064384};
    }

    private static void jj_la1_init_3() {
        jj_la1_3 = new int[]{392, 392, 15, 50331663, 0, 50331648, 50331648, 1677721600, 3584, 7864320, 96, 112, 112, 6, 0, 0, 50331663, 0, 0, 0, 7864321, 209190913, 0, 1, 0, 0, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 127488, Opcodes.ASM6, 520704, 0, 127488, Opcodes.ASM6, 520704, 0, 0, 0, 520704, 0, 0, 0, 0, 0, 268435456, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50331663, 0, 50331663, 50331663, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_4() {
        jj_la1_4 = new int[]{33554594, 33554594, 17056, 17058, 2, 0, 0, 0, 0, 12582912, 0, 0, 0, 0, 16384, 33554434, 17058, 4, 16384, 16512, 14336, 30720, 0, 0, 20, 4, 20, 17058, 0, 8, 0, 0, 0, 4, 4096, 0, 4, 0, 8192, 17058, 8192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2048, 2048, 0, 1050624, 8, 16384, 0, 128, 16384, 0, 0, 4, 256, 0, 16777216, 16777216, 16777220, 16777216, 16777216, 16793600, 8, 3145728, 256, 16384, 17062, 4, 17058, 17062, 4, 17058, 0, 8, 16384, 3145728, 0, 0, 0, 0, 0, 3145728, 0, 0, 0, 1006632960, 1006632960, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16384, 0, 4, 16384, 0, 0, 0};
    }

    public FMParser(InputStream stream) {
        this(stream, null);
    }

    public FMParser(InputStream stream, String encoding) {
        this.escapes = new LinkedList();
        this.jj_la1 = new int[116];
        this.jj_2_rtns = new JJCalls[17];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
            this.token_source = new FMParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 116; i++) {
                this.jj_la1[i] = -1;
            }
            for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                this.jj_2_rtns[i2] = new JJCalls();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReInit(InputStream stream) {
        ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
            this.token_source.ReInit(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 116; i++) {
                this.jj_la1[i] = -1;
            }
            for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                this.jj_2_rtns[i2] = new JJCalls();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public FMParser(Reader stream) {
        this.escapes = new LinkedList();
        this.jj_la1 = new int[116];
        this.jj_2_rtns = new JJCalls[17];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new FMParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 116; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        if (this.jj_input_stream == null) {
            this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        } else {
            this.jj_input_stream.ReInit(stream, 1, 1);
        }
        if (this.token_source == null) {
            this.token_source = new FMParserTokenManager(this.jj_input_stream);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 116; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public FMParser(FMParserTokenManager tm) {
        this.escapes = new LinkedList();
        this.jj_la1 = new int[116];
        this.jj_2_rtns = new JJCalls[17];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 116; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public void ReInit(FMParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 116; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        if (oldToken.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            this.jj_gen++;
            int i = this.jj_gc + 1;
            this.jj_gc = i;
            if (i > 100) {
                this.jj_gc = 0;
                for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                    JJCalls jJCalls = this.jj_2_rtns[i2];
                    while (true) {
                        JJCalls c = jJCalls;
                        if (c != null) {
                            if (c.gen < this.jj_gen) {
                                c.first = null;
                            }
                            jJCalls = c.next;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw generateParseException();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FMParser$LookaheadSuccess.class */
    private static final class LookaheadSuccess extends Error {
        private LookaheadSuccess() {
        }

        @Override // java.lang.Throwable
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    private boolean jj_scan_token(int kind) {
        Token tok;
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;
            if (this.jj_scanpos.next == null) {
                Token token = this.jj_scanpos;
                Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            } else {
                Token token2 = this.jj_scanpos.next;
                this.jj_scanpos = token2;
                this.jj_lastpos = token2;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token token3 = this.token;
            while (true) {
                tok = token3;
                if (tok == null || tok == this.jj_scanpos) {
                    break;
                }
                i++;
                token3 = tok.next;
            }
            if (tok != null) {
                jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int index) {
        Token token;
        Token t = this.token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                token = t.next;
            } else {
                Token nextToken = this.token_source.getNextToken();
                token = nextToken;
                t.next = nextToken;
            }
            t = token;
        }
        return t;
    }

    private int jj_ntk_f() {
        Token token = this.token.next;
        this.jj_nt = token;
        if (token == null) {
            Token token2 = this.token;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            int i = nextToken.kind;
            this.jj_ntk = i;
            return i;
        }
        int i2 = this.jj_nt.kind;
        this.jj_ntk = i2;
        return i2;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            int[] iArr = this.jj_lasttokens;
            int i = this.jj_endpos;
            this.jj_endpos = i + 1;
            iArr[i] = kind;
            return;
        }
        if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i2 = 0; i2 < this.jj_endpos; i2++) {
                this.jj_expentry[i2] = this.jj_lasttokens[i2];
            }
            Iterator<int[]> it = this.jj_expentries.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int[] oldentry = it.next();
                if (oldentry.length == this.jj_expentry.length) {
                    boolean isMatched = true;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= this.jj_expentry.length) {
                            break;
                        }
                        if (oldentry[i3] == this.jj_expentry[i3]) {
                            i3++;
                        } else {
                            isMatched = false;
                            break;
                        }
                    }
                    if (isMatched) {
                        this.jj_expentries.add(this.jj_expentry);
                        break;
                    }
                }
            }
            if (pos != 0) {
                int[] iArr2 = this.jj_lasttokens;
                this.jj_endpos = pos;
                iArr2[pos - 1] = kind;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [int[], int[][]] */
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[158];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 116; i++) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                    if ((jj_la1_1[i] & (1 << j)) != 0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((jj_la1_2[i] & (1 << j)) != 0) {
                        la1tokens[64 + j] = true;
                    }
                    if ((jj_la1_3[i] & (1 << j)) != 0) {
                        la1tokens[96 + j] = true;
                    }
                    if ((jj_la1_4[i] & (1 << j)) != 0) {
                        la1tokens[128 + j] = true;
                    }
                }
            }
        }
        for (int i2 = 0; i2 < 158; i2++) {
            if (la1tokens[i2]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i2;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        ?? r0 = new int[this.jj_expentries.size()];
        for (int i3 = 0; i3 < this.jj_expentries.size(); i3++) {
            r0[i3] = this.jj_expentries.get(i3);
        }
        return new ParseException(this.token, (int[][]) r0, tokenImage);
    }

    public final boolean trace_enabled() {
        return this.trace_enabled;
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 17; i++) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        Token token = p.first;
                        this.jj_scanpos = token;
                        this.jj_lastpos = token;
                        switch (i) {
                            case 0:
                                jj_3_1();
                                break;
                            case 1:
                                jj_3_2();
                                break;
                            case 2:
                                jj_3_3();
                                break;
                            case 3:
                                jj_3_4();
                                break;
                            case 4:
                                jj_3_5();
                                break;
                            case 5:
                                jj_3_6();
                                break;
                            case 6:
                                jj_3_7();
                                break;
                            case 7:
                                jj_3_8();
                                break;
                            case 8:
                                jj_3_9();
                                break;
                            case 9:
                                jj_3_10();
                                break;
                            case 10:
                                jj_3_11();
                                break;
                            case 11:
                                jj_3_12();
                                break;
                            case 12:
                                jj_3_13();
                                break;
                            case 13:
                                jj_3_14();
                                break;
                            case 14:
                                jj_3_15();
                                break;
                            case 15:
                                jj_3_16();
                                break;
                            case 16:
                                jj_3_17();
                                break;
                        }
                    }
                    p = p.next;
                } while (p != null);
            } catch (LookaheadSuccess e) {
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p;
        JJCalls jJCalls = this.jj_2_rtns[index];
        while (true) {
            p = jJCalls;
            if (p.gen <= this.jj_gen) {
                break;
            }
            if (p.next == null) {
                JJCalls jJCalls2 = new JJCalls();
                p.next = jJCalls2;
                p = jJCalls2;
                break;
            }
            jJCalls = p.next;
        }
        p.gen = (this.jj_gen + xla) - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/FMParser$JJCalls.class */
    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }
}
