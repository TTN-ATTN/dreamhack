package freemarker.core;

import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import freemarker.cache.AndMatcher;
import freemarker.cache.ConditionalTemplateConfigurationFactory;
import freemarker.cache.FileExtensionMatcher;
import freemarker.cache.FileNameGlobMatcher;
import freemarker.cache.FirstMatchTemplateConfigurationFactory;
import freemarker.cache.MergingTemplateConfigurationFactory;
import freemarker.cache.NotMatcher;
import freemarker.cache.OrMatcher;
import freemarker.cache.PathGlobMatcher;
import freemarker.cache.PathRegexMatcher;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator.class */
public class _ObjectBuilderSettingEvaluator {
    private static final String INSTANCE_FIELD_NAME = "INSTANCE";
    private static final String BUILD_METHOD_NAME = "build";
    private static final String BUILDER_CLASS_POSTFIX = "Builder";
    private static Map<String, String> SHORTHANDS;
    private static final Object VOID = new Object();
    private final String src;
    private final Class expectedClass;
    private final boolean allowNull;
    private final _SettingEvaluationEnvironment env;
    private int pos;
    private boolean modernMode = false;

    private _ObjectBuilderSettingEvaluator(String src, int pos, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) {
        this.src = src;
        this.pos = pos;
        this.expectedClass = expectedClass;
        this.allowNull = allowNull;
        this.env = env;
    }

    public static Object eval(String src, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) throws IllegalAccessException, InstantiationException, ClassNotFoundException, _ObjectBuilderSettingEvaluationException {
        return new _ObjectBuilderSettingEvaluator(src, 0, expectedClass, allowNull, env).eval();
    }

    public static int configureBean(String argumentListSrc, int posAfterOpenParen, Object bean, _SettingEvaluationEnvironment env) throws IllegalAccessException, InstantiationException, ClassNotFoundException, _ObjectBuilderSettingEvaluationException {
        return new _ObjectBuilderSettingEvaluator(argumentListSrc, posAfterOpenParen, bean.getClass(), true, env).configureBean(bean);
    }

    private Object eval() throws IllegalAccessException, InstantiationException, ClassNotFoundException, _ObjectBuilderSettingEvaluationException {
        Object value;
        skipWS();
        try {
            value = ensureEvaled(fetchValue(false, true, false, true));
        } catch (LegacyExceptionWrapperSettingEvaluationExpression e) {
            e.rethrowLegacy();
            value = null;
        }
        skipWS();
        if (this.pos != this.src.length()) {
            throw new _ObjectBuilderSettingEvaluationException("end-of-expression", this.src, this.pos);
        }
        if (value == null && !this.allowNull) {
            throw new _ObjectBuilderSettingEvaluationException("Value can't be null.");
        }
        if (value != null && !this.expectedClass.isInstance(value)) {
            throw new _ObjectBuilderSettingEvaluationException("The resulting object (of class " + value.getClass() + ") is not a(n) " + this.expectedClass.getName() + ".");
        }
        return value;
    }

    private int configureBean(Object bean) throws IllegalAccessException, InstantiationException, ClassNotFoundException, _ObjectBuilderSettingEvaluationException {
        PropertyAssignmentsExpression propAssignments = new PropertyAssignmentsExpression(bean);
        fetchParameterListInto(propAssignments);
        skipWS();
        propAssignments.eval();
        return this.pos;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object ensureEvaled(Object value) throws _ObjectBuilderSettingEvaluationException {
        return value instanceof SettingExpression ? ((SettingExpression) value).eval() : value;
    }

    private Object fetchBuilderCall(boolean optional, boolean topLevel) throws _ObjectBuilderSettingEvaluationException {
        int startPos = this.pos;
        BuilderCallExpression exp = new BuilderCallExpression();
        exp.canBeStaticField = true;
        String fetchedClassName = fetchClassName(optional);
        if (fetchedClassName == null) {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
            }
            return VOID;
        }
        exp.className = shorthandToFullQualified(fetchedClassName);
        if (!fetchedClassName.equals(exp.className)) {
            this.modernMode = true;
            exp.canBeStaticField = false;
        }
        skipWS();
        char openParen = fetchOptionalChar("(");
        if (openParen == 0 && !topLevel) {
            if (fetchedClassName.indexOf(46) == -1) {
                this.pos = startPos;
                return VOID;
            }
            exp.mustBeStaticField = true;
        }
        if (openParen != 0) {
            fetchParameterListInto(exp);
            exp.canBeStaticField = false;
        }
        return exp;
    }

