package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.pattern.parser.Parser;
import freemarker.core.BuiltInsForCallables;
import freemarker.core.BuiltInsForDates;
import freemarker.core.BuiltInsForExistenceHandling;
import freemarker.core.BuiltInsForHashes;
import freemarker.core.BuiltInsForLoopVariables;
import freemarker.core.BuiltInsForMarkupOutputs;
import freemarker.core.BuiltInsForMultipleTypes;
import freemarker.core.BuiltInsForNodes;
import freemarker.core.BuiltInsForNumbers;
import freemarker.core.BuiltInsForOutputFormatRelated;
import freemarker.core.BuiltInsForSequences;
import freemarker.core.BuiltInsForStringsBasic;
import freemarker.core.BuiltInsForStringsEncoding;
import freemarker.core.BuiltInsForStringsMisc;
import freemarker.core.BuiltInsForStringsRegexp;
import freemarker.core.BuiltInsWithLazyConditionals;
import freemarker.core.Expression;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltIn.class */
abstract class BuiltIn extends Expression implements Cloneable {
    protected Expression target;
    protected String key;
    static final int NUMBER_OF_BIS = 296;
    static final String BI_NAME_SNAKE_CASE_WITH_ARGS = "with_args";
    static final String BI_NAME_CAMEL_CASE_WITH_ARGS = "withArgs";
    static final String BI_NAME_SNAKE_CASE_WITH_ARGS_LAST = "with_args_last";
    static final String BI_NAME_CAMEL_CASE_WITH_ARGS_LAST = "withArgsLast";
    static final Set<String> CAMEL_CASE_NAMES = new TreeSet();
    static final Set<String> SNAKE_CASE_NAMES = new TreeSet();
    static final HashMap<String, BuiltIn> BUILT_INS_BY_NAME = new HashMap<>(445, 1.0f);

    BuiltIn() {
    }

