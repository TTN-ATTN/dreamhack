package freemarker.core;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers.class */
class BuiltInsForNumbers {
    private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal(CustomBooleanEditor.VALUE_1);
    private static final BigDecimal BIG_DECIMAL_LONG_MIN = BigDecimal.valueOf(Long.MIN_VALUE);
    private static final BigDecimal BIG_DECIMAL_LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);
    private static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger BIG_INTEGER_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$abcBI.class */
    private static abstract class abcBI extends BuiltInForNumber {
        protected abstract String toABC(int i);

        private abcBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            try {
                int n = NumberUtil.toIntExact(num);
                if (n <= 0) {
                    throw new _TemplateModelException(this.target, "The left side operand of to ?", this.key, " must be at least 1, but was ", Integer.valueOf(n), ".");
                }
                return new SimpleScalar(toABC(n));
            } catch (ArithmeticException e) {
                throw new _TemplateModelException(this.target, "The left side operand value isn't compatible with ?", this.key, ": ", e.getMessage());
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$lower_abcBI.class */
    static class lower_abcBI extends abcBI {
        lower_abcBI() {
            super();
        }

        @Override // freemarker.core.BuiltInsForNumbers.abcBI
        protected String toABC(int n) {
            return StringUtil.toLowerABC(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$upper_abcBI.class */
    static class upper_abcBI extends abcBI {
        upper_abcBI() {
            super();
        }

        @Override // freemarker.core.BuiltInsForNumbers.abcBI
        protected String toABC(int n) {
            return StringUtil.toUpperABC(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$absBI.class */
    static class absBI extends BuiltInForNumber {
        absBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            if (num instanceof Integer) {
                int n = num.intValue();
                if (n < 0) {
                    return new SimpleNumber(-n);
                }
                return model;
            }
            if (num instanceof BigDecimal) {
                BigDecimal n2 = (BigDecimal) num;
                if (n2.signum() < 0) {
                    return new SimpleNumber(n2.negate());
                }
                return model;
            }
            if (num instanceof Double) {
                double n3 = num.doubleValue();
                if (n3 < 0.0d) {
                    return new SimpleNumber(-n3);
                }
                return model;
            }
            if (num instanceof Float) {
                float n4 = num.floatValue();
                if (n4 < 0.0f) {
                    return new SimpleNumber(-n4);
                }
                return model;
            }
            if (num instanceof Long) {
                long n5 = num.longValue();
                if (n5 < 0) {
                    return new SimpleNumber(-n5);
                }
                return model;
            }
            if (num instanceof Short) {
                short n6 = num.shortValue();
                if (n6 < 0) {
                    return new SimpleNumber(-n6);
                }
                return model;
            }
            if (num instanceof Byte) {
                byte n7 = num.byteValue();
                if (n7 < 0) {
                    return new SimpleNumber(-n7);
                }
                return model;
            }
            if (num instanceof BigInteger) {
                BigInteger n8 = (BigInteger) num;
                if (n8.signum() < 0) {
                    return new SimpleNumber(n8.negate());
                }
                return model;
            }
            throw new _TemplateModelException("Unsupported number class: ", num.getClass());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$byteBI.class */
    static class byteBI extends BuiltInForNumber {
        byteBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Byte) {
                return model;
            }
            return new SimpleNumber(Byte.valueOf(num.byteValue()));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$ceilingBI.class */
    static class ceilingBI extends BuiltInForNumber {
        ceilingBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 2));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$doubleBI.class */
    static class doubleBI extends BuiltInForNumber {
        doubleBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Double) {
                return model;
            }
            return new SimpleNumber(num.doubleValue());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$floatBI.class */
    static class floatBI extends BuiltInForNumber {
        floatBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Float) {
                return model;
            }
            return new SimpleNumber(num.floatValue());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$floorBI.class */
    static class floorBI extends BuiltInForNumber {
        floorBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$intBI.class */
    static class intBI extends BuiltInForNumber {
        intBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Integer) {
                return model;
            }
            return new SimpleNumber(num.intValue());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$is_infiniteBI.class */
    static class is_infiniteBI extends BuiltInForNumber {
        is_infiniteBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return NumberUtil.isInfinite(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$is_nanBI.class */
    static class is_nanBI extends BuiltInForNumber {
        is_nanBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return NumberUtil.isNaN(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$longBI.class */
    static class longBI extends BuiltIn {
        longBI() {
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (!(model instanceof TemplateNumberModel) && (model instanceof TemplateDateModel)) {
                Date date = EvalUtil.modelToDate((TemplateDateModel) model, this.target);
                return new SimpleNumber(date.getTime());
            }
            Number num = this.target.modelToNumber(model, env);
            if (num instanceof Long) {
                return model;
            }
            return new SimpleNumber(num.longValue());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$number_to_dateBI.class */
    static class number_to_dateBI extends BuiltInForNumber {
        private final int dateType;

        number_to_dateBI(int dateType) {
            this.dateType = dateType;
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return new SimpleDate(new Date(BuiltInsForNumbers.safeToLong(num)), this.dateType);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$roundBI.class */
    static class roundBI extends BuiltInForNumber {
        private static final BigDecimal half = new BigDecimal("0.5");

        roundBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).add(half).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForNumbers$shortBI.class */
    static class shortBI extends BuiltInForNumber {
        shortBI() {
        }

        @Override // freemarker.core.BuiltInForNumber
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Short) {
                return model;
            }
            return new SimpleNumber(Short.valueOf(num.shortValue()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long safeToLong(Number num) throws TemplateModelException {
        if (num instanceof Double) {
            double d = Math.round(num.doubleValue());
            if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", Double.valueOf(d));
            }
            return (long) d;
        }
        if (num instanceof Float) {
            float f = Math.round(num.floatValue());
            if (f > 9.223372E18f || f < -9.223372E18f) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", Float.valueOf(f));
            }
            return (long) f;
        }
        if (num instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal) num).setScale(0, 4);
            if (bd.compareTo(BIG_DECIMAL_LONG_MAX) > 0 || bd.compareTo(BIG_DECIMAL_LONG_MIN) < 0) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", bd);
            }
            return bd.longValue();
        }
        if (num instanceof BigInteger) {
            BigInteger bi = (BigInteger) num;
            if (bi.compareTo(BIG_INTEGER_LONG_MAX) > 0 || bi.compareTo(BIG_INTEGER_LONG_MIN) < 0) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", bi);
            }
            return bi.longValue();
        }
        if ((num instanceof Long) || (num instanceof Integer) || (num instanceof Byte) || (num instanceof Short)) {
            return num.longValue();
        }
        throw new _TemplateModelException("Unsupported number type: ", num.getClass());
    }

    private BuiltInsForNumbers() {
    }
}