    private void fetchParameterListInto(ExpressionWithParameters exp) throws _ObjectBuilderSettingEvaluationException {
        this.modernMode = true;
        skipWS();
        if (fetchOptionalChar(")") != ')') {
            do {
                skipWS();
                Object paramNameOrValue = fetchValue(false, false, true, false);
                if (paramNameOrValue != VOID) {
                    skipWS();
                    if (!(paramNameOrValue instanceof Name)) {
                        if (!exp.namedParamNames.isEmpty()) {
                            throw new _ObjectBuilderSettingEvaluationException("Positional parameters must precede named parameters");
                        }
                        if (!exp.getAllowPositionalParameters()) {
                            throw new _ObjectBuilderSettingEvaluationException("Positional parameters not supported here");
                        }
                        exp.positionalParamValues.add(ensureEvaled(paramNameOrValue));
                    } else {
                        exp.namedParamNames.add(((Name) paramNameOrValue).name);
                        skipWS();
                        fetchRequiredChar("=");
                        skipWS();
                        Object paramValue = fetchValue(false, false, true, true);
                        exp.namedParamValues.add(ensureEvaled(paramValue));
                    }
                    skipWS();
                }
            } while (fetchRequiredChar(",)") == ',');
        }
    }

    private Object fetchValue(boolean optional, boolean topLevel, boolean resultCoerced, boolean resolveVariables) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos < this.src.length()) {
            Object val = fetchNumberLike(true, resultCoerced);
            if (val != VOID) {
                return val;
            }
            Object val2 = fetchStringLiteral(true);
            if (val2 != VOID) {
                return val2;
            }
            Object val3 = fetchListLiteral(true);
            if (val3 != VOID) {
                return val3;
            }
            Object val4 = fetchMapLiteral(true);
            if (val4 != VOID) {
                return val4;
            }
            Object val5 = fetchBuilderCall(true, topLevel);
            if (val5 != VOID) {
                return val5;
            }
            String name = fetchSimpleName(true);
            if (name != null) {
                Object val6 = keywordToValueOrVoid(name);
                if (val6 != VOID) {
                    return val6;
                }
                if (resolveVariables) {
                    throw new _ObjectBuilderSettingEvaluationException("Can't resolve variable reference: " + name);
                }
                return new Name(name);
            }
        }
        if (optional) {
            return VOID;
        }
        throw new _ObjectBuilderSettingEvaluationException("value or name", this.src, this.pos);
    }

    private boolean isKeyword(String name) {
        return keywordToValueOrVoid(name) != VOID;
    }

    private Object keywordToValueOrVoid(String name) {
        if (name.equals("true")) {
            return Boolean.TRUE;
        }
        if (name.equals("false")) {
            return Boolean.FALSE;
        }
        if (name.equals(BeanDefinitionParserDelegate.NULL_ELEMENT)) {
            return null;
        }
        return VOID;
    }

    private String fetchSimpleName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        char c = this.pos < this.src.length() ? this.src.charAt(this.pos) : (char) 0;
        if (!isIdentifierStart(c)) {
            if (optional) {
                return null;
            }
            throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
        }
        int startPos = this.pos;
        this.pos++;
        while (this.pos != this.src.length()) {
            char c2 = this.src.charAt(this.pos);
            if (!isIdentifierMiddle(c2)) {
                break;
            }
            this.pos++;
        }
        return this.src.substring(startPos, this.pos);
    }

    private String fetchClassName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        int startPos = this.pos;
        StringBuilder sb = new StringBuilder();
        while (true) {
            String name = fetchSimpleName(true);
            if (name == null) {
                if (!optional) {
                    throw new _ObjectBuilderSettingEvaluationException("name", this.src, this.pos);
                }
                this.pos = startPos;
                return null;
            }
            sb.append(name);
            skipWS();
            if (this.pos >= this.src.length() || this.src.charAt(this.pos) != '.') {
                break;
            }
            sb.append('.');
            this.pos++;
            skipWS();
        }
        String className = sb.toString();
        if (isKeyword(className)) {
            this.pos = startPos;
            return null;
        }
        return className;
    }

    private Object fetchNumberLike(boolean optional, boolean resultCoerced) throws _ObjectBuilderSettingEvaluationException {
        String strValueOf;
        int startPos = this.pos;
        boolean isVersion = false;
        boolean hasDot = false;
        while (this.pos != this.src.length()) {
            char c = this.src.charAt(this.pos);
            if (c == '.') {
                if (hasDot) {
                    isVersion = true;
                } else {
                    hasDot = true;
                }
            } else if (!isASCIIDigit(c) && c != '-') {
                break;
            }
            this.pos++;
        }
        if (startPos == this.pos) {
            if (optional) {
                return VOID;
            }
            throw new _ObjectBuilderSettingEvaluationException("number-like", this.src, this.pos);
        }
        String numStr = this.src.substring(startPos, this.pos);
        if (isVersion) {
            try {
                return new Version(numStr);
            } catch (IllegalArgumentException e) {
                throw new _ObjectBuilderSettingEvaluationException("Malformed version number: " + numStr, e);
            }
        }
        String typePostfix = null;
        while (this.pos != this.src.length()) {
            char c2 = this.src.charAt(this.pos);
            if (Character.isLetter(c2)) {
                if (typePostfix == null) {
                    strValueOf = String.valueOf(c2);
                } else {
                    strValueOf = typePostfix + c2;
                }
                typePostfix = strValueOf;
                this.pos++;
            }
        }
        try {
            if (numStr.endsWith(".")) {
                throw new NumberFormatException("A number can't end with a dot");
            }
            if (numStr.startsWith(".") || numStr.startsWith("-.") || numStr.startsWith("+.")) {
                throw new NumberFormatException("A number can't start with a dot");
            }
            if (typePostfix == null) {
                if (numStr.indexOf(46) == -1) {
                    BigInteger biNum = new BigInteger(numStr);
                    int bitLength = biNum.bitLength();
                    if (bitLength <= 31) {
                        return Integer.valueOf(biNum.intValue());
                    }
                    if (bitLength <= 63) {
                        return Long.valueOf(biNum.longValue());
                    }
                    return biNum;
                }
                if (resultCoerced) {
                    return new BigDecimal(numStr);
                }
                return Double.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("l")) {
                return Long.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("bi")) {
                return new BigInteger(numStr);
            }
            if (typePostfix.equalsIgnoreCase("bd")) {
                return new BigDecimal(numStr);
            }
            if (typePostfix.equalsIgnoreCase(DateTokenConverter.CONVERTER_KEY)) {
                return Double.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("f")) {
                return Float.valueOf(numStr);
            }
            throw new _ObjectBuilderSettingEvaluationException("Unrecognized number type postfix: " + typePostfix);
        } catch (NumberFormatException e2) {
            throw new _ObjectBuilderSettingEvaluationException("Malformed number: " + numStr, e2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x00fc, code lost:
    
        if (r0 != r6.pos) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0100, code lost:
    
        if (r7 == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0106, code lost:
    
        return freemarker.core._ObjectBuilderSettingEvaluator.VOID;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0118, code lost:
    
        throw new freemarker.core._ObjectBuilderSettingEvaluationException("string literal", r6.src, r6.pos);
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0119, code lost:
    
        r0 = r6.src;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0120, code lost:
    
        if (r11 == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0123, code lost:
    
        r2 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0127, code lost:
    
        r2 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0128, code lost:
    
        r0 = r0.substring(r0 + r2, r6.pos);
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0132, code lost:
    
        r6.pos++;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x013e, code lost:
    
        if (r11 == false) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x014b, code lost:
    
        return freemarker.template.utility.StringUtil.FTLStringLiteralDec(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x014c, code lost:
    
        r13 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x016b, code lost:
    
        throw new freemarker.core._ObjectBuilderSettingEvaluationException("Malformed string literal: " + r0, r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:?, code lost:
    
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.Object fetchStringLiteral(boolean r7) throws freemarker.core._ObjectBuilderSettingEvaluationException {
        /*
            Method dump skipped, instructions count: 364
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core._ObjectBuilderSettingEvaluator.fetchStringLiteral(boolean):java.lang.Object");
    }

    private Object fetchListLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos == this.src.length() || this.src.charAt(this.pos) != '[') {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException(PropertyAccessor.PROPERTY_KEY_PREFIX, this.src, this.pos);
            }
            return VOID;
        }
        this.pos++;
        ListExpression listExp = new ListExpression();
        while (true) {
            skipWS();
            if (fetchOptionalChar("]") != 0) {
                return listExp;
            }
            if (listExp.itemCount() != 0) {
                fetchRequiredChar(",");
                skipWS();
            }
            listExp.addItem(fetchValue(false, false, false, true));
            skipWS();
        }
    }

    private Object fetchMapLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos == this.src.length() || this.src.charAt(this.pos) != '{') {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException("{", this.src, this.pos);
            }
            return VOID;
        }
        this.pos++;
        MapExpression mapExp = new MapExpression();
        while (true) {
            skipWS();
            if (fetchOptionalChar("}") != 0) {
                return mapExp;
            }
            if (mapExp.itemCount() != 0) {
                fetchRequiredChar(",");
                skipWS();
            }
            Object key = fetchValue(false, false, false, true);
            skipWS();
            fetchRequiredChar(":");
            skipWS();
            Object value = fetchValue(false, false, false, true);
            mapExp.addItem(new KeyValuePair(key, value));
            skipWS();
        }
    }

    private void skipWS() {
        while (this.pos != this.src.length()) {
            char c = this.src.charAt(this.pos);
            if (!Character.isWhitespace(c)) {
                return;
            } else {
                this.pos++;
            }
        }
    }

    private char fetchOptionalChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
        return fetchChar(expectedChars, true);
    }

    private char fetchRequiredChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
        return fetchChar(expectedChars, false);
    }

    private char fetchChar(String expectedChars, boolean optional) throws _ObjectBuilderSettingEvaluationException {
        char c = this.pos < this.src.length() ? this.src.charAt(this.pos) : (char) 0;
        if (expectedChars.indexOf(c) != -1) {
            this.pos++;
            return c;
        }
        if (optional) {
            return (char) 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expectedChars.length(); i++) {
            if (i != 0) {
                sb.append(" or ");
            }
            sb.append(StringUtil.jQuote(expectedChars.substring(i, i + 1)));
        }
        throw new _ObjectBuilderSettingEvaluationException(sb.toString(), this.src, this.pos);
    }

    private boolean isASCIIDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }

    private boolean isIdentifierMiddle(char c) {
        return isIdentifierStart(c) || isASCIIDigit(c);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized String shorthandToFullQualified(String className) {
        if (SHORTHANDS == null) {
            SHORTHANDS = new HashMap();
            addWithSimpleName(SHORTHANDS, DefaultObjectWrapper.class);
            addWithSimpleName(SHORTHANDS, BeansWrapper.class);
            addWithSimpleName(SHORTHANDS, SimpleObjectWrapper.class);
            addWithSimpleName(SHORTHANDS, TemplateConfiguration.class);
            addWithSimpleName(SHORTHANDS, PathGlobMatcher.class);
            addWithSimpleName(SHORTHANDS, FileNameGlobMatcher.class);
            addWithSimpleName(SHORTHANDS, FileExtensionMatcher.class);
            addWithSimpleName(SHORTHANDS, PathRegexMatcher.class);
            addWithSimpleName(SHORTHANDS, AndMatcher.class);
            addWithSimpleName(SHORTHANDS, OrMatcher.class);
            addWithSimpleName(SHORTHANDS, NotMatcher.class);
            addWithSimpleName(SHORTHANDS, ConditionalTemplateConfigurationFactory.class);
            addWithSimpleName(SHORTHANDS, MergingTemplateConfigurationFactory.class);
            addWithSimpleName(SHORTHANDS, FirstMatchTemplateConfigurationFactory.class);
            addWithSimpleName(SHORTHANDS, HTMLOutputFormat.class);
            addWithSimpleName(SHORTHANDS, XHTMLOutputFormat.class);
            addWithSimpleName(SHORTHANDS, XMLOutputFormat.class);
            addWithSimpleName(SHORTHANDS, RTFOutputFormat.class);
            addWithSimpleName(SHORTHANDS, PlainTextOutputFormat.class);
            addWithSimpleName(SHORTHANDS, UndefinedOutputFormat.class);
            addWithSimpleName(SHORTHANDS, DefaultTruncateBuiltinAlgorithm.class);
            addWithSimpleName(SHORTHANDS, Locale.class);
            SHORTHANDS.put("TimeZone", "freemarker.core._TimeZone");
            SHORTHANDS.put("markup", "freemarker.core._Markup");
            addWithSimpleName(SHORTHANDS, Configuration.class);
        }
        String fullClassName = SHORTHANDS.get(className);
        return fullClassName == null ? className : fullClassName;
    }

    private static void addWithSimpleName(Map map, Class<?> pClass) {
        map.put(pClass.getSimpleName(), pClass.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setJavaBeanProperties(Object bean, List namedParamNames, List namedParamValues) throws _ObjectBuilderSettingEvaluationException {
        if (namedParamNames.isEmpty()) {
            return;
        }
        Class cl = bean.getClass();
        try {
            PropertyDescriptor[] propDescs = Introspector.getBeanInfo(cl).getPropertyDescriptors();
            Map beanPropSetters = new HashMap((propDescs.length * 4) / 3, 1.0f);
            for (PropertyDescriptor propDesc : propDescs) {
                Method writeMethod = propDesc.getWriteMethod();
                if (writeMethod != null) {
                    beanPropSetters.put(propDesc.getName(), writeMethod);
                }
            }
            TemplateHashModel beanTM = null;
            for (int i = 0; i < namedParamNames.size(); i++) {
                String name = (String) namedParamNames.get(i);
                if (!beanPropSetters.containsKey(name)) {
                    throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class has no writeable JavaBeans property called " + StringUtil.jQuote(name) + ".");
                }
                Method beanPropSetter = (Method) beanPropSetters.put(name, null);
                if (beanPropSetter == null) {
                    throw new _ObjectBuilderSettingEvaluationException("JavaBeans property " + StringUtil.jQuote(name) + " is set twice.");
                }
                if (beanTM == null) {
                    try {
                        TemplateModel wrappedObj = this.env.getObjectWrapper().wrap(bean);
                        if (!(wrappedObj instanceof TemplateHashModel)) {
                            throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class is not a wrapped as TemplateHashModel.");
                        }
                        beanTM = (TemplateHashModel) wrappedObj;
                    } catch (Exception e) {
                        throw new _ObjectBuilderSettingEvaluationException("Failed to set " + StringUtil.jQuote(name), e);
                    }
                }
                TemplateModel m = beanTM.get(beanPropSetter.getName());
                if (m == null) {
                    throw new _ObjectBuilderSettingEvaluationException("Can't find " + beanPropSetter + " as FreeMarker method.");
                }
                if (!(m instanceof TemplateMethodModelEx)) {
                    throw new _ObjectBuilderSettingEvaluationException(StringUtil.jQuote(beanPropSetter.getName()) + " wasn't a TemplateMethodModelEx.");
                }
                List args = new ArrayList();
                args.add(this.env.getObjectWrapper().wrap(namedParamValues.get(i)));
                ((TemplateMethodModelEx) m).exec(args);
            }
        } catch (Exception e2) {
            throw new _ObjectBuilderSettingEvaluationException("Failed to inspect " + cl.getName() + " class", e2);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$Name.class */
    private static class Name {
        private final String name;

        public Name(String name) {
            this.name = name;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$SettingExpression.class */
    private static abstract class SettingExpression {
        abstract Object eval() throws _ObjectBuilderSettingEvaluationException;

        private SettingExpression() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$ExpressionWithParameters.class */
    private abstract class ExpressionWithParameters extends SettingExpression {
        protected List positionalParamValues;
        protected List namedParamNames;
        protected List namedParamValues;

        protected abstract boolean getAllowPositionalParameters();

        private ExpressionWithParameters() {
            super();
            this.positionalParamValues = new ArrayList();
            this.namedParamNames = new ArrayList();
            this.namedParamValues = new ArrayList();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$ListExpression.class */
    private class ListExpression extends SettingExpression {
        private List<Object> items;

        private ListExpression() {
            super();
            this.items = new ArrayList();
        }

        void addItem(Object item) {
            this.items.add(item);
        }

        public int itemCount() {
            return this.items.size();
        }

        @Override // freemarker.core._ObjectBuilderSettingEvaluator.SettingExpression
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            ArrayList res = new ArrayList(this.items.size());
            for (Object item : this.items) {
                res.add(_ObjectBuilderSettingEvaluator.this.ensureEvaled(item));
            }
            return res;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$MapExpression.class */
    private class MapExpression extends SettingExpression {
        private List<KeyValuePair> items;

        private MapExpression() {
            super();
            this.items = new ArrayList();
        }

        void addItem(KeyValuePair item) {
            this.items.add(item);
        }

        public int itemCount() {
            return this.items.size();
        }

        @Override // freemarker.core._ObjectBuilderSettingEvaluator.SettingExpression
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            LinkedHashMap res = new LinkedHashMap((this.items.size() * 4) / 3, 1.0f);
            for (KeyValuePair item : this.items) {
                Object key = _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.key);
                if (key == null) {
                    throw new _ObjectBuilderSettingEvaluationException("Map can't use null as key.");
                }
                res.put(key, _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.value));
            }
            return res;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$KeyValuePair.class */
    private static class KeyValuePair {
        private final Object key;
        private final Object value;

        public KeyValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$BuilderCallExpression.class */
    private class BuilderCallExpression extends ExpressionWithParameters {
        private String className;
        private boolean canBeStaticField;
        private boolean mustBeStaticField;

        private BuilderCallExpression() {
            super();
        }

        /* JADX WARN: Removed duplicated region for block: B:49:0x00de  */
        /* JADX WARN: Removed duplicated region for block: B:50:0x00e3  */
        @Override // freemarker.core._ObjectBuilderSettingEvaluator.SettingExpression
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        java.lang.Object eval() throws java.lang.NoSuchFieldException, java.lang.NoSuchMethodException, java.lang.SecurityException, freemarker.core._ObjectBuilderSettingEvaluationException {
            /*
                Method dump skipped, instructions count: 392
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: freemarker.core._ObjectBuilderSettingEvaluator.BuilderCallExpression.eval():java.lang.Object");
        }

        private Object getStaticFieldValue(String dottedName) throws NoSuchFieldException, _ObjectBuilderSettingEvaluationException {
            int lastDotIdx = dottedName.lastIndexOf(46);
            if (lastDotIdx != -1) {
                String className = _ObjectBuilderSettingEvaluator.shorthandToFullQualified(dottedName.substring(0, lastDotIdx));
                String fieldName = dottedName.substring(lastDotIdx + 1);
                try {
                    Class<?> cl = ClassUtil.forName(className);
                    try {
                        Field field = cl.getField(fieldName);
                        if ((field.getModifiers() & 8) == 0) {
                            throw new _ObjectBuilderSettingEvaluationException("Referred field isn't static: " + field);
                        }
                        if ((field.getModifiers() & 1) == 0) {
                            throw new _ObjectBuilderSettingEvaluationException("Referred field isn't public: " + field);
                        }
                        if (field.getName().equals(_ObjectBuilderSettingEvaluator.INSTANCE_FIELD_NAME)) {
                            throw new _ObjectBuilderSettingEvaluationException("The INSTANCE field is only accessible through pseudo-constructor call: " + className + "()");
                        }
                        try {
                            return field.get(null);
                        } catch (Exception e) {
                            throw new _ObjectBuilderSettingEvaluationException("Failed to get field value: " + field, e);
                        }
                    } catch (Exception e2) {
                        throw new _ObjectBuilderSettingEvaluationException("Failed to get field " + StringUtil.jQuote(fieldName) + " from class " + StringUtil.jQuote(className) + ".", e2);
                    }
                } catch (Exception e3) {
                    throw new _ObjectBuilderSettingEvaluationException("Failed to get field's parent class, " + StringUtil.jQuote(className) + ".", e3);
                }
            }
            throw new IllegalArgumentException();
        }

        private Object callConstructor(Class cl) throws _ObjectBuilderSettingEvaluationException {
            if (!hasNoParameters()) {
                BeansWrapper ow = _ObjectBuilderSettingEvaluator.this.env.getObjectWrapper();
                List tmArgs = new ArrayList(this.positionalParamValues.size());
                for (int i = 0; i < this.positionalParamValues.size(); i++) {
                    try {
                        tmArgs.add(ow.wrap(this.positionalParamValues.get(i)));
                    } catch (TemplateModelException e) {
                        throw new _ObjectBuilderSettingEvaluationException("Failed to wrap arg #" + (i + 1), e);
                    }
                }
                try {
                    return ow.newInstance(cl, tmArgs);
                } catch (Exception e2) {
                    throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " constructor", e2);
                }
            }
            try {
                return cl.newInstance();
            } catch (Exception e3) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " 0-argument constructor", e3);
            }
        }

        private Object callBuild(Object constructorResult) throws NoSuchMethodException, SecurityException, _ObjectBuilderSettingEvaluationException {
            Throwable cause;
            Class cl = constructorResult.getClass();
            try {
                Method buildMethod = constructorResult.getClass().getMethod("build", (Class[]) null);
                try {
                    return buildMethod.invoke(constructorResult, (Object[]) null);
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException) {
                        cause = ((InvocationTargetException) e).getTargetException();
                    } else {
                        cause = e;
                    }
                    throw new _ObjectBuilderSettingEvaluationException("Failed to call build() method on " + cl.getName() + " instance", cause);
                }
            } catch (NoSuchMethodException e2) {
                throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " builder class must have a public build() method", e2);
            } catch (Exception e3) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to get the build() method of the " + cl.getName() + " builder class", e3);
            }
        }

        private boolean hasNoParameters() {
            return this.positionalParamValues.isEmpty() && this.namedParamValues.isEmpty();
        }

        @Override // freemarker.core._ObjectBuilderSettingEvaluator.ExpressionWithParameters
        protected boolean getAllowPositionalParameters() {
            return true;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$PropertyAssignmentsExpression.class */
    private class PropertyAssignmentsExpression extends ExpressionWithParameters {
        private final Object bean;

        public PropertyAssignmentsExpression(Object bean) {
            super();
            this.bean = bean;
        }

        @Override // freemarker.core._ObjectBuilderSettingEvaluator.SettingExpression
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(this.bean, this.namedParamNames, this.namedParamValues);
            return this.bean;
        }

        @Override // freemarker.core._ObjectBuilderSettingEvaluator.ExpressionWithParameters
        protected boolean getAllowPositionalParameters() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ObjectBuilderSettingEvaluator$LegacyExceptionWrapperSettingEvaluationExpression.class */
    private static class LegacyExceptionWrapperSettingEvaluationExpression extends _ObjectBuilderSettingEvaluationException {
        public LegacyExceptionWrapperSettingEvaluationExpression(Throwable cause) {
            super("Legacy operation failed", cause);
            if (!(cause instanceof ClassNotFoundException) && !(cause instanceof InstantiationException) && !(cause instanceof IllegalAccessException)) {
                throw new IllegalArgumentException();
            }
        }

        public void rethrowLegacy() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
            Throwable cause = getCause();
            if (cause instanceof ClassNotFoundException) {
                throw ((ClassNotFoundException) cause);
            }
            if (cause instanceof InstantiationException) {
                throw ((InstantiationException) cause);
            }
            if (!(cause instanceof IllegalAccessException)) {
                throw new BugException();
            }
            throw ((IllegalAccessException) cause);
        }
    }
}
