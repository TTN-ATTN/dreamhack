package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._VersionInts;
import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/EvalUtil.class */
class EvalUtil {
    static final int CMP_OP_EQUALS = 1;
    static final int CMP_OP_NOT_EQUALS = 2;
    static final int CMP_OP_LESS_THAN = 3;
    static final int CMP_OP_GREATER_THAN = 4;
    static final int CMP_OP_LESS_THAN_EQUALS = 5;
    static final int CMP_OP_GREATER_THAN_EQUALS = 6;
    private static final String VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE = "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison.";

    private EvalUtil() {
    }

    static String modelToString(TemplateScalarModel model, Expression expr, Environment env) throws TemplateModelException {
        String value = model.getAsString();
        if (value == null) {
            if (env == null) {
                env = Environment.getCurrentEnvironment();
            }
            if (env != null && env.isClassicCompatible()) {
                return "";
            }
            throw newModelHasStoredNullException(String.class, model, expr);
        }
        return value;
    }

    static Number modelToNumber(TemplateNumberModel model, Expression expr) throws TemplateModelException {
        Number value = model.getAsNumber();
        if (value == null) {
            throw newModelHasStoredNullException(Number.class, model, expr);
        }
        return value;
    }

    static Date modelToDate(TemplateDateModel model, Expression expr) throws TemplateModelException {
        Date value = model.getAsDate();
        if (value == null) {
            throw newModelHasStoredNullException(Date.class, model, expr);
        }
        return value;
    }

    static TemplateModelException newModelHasStoredNullException(Class expected, TemplateModel model, Expression expr) {
        return new _TemplateModelException(expr, _TemplateModelException.modelHasStoredNullDescription(expected, model));
    }

    static boolean compare(Expression leftExp, int operator, String operatorString, Expression rightExp, Expression defaultBlamed, Environment env) throws TemplateException {
        TemplateModel ltm = leftExp.eval(env);
        TemplateModel rtm = rightExp.eval(env);
        return compare(ltm, leftExp, operator, operatorString, rtm, rightExp, defaultBlamed, false, false, false, false, env);
    }

    static boolean compare(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
        return compare(leftValue, null, operator, null, rightValue, null, null, false, false, false, false, env);
    }

