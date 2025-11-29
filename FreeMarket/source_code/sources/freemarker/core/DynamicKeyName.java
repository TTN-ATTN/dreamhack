package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.PropertyAccessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DynamicKeyName.class */
class DynamicKeyName extends Expression {
    private static final int UNKNOWN_RESULT_SIZE = -1;
    private final Expression keyExpression;
    private final Expression target;
    private boolean lazilyGeneratedResultEnabled;
    private static Class[] NUMERICAL_KEY_LHO_EXPECTED_TYPES = new Class[1 + NonStringException.STRING_COERCABLE_TYPES.length];

    DynamicKeyName(Expression target, Expression keyExpression) {
        this.target = target;
        this.keyExpression = keyExpression;
        target.enableLazilyGeneratedResult();
    }

    DynamicKeyName(DynamicKeyName dynamicKeyName) {
        this(dynamicKeyName.target, dynamicKeyName.keyExpression);
        this.lazilyGeneratedResultEnabled = dynamicKeyName.lazilyGeneratedResultEnabled;
        this.constantValue = dynamicKeyName.constantValue;
        copyFieldsFrom(dynamicKeyName);
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel targetModel = this.target.eval(env);
        if (targetModel == null) {
            if (env.isClassicCompatible()) {
                return null;
            }
            throw InvalidReferenceException.getInstance(this.target, env);
        }
        TemplateModel keyModel = this.keyExpression.eval(env);
        if (keyModel == null) {
            if (env.isClassicCompatible()) {
                keyModel = TemplateScalarModel.EMPTY_STRING;
            } else {
                this.keyExpression.assertNonNull(null, env);
            }
        }
        if (keyModel instanceof TemplateNumberModel) {
            int index = this.keyExpression.modelToNumber(keyModel, env).intValue();
            return dealWithNumericalKey(targetModel, index, env);
        }
        if (keyModel instanceof TemplateScalarModel) {
            String key = EvalUtil.modelToString((TemplateScalarModel) keyModel, this.keyExpression, env);
            return dealWithStringKey(targetModel, key, env);
        }
        if (keyModel instanceof RangeModel) {
            return dealWithRangeKey(targetModel, (RangeModel) keyModel, env);
        }
        throw new UnexpectedTypeException(this.keyExpression, keyModel, "number, range, or string", new Class[]{TemplateNumberModel.class, TemplateScalarModel.class, Range.class}, env);
    }

    static {
        NUMERICAL_KEY_LHO_EXPECTED_TYPES[0] = TemplateSequenceModel.class;
        for (int i = 0; i < NonStringException.STRING_COERCABLE_TYPES.length; i++) {
            NUMERICAL_KEY_LHO_EXPECTED_TYPES[i + 1] = NonStringException.STRING_COERCABLE_TYPES[i];
        }
    }