    static {
        putBI("abs", new BuiltInsForNumbers.absBI());
        putBI("absolute_template_name", "absoluteTemplateName", new BuiltInsForStringsMisc.absolute_template_nameBI());
        putBI("ancestors", new BuiltInsForNodes.ancestorsBI());
        putBI("api", new BuiltInsForMultipleTypes.apiBI());
        putBI("boolean", new BuiltInsForStringsMisc.booleanBI());
        putBI("byte", new BuiltInsForNumbers.byteBI());
        putBI("c", new BuiltInsForMultipleTypes.cBI());
        putBI("cn", new BuiltInsForMultipleTypes.cnBI());
        putBI("cap_first", "capFirst", new BuiltInsForStringsBasic.cap_firstBI());
        putBI("capitalize", new BuiltInsForStringsBasic.capitalizeBI());
        putBI("ceiling", new BuiltInsForNumbers.ceilingBI());
        putBI("children", new BuiltInsForNodes.childrenBI());
        putBI("chop_linebreak", "chopLinebreak", new BuiltInsForStringsBasic.chop_linebreakBI());
        putBI("contains", new BuiltInsForStringsBasic.containsBI());
        putBI("date", new BuiltInsForMultipleTypes.dateBI(2));
        putBI("date_if_unknown", "dateIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(2));
        putBI("datetime", new BuiltInsForMultipleTypes.dateBI(3));
        putBI("datetime_if_unknown", "datetimeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(3));
        putBI("default", new BuiltInsForExistenceHandling.defaultBI());
        putBI("double", new BuiltInsForNumbers.doubleBI());
        putBI("drop_while", "dropWhile", new BuiltInsForSequences.drop_whileBI());
        putBI("ends_with", "endsWith", new BuiltInsForStringsBasic.ends_withBI());
        putBI("ensure_ends_with", "ensureEndsWith", new BuiltInsForStringsBasic.ensure_ends_withBI());
        putBI("ensure_starts_with", "ensureStartsWith", new BuiltInsForStringsBasic.ensure_starts_withBI());
        putBI("esc", new BuiltInsForOutputFormatRelated.escBI());
        putBI("eval", new BuiltInsForStringsMisc.evalBI());
        putBI("eval_json", "evalJson", new BuiltInsForStringsMisc.evalJsonBI());
        putBI("exists", new BuiltInsForExistenceHandling.existsBI());
        putBI("filter", new BuiltInsForSequences.filterBI());
        putBI("first", new BuiltInsForSequences.firstBI());
        putBI("float", new BuiltInsForNumbers.floatBI());
        putBI("floor", new BuiltInsForNumbers.floorBI());
        putBI("chunk", new BuiltInsForSequences.chunkBI());
        putBI("counter", new BuiltInsForLoopVariables.counterBI());
        putBI("item_cycle", "itemCycle", new BuiltInsForLoopVariables.item_cycleBI());
        putBI("has_api", "hasApi", new BuiltInsForMultipleTypes.has_apiBI());
        putBI("has_content", "hasContent", new BuiltInsForExistenceHandling.has_contentBI());
        putBI("has_next", "hasNext", new BuiltInsForLoopVariables.has_nextBI());
        putBI("html", new BuiltInsForStringsEncoding.htmlBI());
        putBI("if_exists", "ifExists", new BuiltInsForExistenceHandling.if_existsBI());
        putBI(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, new BuiltInsForLoopVariables.indexBI());
        putBI("index_of", "indexOf", new BuiltInsForStringsBasic.index_ofBI(false));
        putBI("int", new BuiltInsForNumbers.intBI());
        putBI("interpret", new Interpret());
        putBI("is_boolean", "isBoolean", new BuiltInsForMultipleTypes.is_booleanBI());
        putBI("is_collection", "isCollection", new BuiltInsForMultipleTypes.is_collectionBI());
        putBI("is_collection_ex", "isCollectionEx", new BuiltInsForMultipleTypes.is_collection_exBI());
        BuiltInsForMultipleTypes.is_dateLikeBI bi = new BuiltInsForMultipleTypes.is_dateLikeBI();
        putBI("is_date", "isDate", bi);
        putBI("is_date_like", "isDateLike", bi);
        putBI("is_date_only", "isDateOnly", new BuiltInsForMultipleTypes.is_dateOfTypeBI(2));
        putBI("is_even_item", "isEvenItem", new BuiltInsForLoopVariables.is_even_itemBI());
        putBI("is_first", "isFirst", new BuiltInsForLoopVariables.is_firstBI());
        putBI("is_last", "isLast", new BuiltInsForLoopVariables.is_lastBI());
        putBI("is_unknown_date_like", "isUnknownDateLike", new BuiltInsForMultipleTypes.is_dateOfTypeBI(0));
        putBI("is_datetime", "isDatetime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(3));
        putBI("is_directive", "isDirective", new BuiltInsForMultipleTypes.is_directiveBI());
        putBI("is_enumerable", "isEnumerable", new BuiltInsForMultipleTypes.is_enumerableBI());
        putBI("is_hash_ex", "isHashEx", new BuiltInsForMultipleTypes.is_hash_exBI());
        putBI("is_hash", "isHash", new BuiltInsForMultipleTypes.is_hashBI());
        putBI("is_infinite", "isInfinite", new BuiltInsForNumbers.is_infiniteBI());
        putBI("is_indexable", "isIndexable", new BuiltInsForMultipleTypes.is_indexableBI());
        putBI("is_macro", "isMacro", new BuiltInsForMultipleTypes.is_macroBI());
        putBI("is_markup_output", "isMarkupOutput", new BuiltInsForMultipleTypes.is_markup_outputBI());
        putBI("is_method", "isMethod", new BuiltInsForMultipleTypes.is_methodBI());
        putBI("is_nan", "isNan", new BuiltInsForNumbers.is_nanBI());
        putBI("is_node", "isNode", new BuiltInsForMultipleTypes.is_nodeBI());
        putBI("is_number", "isNumber", new BuiltInsForMultipleTypes.is_numberBI());
        putBI("is_odd_item", "isOddItem", new BuiltInsForLoopVariables.is_odd_itemBI());
        putBI("is_sequence", "isSequence", new BuiltInsForMultipleTypes.is_sequenceBI());
        putBI("is_string", "isString", new BuiltInsForMultipleTypes.is_stringBI());
        putBI("is_time", "isTime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(1));
        putBI("is_transform", "isTransform", new BuiltInsForMultipleTypes.is_transformBI());
        putBI("iso_utc", "isoUtc", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, true));
        putBI("iso_utc_fz", "isoUtcFZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.TRUE, 6, true));
        putBI("iso_utc_nz", "isoUtcNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, true));
        putBI("iso_utc_ms", "isoUtcMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, true));
        putBI("iso_utc_ms_nz", "isoUtcMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, true));
        putBI("iso_utc_m", "isoUtcM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, true));
        putBI("iso_utc_m_nz", "isoUtcMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, true));
        putBI("iso_utc_h", "isoUtcH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, true));
        putBI("iso_utc_h_nz", "isoUtcHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, true));
        putBI("iso_local", "isoLocal", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, false));
        putBI("iso_local_nz", "isoLocalNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, false));
        putBI("iso_local_ms", "isoLocalMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, false));
        putBI("iso_local_ms_nz", "isoLocalMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, false));
        putBI("iso_local_m", "isoLocalM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, false));
        putBI("iso_local_m_nz", "isoLocalMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, false));
        putBI("iso_local_h", "isoLocalH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, false));
        putBI("iso_local_h_nz", "isoLocalHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, false));
        putBI("iso", new BuiltInsForDates.iso_BI(null, 6));
        putBI("iso_nz", "isoNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 6));
        putBI("iso_ms", "isoMs", new BuiltInsForDates.iso_BI(null, 7));
        putBI("iso_ms_nz", "isoMsNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 7));
        putBI("iso_m", "isoM", new BuiltInsForDates.iso_BI(null, 5));
        putBI("iso_m_nz", "isoMNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 5));
        putBI("iso_h", "isoH", new BuiltInsForDates.iso_BI(null, 4));
        putBI("iso_h_nz", "isoHNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 4));
        putBI("j_string", "jString", new BuiltInsForStringsEncoding.j_stringBI());
        putBI("join", new BuiltInsForSequences.joinBI());
        putBI("js_string", "jsString", new BuiltInsForStringsEncoding.js_stringBI());
        putBI("json_string", "jsonString", new BuiltInsForStringsEncoding.json_stringBI());
        putBI("keep_after", "keepAfter", new BuiltInsForStringsBasic.keep_afterBI());
        putBI("keep_before", "keepBefore", new BuiltInsForStringsBasic.keep_beforeBI());
        putBI("keep_after_last", "keepAfterLast", new BuiltInsForStringsBasic.keep_after_lastBI());
        putBI("keep_before_last", "keepBeforeLast", new BuiltInsForStringsBasic.keep_before_lastBI());
        putBI("keys", new BuiltInsForHashes.keysBI());
        putBI("last_index_of", "lastIndexOf", new BuiltInsForStringsBasic.index_ofBI(true));
        putBI("last", new BuiltInsForSequences.lastBI());
        putBI("left_pad", "leftPad", new BuiltInsForStringsBasic.padBI(true));
        putBI("length", new BuiltInsForStringsBasic.lengthBI());
        putBI("long", new BuiltInsForNumbers.longBI());
        putBI("lower_abc", "lowerAbc", new BuiltInsForNumbers.lower_abcBI());
        putBI("lower_case", "lowerCase", new BuiltInsForStringsBasic.lower_caseBI());
        putBI("c_lower_case", "cLowerCase", new BuiltInsForStringsBasic.c_lower_caseBI());
        putBI(BeanDefinitionParserDelegate.MAP_ELEMENT, new BuiltInsForSequences.mapBI());
        putBI("namespace", new BuiltInsForMultipleTypes.namespaceBI());
        putBI("new", new NewBI());
        putBI("markup_string", "markupString", new BuiltInsForMarkupOutputs.markup_stringBI());
        putBI("node_name", "nodeName", new BuiltInsForNodes.node_nameBI());
        putBI("node_namespace", "nodeNamespace", new BuiltInsForNodes.node_namespaceBI());
        putBI("node_type", "nodeType", new BuiltInsForNodes.node_typeBI());
        putBI("no_esc", "noEsc", new BuiltInsForOutputFormatRelated.no_escBI());
        putBI("max", new BuiltInsForSequences.maxBI());
        putBI("min", new BuiltInsForSequences.minBI());
        putBI("number", new BuiltInsForStringsMisc.numberBI());
        putBI("number_to_date", "numberToDate", new BuiltInsForNumbers.number_to_dateBI(2));
        putBI("number_to_time", "numberToTime", new BuiltInsForNumbers.number_to_dateBI(1));
        putBI("number_to_datetime", "numberToDatetime", new BuiltInsForNumbers.number_to_dateBI(3));
        putBI("parent", new BuiltInsForNodes.parentBI());
        putBI("previous_sibling", "previousSibling", new BuiltInsForNodes.previousSiblingBI());
        putBI("next_sibling", "nextSibling", new BuiltInsForNodes.nextSiblingBI());
        putBI("item_parity", "itemParity", new BuiltInsForLoopVariables.item_parityBI());
        putBI("item_parity_cap", "itemParityCap", new BuiltInsForLoopVariables.item_parity_capBI());
        putBI("reverse", new BuiltInsForSequences.reverseBI());
        putBI("right_pad", "rightPad", new BuiltInsForStringsBasic.padBI(false));
        putBI("root", new BuiltInsForNodes.rootBI());
        putBI("round", new BuiltInsForNumbers.roundBI());
        putBI("remove_ending", "removeEnding", new BuiltInsForStringsBasic.remove_endingBI());
        putBI("remove_beginning", "removeBeginning", new BuiltInsForStringsBasic.remove_beginningBI());
        putBI("rtf", new BuiltInsForStringsEncoding.rtfBI());
        putBI("seq_contains", "seqContains", new BuiltInsForSequences.seq_containsBI());
        putBI("seq_index_of", "seqIndexOf", new BuiltInsForSequences.seq_index_ofBI(true));
        putBI("seq_last_index_of", "seqLastIndexOf", new BuiltInsForSequences.seq_index_ofBI(false));
        putBI("sequence", new BuiltInsForSequences.sequenceBI());
        putBI("short", new BuiltInsForNumbers.shortBI());
        putBI(InputTag.SIZE_ATTRIBUTE, new BuiltInsForMultipleTypes.sizeBI());
        putBI("sort_by", "sortBy", new BuiltInsForSequences.sort_byBI());
        putBI("sort", new BuiltInsForSequences.sortBI());
        putBI("split", new BuiltInsForStringsBasic.split_BI());
        putBI("switch", new BuiltInsWithLazyConditionals.switch_BI());
        putBI("starts_with", "startsWith", new BuiltInsForStringsBasic.starts_withBI());
        putBI("string", new BuiltInsForMultipleTypes.stringBI());
        putBI("substring", new BuiltInsForStringsBasic.substringBI());
        putBI("take_while", "takeWhile", new BuiltInsForSequences.take_whileBI());
        putBI("then", new BuiltInsWithLazyConditionals.then_BI());
        putBI("time", new BuiltInsForMultipleTypes.dateBI(1));
        putBI("time_if_unknown", "timeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(1));
        putBI("trim", new BuiltInsForStringsBasic.trimBI());
        putBI("truncate", new BuiltInsForStringsBasic.truncateBI());
        putBI("truncate_w", "truncateW", new BuiltInsForStringsBasic.truncate_wBI());
        putBI("truncate_c", "truncateC", new BuiltInsForStringsBasic.truncate_cBI());
        putBI("truncate_m", "truncateM", new BuiltInsForStringsBasic.truncate_mBI());
        putBI("truncate_w_m", "truncateWM", new BuiltInsForStringsBasic.truncate_w_mBI());
        putBI("truncate_c_m", "truncateCM", new BuiltInsForStringsBasic.truncate_c_mBI());
        putBI("uncap_first", "uncapFirst", new BuiltInsForStringsBasic.uncap_firstBI());
        putBI("upper_abc", "upperAbc", new BuiltInsForNumbers.upper_abcBI());
        putBI("upper_case", "upperCase", new BuiltInsForStringsBasic.upper_caseBI());
        putBI("c_upper_case", "cUpperCase", new BuiltInsForStringsBasic.c_upper_caseBI());
        putBI("url", new BuiltInsForStringsEncoding.urlBI());
        putBI("url_path", "urlPath", new BuiltInsForStringsEncoding.urlPathBI());
        putBI("values", new BuiltInsForHashes.valuesBI());
        putBI("web_safe", "webSafe", BUILT_INS_BY_NAME.get("html"));
        putBI(BI_NAME_SNAKE_CASE_WITH_ARGS, BI_NAME_CAMEL_CASE_WITH_ARGS, new BuiltInsForCallables.with_argsBI());
        putBI(BI_NAME_SNAKE_CASE_WITH_ARGS_LAST, BI_NAME_CAMEL_CASE_WITH_ARGS_LAST, new BuiltInsForCallables.with_args_lastBI());
        putBI("word_list", "wordList", new BuiltInsForStringsBasic.word_listBI());
        putBI("xhtml", new BuiltInsForStringsEncoding.xhtmlBI());
        putBI("xml", new BuiltInsForStringsEncoding.xmlBI());
        putBI("matches", new BuiltInsForStringsRegexp.matchesBI());
        putBI("groups", new BuiltInsForStringsRegexp.groupsBI());
        putBI(Parser.REPLACE_CONVERTER_WORD, new BuiltInsForStringsRegexp.replace_reBI());
        if (NUMBER_OF_BIS < BUILT_INS_BY_NAME.size()) {
            throw new AssertionError("Update NUMBER_OF_BIS! Should be: " + BUILT_INS_BY_NAME.size());
        }
    }

    private static void putBI(String name, BuiltIn bi) {
        BUILT_INS_BY_NAME.put(name, bi);
        SNAKE_CASE_NAMES.add(name);
        CAMEL_CASE_NAMES.add(name);
    }

    private static void putBI(String nameSnakeCase, String nameCamelCase, BuiltIn bi) {
        BUILT_INS_BY_NAME.put(nameSnakeCase, bi);
        BUILT_INS_BY_NAME.put(nameCamelCase, bi);
        SNAKE_CASE_NAMES.add(nameSnakeCase);
        CAMEL_CASE_NAMES.add(nameCamelCase);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x00c9  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00e6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static freemarker.core.BuiltIn newBuiltIn(int r6, freemarker.core.Expression r7, freemarker.core.Token r8, freemarker.core.FMParserTokenManager r9) throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 347
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.BuiltIn.newBuiltIn(int, freemarker.core.Expression, freemarker.core.Token, freemarker.core.FMParserTokenManager):freemarker.core.BuiltIn");
    }

    protected void setTarget(Expression target) {
        this.target = target;
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.target.getCanonicalForm() + CallerData.NA + this.key;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return CallerData.NA + this.key;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
    }

    protected final void checkMethodArgCount(List args, int expectedCnt) throws TemplateModelException {
        checkMethodArgCount(args.size(), expectedCnt);
    }

    protected final void checkMethodArgCount(int argCnt, int expectedCnt) throws TemplateModelException {
        if (argCnt != expectedCnt) {
            throw _MessageUtil.newArgCntError(CallerData.NA + this.key, argCnt, expectedCnt);
        }
    }

    protected final void checkMethodArgCount(List args, int minCnt, int maxCnt) throws TemplateModelException {
        checkMethodArgCount(args.size(), minCnt, maxCnt);
    }

    protected final void checkMethodArgCount(int argCnt, int minCnt, int maxCnt) throws TemplateModelException {
        if (argCnt < minCnt || argCnt > maxCnt) {
            throw _MessageUtil.newArgCntError(CallerData.NA + this.key, argCnt, minCnt, maxCnt);
        }
    }

    protected final String getOptStringMethodArg(List args, int argIdx) throws TemplateModelException {
        if (args.size() > argIdx) {
            return getStringMethodArg(args, argIdx);
        }
        return null;
    }

    protected final String getStringMethodArg(List args, int argIdx) throws TemplateModelException {
        TemplateModel arg = (TemplateModel) args.get(argIdx);
        if (!(arg instanceof TemplateScalarModel)) {
            throw _MessageUtil.newMethodArgMustBeStringException(CallerData.NA + this.key, argIdx, arg);
        }
        return EvalUtil.modelToString((TemplateScalarModel) arg, null, null);
    }

    protected final Number getOptNumberMethodArg(List args, int argIdx) throws TemplateModelException {
        if (args.size() > argIdx) {
            return getNumberMethodArg(args, argIdx);
        }
        return null;
    }

    protected final Number getNumberMethodArg(List args, int argIdx) throws TemplateModelException {
        TemplateModel arg = (TemplateModel) args.get(argIdx);
        if (!(arg instanceof TemplateNumberModel)) {
            throw _MessageUtil.newMethodArgMustBeNumberException(CallerData.NA + this.key, argIdx, arg);
        }
        return EvalUtil.modelToNumber((TemplateNumberModel) arg, null);
    }

    protected final TemplateModelException newMethodArgInvalidValueException(int argIdx, Object[] details) {
        return _MessageUtil.newMethodArgInvalidValueException(CallerData.NA + this.key, argIdx, details);
    }

    protected final TemplateModelException newMethodArgsInvalidValueException(Object[] details) {
        return _MessageUtil.newMethodArgsInvalidValueException(CallerData.NA + this.key, details);
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        try {
            BuiltIn clone = (BuiltIn) clone();
            clone.target = this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Internal error: " + e);
        }
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.target;
            case 1:
                return this.key;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.LEFT_HAND_OPERAND;
            case 1:
                return ParameterRole.RIGHT_HAND_OPERAND;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