    static boolean compareLenient(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
        return compare(leftValue, null, operator, null, rightValue, null, null, false, true, false, false, env);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean compare(TemplateModel templateModel, Expression expression, int i, String str, TemplateModel templateModel2, Expression expression2, Expression expression3, boolean z, boolean z2, boolean z3, boolean z4, Environment environment) throws TemplateException {
        int iCompare;
        Object obj;
        Expression expression4;
        ArithmeticEngine arithmeticEngine;
        if (templateModel == null) {
            if (environment != null && environment.isClassicCompatible()) {
                templateModel = TemplateScalarModel.EMPTY_STRING;
            } else {
                if (z3) {
                    return false;
                }
                if (expression != null) {
                    throw InvalidReferenceException.getInstance(expression, environment);
                }
                throw new _MiscTemplateException(expression3, environment, "The left operand of the comparison was undefined or null.");
            }
        }
        if (templateModel2 == null) {
            if (environment != null && environment.isClassicCompatible()) {
                templateModel2 = TemplateScalarModel.EMPTY_STRING;
            } else {
                if (z4) {
                    return false;
                }
                if (expression2 != null) {
                    throw InvalidReferenceException.getInstance(expression2, environment);
                }
                throw new _MiscTemplateException(expression3, environment, "The right operand of the comparison was undefined or null.");
            }
        }
        if ((templateModel instanceof TemplateNumberModel) && (templateModel2 instanceof TemplateNumberModel)) {
            Number numberModelToNumber = modelToNumber((TemplateNumberModel) templateModel, expression);
            Number numberModelToNumber2 = modelToNumber((TemplateNumberModel) templateModel2, expression2);
            if (environment != null) {
                arithmeticEngine = environment.getArithmeticEngine();
            } else {
                arithmeticEngine = expression != null ? expression.getTemplate().getArithmeticEngine() : ArithmeticEngine.BIGDECIMAL_ENGINE;
            }
            try {
                iCompare = arithmeticEngine.compareNumbers(numberModelToNumber, numberModelToNumber2);
            } catch (RuntimeException e) {
                throw new _MiscTemplateException(expression3, e, environment, "Unexpected error while comparing two numbers: ", e);
            }
        } else if ((templateModel instanceof TemplateDateModel) && (templateModel2 instanceof TemplateDateModel)) {
            TemplateDateModel templateDateModel = (TemplateDateModel) templateModel;
            TemplateDateModel templateDateModel2 = (TemplateDateModel) templateModel2;
            int dateType = templateDateModel.getDateType();
            int dateType2 = templateDateModel2.getDateType();
            if (dateType == 0 || dateType2 == 0) {
                if (dateType == 0) {
                    obj = "left";
                    expression4 = expression;
                } else {
                    obj = "right";
                    expression4 = expression2;
                }
                throw new _MiscTemplateException(expression4 != null ? expression4 : expression3, environment, "The ", obj, " ", VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE);
            }
            if (dateType != dateType2) {
                throw new _MiscTemplateException(expression3, environment, "Can't compare dates of different types. Left date type is ", TemplateDateModel.TYPE_NAMES.get(dateType), ", right date type is ", TemplateDateModel.TYPE_NAMES.get(dateType2), ".");
            }
            iCompare = modelToDate(templateDateModel, expression).compareTo(modelToDate(templateDateModel2, expression2));
        } else if ((templateModel instanceof TemplateScalarModel) && (templateModel2 instanceof TemplateScalarModel)) {
            if (i != 1 && i != 2) {
                throw new _MiscTemplateException(expression3, environment, "Can't use operator \"", cmpOpToString(i, str), "\" on string values.");
            }
            String strModelToString = modelToString((TemplateScalarModel) templateModel, expression, environment);
            String strModelToString2 = modelToString((TemplateScalarModel) templateModel2, expression2, environment);
            if (environment.getConfiguration().getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_33) {
                iCompare = environment.getCollator().compare(strModelToString, strModelToString2);
            } else {
                iCompare = Normalizer.normalize(strModelToString, Normalizer.Form.NFKC).compareTo(Normalizer.normalize(strModelToString2, Normalizer.Form.NFKC));
            }
        } else if ((templateModel instanceof TemplateBooleanModel) && (templateModel2 instanceof TemplateBooleanModel)) {
            if (i != 1 && i != 2) {
                throw new _MiscTemplateException(expression3, environment, "Can't use operator \"", cmpOpToString(i, str), "\" on boolean values.");
            }
            iCompare = (((TemplateBooleanModel) templateModel).getAsBoolean() ? 1 : 0) - (((TemplateBooleanModel) templateModel2).getAsBoolean() ? 1 : 0);
        } else if (environment.isClassicCompatible()) {
            iCompare = environment.getCollator().compare(expression.evalAndCoerceToPlainText(environment), expression2.evalAndCoerceToPlainText(environment));
        } else {
            if (z2) {
                if (i == 1) {
                    return false;
                }
                if (i == 2) {
                    return true;
                }
            }
            Object[] objArr = new Object[12];
            objArr[0] = "Can't compare values of these types. ";
            objArr[1] = "Allowed comparisons are between two numbers, two strings, two dates, or two booleans.\n";
            objArr[2] = "Left hand operand ";
            objArr[3] = (!z || expression == null) ? "" : new Object[]{"(", new _DelayedGetCanonicalForm(expression), ") value "};
            objArr[4] = "is ";
            objArr[5] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(templateModel));
            objArr[6] = ".\n";
            objArr[7] = "Right hand operand ";
            objArr[8] = (!z || expression2 == null) ? "" : new Object[]{"(", new _DelayedGetCanonicalForm(expression2), ") value "};
            objArr[9] = "is ";
            objArr[10] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(templateModel2));
            objArr[11] = ".";
            throw new _MiscTemplateException(expression3, environment, objArr);
        }
        switch (i) {
            case 1:
                return iCompare == 0;
            case 2:
                return iCompare != 0;
            case 3:
                return iCompare < 0;
            case 4:
                return iCompare > 0;
            case 5:
                return iCompare <= 0;
            case 6:
                return iCompare >= 0;
            default:
                throw new BugException("Unsupported comparator operator code: " + i);
        }
    }

    private static String cmpOpToString(int operator, String operatorString) {
        if (operatorString != null) {
            return operatorString;
        }
        switch (operator) {
            case 1:
                return "equals";
            case 2:
                return "not-equals";
            case 3:
                return "less-than";
            case 4:
                return "greater-than";
            case 5:
                return "less-than-equals";
            case 6:
                return "greater-than-equals";
            default:
                return "???";
        }
    }

    static int mirrorCmpOperator(int operator) {
        switch (operator) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 6;
            case 6:
                return 5;
            default:
                throw new BugException("Unsupported comparator operator code: " + operator);
        }
    }

    static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        return coerceModelToStringOrMarkup(tm, exp, false, seqTip, env);
    }

    static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, boolean returnNullOnNonCoercableType, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            TemplateNumberModel tnm = (TemplateNumberModel) tm;
            TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
            try {
                return assertFormatResultNotNull(format.format(tnm));
            } catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
            }
        }
        if (tm instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel) tm;
            TemplateDateFormat format2 = env.getTemplateDateFormat(tdm, exp, false);
            try {
                return assertFormatResultNotNull(format2.format(tdm));
            } catch (TemplateValueFormatException e2) {
                throw _MessageUtil.newCantFormatDateException(format2, exp, e2, false);
            }
        }
        if (tm instanceof TemplateMarkupOutputModel) {
            return tm;
        }
        return coerceModelToTextualCommon(tm, exp, seqTip, true, returnNullOnNonCoercableType, env);
    }

    static String coerceModelToStringOrUnsupportedMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            TemplateNumberModel tnm = (TemplateNumberModel) tm;
            TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
            try {
                return ensureFormatResultString(format.format(tnm), exp, env);
            } catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
            }
        }
        if (tm instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel) tm;
            TemplateDateFormat format2 = env.getTemplateDateFormat(tdm, exp, false);
            try {
                return ensureFormatResultString(format2.format(tdm), exp, env);
            } catch (TemplateValueFormatException e2) {
                throw _MessageUtil.newCantFormatDateException(format2, exp, e2, false);
            }
        }
        return coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
    }

    static String coerceModelToPlainText(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            return assertFormatResultNotNull(env.formatNumberToPlainText((TemplateNumberModel) tm, exp, false));
        }
        if (tm instanceof TemplateDateModel) {
            return assertFormatResultNotNull(env.formatDateToPlainText((TemplateDateModel) tm, exp, false));
        }
        return coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
    }

    private static String coerceModelToTextualCommon(TemplateModel tm, Expression exp, String seqHint, boolean supportsTOM, boolean returnNullOnNonCoercableType, Environment env) throws TemplateException {
        if (tm instanceof TemplateScalarModel) {
            return modelToString((TemplateScalarModel) tm, exp, env);
        }
        if (tm == null) {
            if (env.isClassicCompatible()) {
                return "";
            }
            if (exp != null) {
                throw InvalidReferenceException.getInstance(exp, env);
            }
            throw new InvalidReferenceException("Null/missing value (no more information available)", env);
        }
        if (tm instanceof TemplateBooleanModel) {
            boolean booleanValue = ((TemplateBooleanModel) tm).getAsBoolean();
            int compatMode = env.getClassicCompatibleAsInt();
            if (compatMode == 0) {
                return env.formatBoolean(booleanValue, false);
            }
            if (compatMode == 1) {
                return booleanValue ? "true" : "";
            }
            if (compatMode == 2) {
                if (tm instanceof BeanModel) {
                    return _BeansAPI.getAsClassicCompatibleString((BeanModel) tm);
                }
                return booleanValue ? "true" : "";
            }
            throw new BugException("Unsupported classic_compatible variation: " + compatMode);
        }
        if (env.isClassicCompatible() && (tm instanceof BeanModel)) {
            return _BeansAPI.getAsClassicCompatibleString((BeanModel) tm);
        }
        if (returnNullOnNonCoercableType) {
            return null;
        }
        if (seqHint != null && ((tm instanceof TemplateSequenceModel) || (tm instanceof TemplateCollectionModel))) {
            if (supportsTOM) {
                throw new NonStringOrTemplateOutputException(exp, tm, seqHint, env);
            }
            throw new NonStringException(exp, tm, seqHint, env);
        }
        if (supportsTOM) {
            throw new NonStringOrTemplateOutputException(exp, tm, env);
        }
        throw new NonStringException(exp, tm, env);
    }

    private static String ensureFormatResultString(Object formatResult, Expression exp, Environment env) throws NonStringException {
        if (formatResult instanceof String) {
            return (String) formatResult;
        }
        assertFormatResultNotNull(formatResult);
        TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel) formatResult;
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Value was formatted to convert it to string, but the result was markup of ouput format ", new _DelayedJQuote(mo.getOutputFormat()), ".").tip("Use value?string to force formatting to plain text.").blame(exp);
        throw new NonStringException((Environment) null, desc);
    }

    static String assertFormatResultNotNull(String r) {
        if (r != null) {
            return r;
        }
        throw new NullPointerException("TemplateValueFormatter result can't be null");
    }

    static Object assertFormatResultNotNull(Object r) {
        if (r != null) {
            return r;
        }
        throw new NullPointerException("TemplateValueFormatter result can't be null");
    }

    static TemplateMarkupOutputModel concatMarkupOutputs(TemplateObject parent, TemplateMarkupOutputModel leftMO, TemplateMarkupOutputModel rightMO) throws TemplateException {
        MarkupOutputFormat leftOF = leftMO.getOutputFormat();
        MarkupOutputFormat rightOF = rightMO.getOutputFormat();
        if (rightOF != leftOF) {
            String rightPT = rightOF.getSourcePlainText(rightMO);
            if (rightPT != null) {
                return leftOF.concat(leftMO, leftOF.fromPlainTextByEscaping(rightPT));
            }
            String leftPT = leftOF.getSourcePlainText(leftMO);
            if (leftPT != null) {
                return rightOF.concat(rightOF.fromPlainTextByEscaping(leftPT), rightMO);
            }
            Object[] message = {"Concatenation left hand operand is in ", new _DelayedToString(leftOF), " format, while the right hand operand is in ", new _DelayedToString(rightOF), ". Conversion to common format wasn't possible."};
            if (parent instanceof Expression) {
                throw new _MiscTemplateException((Expression) parent, message);
            }
            throw new _MiscTemplateException(message);
        }
        return leftOF.concat(leftMO, rightMO);
    }

    static ArithmeticEngine getArithmeticEngine(Environment env, TemplateObject tObj) {
        if (env != null) {
            return env.getArithmeticEngine();
        }
        return tObj.getTemplate().getParserConfiguration().getArithmeticEngine();
    }

    static boolean shouldWrapUncheckedException(Throwable e, Environment env) {
        if (FlowControlException.class.isInstance(e)) {
            return false;
        }
        if (env.getWrapUncheckedExceptions()) {
            return true;
        }
        if (env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_27) {
            Class<?> cls = e.getClass();
            return cls == NullPointerException.class || cls == ClassCastException.class || cls == IndexOutOfBoundsException.class || cls == InvocationTargetException.class;
        }
        return false;
    }
}