    private TemplateModel dealWithNumericalKey(TemplateModel targetModel, int index, Environment env) throws TemplateException {
        int size;
        if (targetModel instanceof TemplateSequenceModel) {
            TemplateSequenceModel tsm = (TemplateSequenceModel) targetModel;
            try {
                size = tsm.size();
            } catch (Exception e) {
                size = Integer.MAX_VALUE;
            }
            if (index < size) {
                return tsm.get(index);
            }
            return null;
        }
        if ((targetModel instanceof LazilyGeneratedCollectionModel) && ((LazilyGeneratedCollectionModel) targetModel).isSequence()) {
            if (index < 0) {
                return null;
            }
            TemplateModelIterator iter = ((LazilyGeneratedCollectionModel) targetModel).iterator();
            int curIndex = 0;
            while (iter.hasNext()) {
                TemplateModel next = iter.next();
                if (index != curIndex) {
                    curIndex++;
                } else {
                    return next;
                }
            }
            return null;
        }
        try {
            String s = this.target.evalAndCoerceToPlainText(env);
            try {
                return new SimpleScalar(s.substring(index, index + 1));
            } catch (IndexOutOfBoundsException e2) {
                if (index < 0) {
                    throw new _MiscTemplateException("Negative index not allowed: ", Integer.valueOf(index));
                }
                if (index >= s.length()) {
                    throw new _MiscTemplateException("String index out of range: The index was ", Integer.valueOf(index), " (0-based), but the length of the string is only ", Integer.valueOf(s.length()), ".");
                }
                throw new RuntimeException("Can't explain exception", e2);
            }
        } catch (NonStringException e3) {
            throw new UnexpectedTypeException(this.target, targetModel, "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, targetModel instanceof TemplateHashModel ? "You had a numerical value inside the []. Currently that's only supported for sequences (lists) and strings. To get a Map item with a non-string key, use myMap?api.get(myKey)." : null, env);
        }
    }

    private TemplateModel dealWithStringKey(TemplateModel targetModel, String key, Environment env) throws TemplateException {
        if (targetModel instanceof TemplateHashModel) {
            return getFromHashModelWithStringKey((TemplateHashModel) targetModel, key);
        }
        throw new NonHashException(this.target, targetModel, env);
    }

    protected TemplateModel getFromHashModelWithStringKey(TemplateHashModel targetModel, String key) throws TemplateException {
        return targetModel.get(key);
    }

    private TemplateModel dealWithRangeKey(TemplateModel targetModel, RangeModel range, Environment env) throws TemplateException {
        TemplateSequenceModel targetSeq;
        LazilyGeneratedCollectionModel targetLazySeq;
        String targetStr;
        int targetSize;
        boolean targetSizeKnown;
        int resultSize;
        int exclEndIdx;
        if (targetModel instanceof TemplateSequenceModel) {
            targetSeq = (TemplateSequenceModel) targetModel;
            targetLazySeq = null;
            targetStr = null;
        } else if ((targetModel instanceof LazilyGeneratedCollectionModel) && ((LazilyGeneratedCollectionModel) targetModel).isSequence()) {
            targetSeq = null;
            targetLazySeq = (LazilyGeneratedCollectionModel) targetModel;
            targetStr = null;
        } else {
            targetSeq = null;
            targetLazySeq = null;
            try {
                targetStr = this.target.evalAndCoerceToPlainText(env);
            } catch (NonStringException e) {
                throw new UnexpectedTypeException(this.target, this.target.eval(env), "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, env);
            }
        }
        int rangeSize = range.size();
        boolean rightUnbounded = range.isRightUnbounded();
        boolean rightAdaptive = range.isRightAdaptive();
        if (!rightUnbounded && rangeSize == 0) {
            return emptyResult(targetSeq != null);
        }
        int firstIdx = range.getBegining();
        if (firstIdx < 0) {
            throw new _MiscTemplateException(this.keyExpression, "Negative range start index (", Integer.valueOf(firstIdx), ") isn't allowed for a range used for slicing.");
        }
        int step = range.getStep();
        if (targetStr != null) {
            targetSize = targetStr.length();
            targetSizeKnown = true;
        } else if (targetSeq != null) {
            targetSize = targetSeq.size();
            targetSizeKnown = true;
        } else if (targetLazySeq instanceof TemplateCollectionModelEx) {
            targetSize = ((TemplateCollectionModelEx) targetLazySeq).size();
            targetSizeKnown = true;
        } else {
            targetSize = Integer.MAX_VALUE;
            targetSizeKnown = false;
        }
        if (targetSizeKnown && (!rightAdaptive || step != 1 ? firstIdx >= targetSize : firstIdx > targetSize)) {
            Expression expression = this.keyExpression;
            Object[] objArr = new Object[10];
            objArr[0] = "Range start index ";
            objArr[1] = Integer.valueOf(firstIdx);
            objArr[2] = " is out of bounds, because the sliced ";
            objArr[3] = targetStr != null ? "string" : "sequence";
            objArr[4] = " has only ";
            objArr[5] = Integer.valueOf(targetSize);
            objArr[6] = " ";
            objArr[7] = targetStr != null ? "character(s)" : "element(s)";
            objArr[8] = ". ";
            objArr[9] = "(Note that indices are 0-based).";
            throw new _MiscTemplateException(expression, objArr);
        }
        if (!rightUnbounded) {
            int lastIdx = firstIdx + ((rangeSize - 1) * step);
            if (lastIdx < 0) {
                if (!rightAdaptive) {
                    throw new _MiscTemplateException(this.keyExpression, "Negative range end index (", Integer.valueOf(lastIdx), ") isn't allowed for a range used for slicing.");
                }
                resultSize = firstIdx + 1;
            } else if (targetSizeKnown && lastIdx >= targetSize) {
                if (!rightAdaptive) {
                    Expression expression2 = this.keyExpression;
                    Object[] objArr2 = new Object[9];
                    objArr2[0] = "Range end index ";
                    objArr2[1] = Integer.valueOf(lastIdx);
                    objArr2[2] = " is out of bounds, because the sliced ";
                    objArr2[3] = targetStr != null ? "string" : "sequence";
                    objArr2[4] = " has only ";
                    objArr2[5] = Integer.valueOf(targetSize);
                    objArr2[6] = " ";
                    objArr2[7] = targetStr != null ? "character(s)" : "element(s)";
                    objArr2[8] = ". (Note that indices are 0-based).";
                    throw new _MiscTemplateException(expression2, objArr2);
                }
                resultSize = Math.abs(targetSize - firstIdx);
            } else {
                resultSize = rangeSize;
            }
        } else {
            resultSize = targetSizeKnown ? targetSize - firstIdx : -1;
        }
        if (resultSize == 0) {
            return emptyResult(targetSeq != null);
        }
        if (targetSeq != null) {
            ArrayList<TemplateModel> resultList = new ArrayList<>(resultSize);
            int srcIdx = firstIdx;
            for (int i = 0; i < resultSize; i++) {
                resultList.add(targetSeq.get(srcIdx));
                srcIdx += step;
            }
            return new SimpleSequence(resultList, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
        }
        if (targetLazySeq != null) {
            if (step == 1) {
                return getStep1RangeFromIterator(targetLazySeq.iterator(), range, resultSize, targetSizeKnown);
            }
            if (step == -1) {
                return getStepMinus1RangeFromIterator(targetLazySeq.iterator(), range, resultSize);
            }
            throw new AssertionError();
        }
        if (step < 0 && resultSize > 1) {
            if (!range.isAffectedByStringSlicingBug() || resultSize != 2) {
                throw new _MiscTemplateException(this.keyExpression, "Decreasing ranges aren't allowed for slicing strings (as it would give reversed text). The index range was: first = ", Integer.valueOf(firstIdx), ", last = ", Integer.valueOf(firstIdx + ((resultSize - 1) * step)));
            }
            exclEndIdx = firstIdx;
        } else {
            exclEndIdx = firstIdx + resultSize;
        }
        return new SimpleScalar(targetStr.substring(firstIdx, exclEndIdx));
    }

    private TemplateModel getStep1RangeFromIterator(final TemplateModelIterator targetIter, RangeModel range, int resultSize, boolean targetSizeKnown) throws TemplateModelException {
        final int firstIdx = range.getBegining();
        final int lastIdx = firstIdx + (range.size() - 1);
        final boolean rightAdaptive = range.isRightAdaptive();
        final boolean rightUnbounded = range.isRightUnbounded();
        if (this.lazilyGeneratedResultEnabled) {
            TemplateModelIterator iterator = new TemplateModelIterator() { // from class: freemarker.core.DynamicKeyName.1
                private boolean elementsBeforeFirsIndexWereSkipped;
                private int nextIdx;

                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    ensureElementsBeforeFirstIndexWereSkipped();
                    if (!rightUnbounded && this.nextIdx > lastIdx) {
                        throw new _TemplateModelException("Iterator has no more elements (at index ", Integer.valueOf(this.nextIdx), ")");
                    }
                    if (!rightAdaptive && !targetIter.hasNext()) {
                        throw DynamicKeyName.this.newRangeEndOutOfBoundsException(this.nextIdx, lastIdx);
                    }
                    TemplateModel result = targetIter.next();
                    this.nextIdx++;
                    return result;
                }

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() throws TemplateModelException {
                    ensureElementsBeforeFirstIndexWereSkipped();
                    return (rightUnbounded || this.nextIdx <= lastIdx) && (!rightAdaptive || targetIter.hasNext());
                }

                public void ensureElementsBeforeFirstIndexWereSkipped() throws TemplateModelException {
                    if (!this.elementsBeforeFirsIndexWereSkipped) {
                        DynamicKeyName.this.skipElementsBeforeFirstIndex(targetIter, firstIdx);
                        this.nextIdx = firstIdx;
                        this.elementsBeforeFirsIndexWereSkipped = true;
                    }
                }
            };
            return (resultSize == -1 || !targetSizeKnown) ? new LazilyGeneratedCollectionModelWithUnknownSize(iterator, true) : new LazilyGeneratedCollectionModelWithAlreadyKnownSize(iterator, resultSize, true);
        }
        List<TemplateModel> resultList = resultSize != -1 ? new ArrayList<>(resultSize) : new ArrayList<>();
        skipElementsBeforeFirstIndex(targetIter, firstIdx);
        int nextIdx = firstIdx;
        while (true) {
            if (!rightUnbounded && nextIdx > lastIdx) {
                break;
            }
            if (!targetIter.hasNext()) {
                if (!rightAdaptive) {
                    throw newRangeEndOutOfBoundsException(nextIdx, lastIdx);
                }
            } else {
                resultList.add(targetIter.next());
                nextIdx++;
            }
        }
        return new SimpleSequence(resultList, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void skipElementsBeforeFirstIndex(TemplateModelIterator targetIter, int firstIdx) throws TemplateModelException {
        for (int nextIdx = 0; nextIdx < firstIdx; nextIdx++) {
            if (!targetIter.hasNext()) {
                throw new _TemplateModelException(this.keyExpression, "Range start index ", Integer.valueOf(firstIdx), " is out of bounds, as the sliced sequence only has ", Integer.valueOf(nextIdx), " elements.");
            }
            targetIter.next();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public _TemplateModelException newRangeEndOutOfBoundsException(int nextIdx, int lastIdx) {
        return new _TemplateModelException(this.keyExpression, "Range end index ", Integer.valueOf(lastIdx), " is out of bounds, as sliced sequence only has ", Integer.valueOf(nextIdx), " elements.");
    }

    private TemplateModel getStepMinus1RangeFromIterator(TemplateModelIterator targetIter, RangeModel range, int resultSize) throws TemplateException {
        int highIndex = range.getBegining();
        int lowIndex = Math.max(highIndex - (range.size() - 1), 0);
        TemplateModel[] resultElements = new TemplateModel[(highIndex - lowIndex) + 1];
        int srcIdx = 0;
        int dstIdx = resultElements.length - 1;
        while (srcIdx <= highIndex && targetIter.hasNext()) {
            TemplateModel element = targetIter.next();
            if (srcIdx >= lowIndex) {
                int i = dstIdx;
                dstIdx--;
                resultElements[i] = element;
            }
            srcIdx++;
        }
        if (dstIdx != -1) {
            throw new _MiscTemplateException(this, "Range top index " + highIndex + " (0-based) is outside the sliced sequence of length " + srcIdx + ".");
        }
        return new SimpleSequence(Arrays.asList(resultElements), _ObjectWrappers.SAFE_OBJECT_WRAPPER);
    }

    private TemplateModel emptyResult(boolean seq) {
        return seq ? _TemplateAPI.getTemplateLanguageVersionAsInt(this) < _VersionInts.V_2_3_21 ? new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER) : Constants.EMPTY_SEQUENCE : TemplateScalarModel.EMPTY_STRING;
    }

    @Override // freemarker.core.Expression
    void enableLazilyGeneratedResult() {
        this.lazilyGeneratedResultEnabled = true;
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.target.getCanonicalForm() + PropertyAccessor.PROPERTY_KEY_PREFIX + this.keyExpression.getCanonicalForm() + "]";
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "...[...]";
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.constantValue != null || (this.target.isLiteral() && this.keyExpression.isLiteral());
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        return idx == 0 ? this.target : this.keyExpression;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return idx == 0 ? ParameterRole.LEFT_HAND_OPERAND : ParameterRole.ENCLOSED_OPERAND;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new DynamicKeyName(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.keyExpression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }
}
