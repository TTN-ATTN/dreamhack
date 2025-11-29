package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Marker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AddConcatExpression.class */
final class AddConcatExpression extends Expression {
    private final Expression left;
    private final Expression right;

    AddConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return _eval(env, this, this.left, this.left.eval(env), this.right, this.right.eval(env));
    }

    static TemplateModel _eval(Environment env, TemplateObject parent, Expression leftExp, TemplateModel leftModel, Expression rightExp, TemplateModel rightModel) throws TemplateException {
        if ((leftModel instanceof TemplateNumberModel) && (rightModel instanceof TemplateNumberModel)) {
            Number first = EvalUtil.modelToNumber((TemplateNumberModel) leftModel, leftExp);
            Number second = EvalUtil.modelToNumber((TemplateNumberModel) rightModel, rightExp);
            return _evalOnNumbers(env, parent, first, second);
        }
        if ((leftModel instanceof TemplateSequenceModel) && (rightModel instanceof TemplateSequenceModel)) {
            return new ConcatenatedSequence((TemplateSequenceModel) leftModel, (TemplateSequenceModel) rightModel);
        }
        boolean hashConcatPossible = (leftModel instanceof TemplateHashModel) && (rightModel instanceof TemplateHashModel);
        try {
            Object leftOMOrStr = EvalUtil.coerceModelToStringOrMarkup(leftModel, leftExp, hashConcatPossible, null, env);
            if (leftOMOrStr == null) {
                return _eval_concatenateHashes(leftModel, rightModel);
            }
            Object rightOMOrStr = EvalUtil.coerceModelToStringOrMarkup(rightModel, rightExp, hashConcatPossible, null, env);
            if (rightOMOrStr == null) {
                return _eval_concatenateHashes(leftModel, rightModel);
            }
            if (leftOMOrStr instanceof String) {
                if (rightOMOrStr instanceof String) {
                    return new SimpleScalar(((String) leftOMOrStr).concat((String) rightOMOrStr));
                }
                TemplateMarkupOutputModel<?> rightMO = (TemplateMarkupOutputModel) rightOMOrStr;
                return EvalUtil.concatMarkupOutputs(parent, rightMO.getOutputFormat().fromPlainTextByEscaping((String) leftOMOrStr), rightMO);
            }
            TemplateMarkupOutputModel<?> leftMO = (TemplateMarkupOutputModel) leftOMOrStr;
            if (rightOMOrStr instanceof String) {
                return EvalUtil.concatMarkupOutputs(parent, leftMO, leftMO.getOutputFormat().fromPlainTextByEscaping((String) rightOMOrStr));
            }
            return EvalUtil.concatMarkupOutputs(parent, leftMO, (TemplateMarkupOutputModel) rightOMOrStr);
        } catch (NonStringOrTemplateOutputException e) {
            if (hashConcatPossible) {
                return _eval_concatenateHashes(leftModel, rightModel);
            }
            throw e;
        }
    }

    private static TemplateModel _eval_concatenateHashes(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {
        if ((leftModel instanceof TemplateHashModelEx) && (rightModel instanceof TemplateHashModelEx)) {
            TemplateHashModelEx leftModelEx = (TemplateHashModelEx) leftModel;
            TemplateHashModelEx rightModelEx = (TemplateHashModelEx) rightModel;
            if (leftModelEx.size() == 0) {
                return rightModelEx;
            }
            if (rightModelEx.size() == 0) {
                return leftModelEx;
            }
            return new ConcatenatedHashEx(leftModelEx, rightModelEx);
        }
        return new ConcatenatedHash((TemplateHashModel) leftModel, (TemplateHashModel) rightModel);
    }

    static TemplateModel _evalOnNumbers(Environment env, TemplateObject parent, Number first, Number second) throws TemplateException {
        ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
        return new SimpleNumber(ae.add(first, second));
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.constantValue != null || (this.left.isLiteral() && this.right.isLiteral());
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new AddConcatExpression(this.left.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return this.left.getCanonicalForm() + " + " + this.right.getCanonicalForm();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return Marker.ANY_NON_NULL_MARKER;
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        return idx == 0 ? this.left : this.right;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AddConcatExpression$ConcatenatedSequence.class */
    static final class ConcatenatedSequence implements TemplateSequenceModel, TemplateCollectionModelEx {
        private final TemplateSequenceModel left;
        private final TemplateSequenceModel right;

        ConcatenatedSequence(TemplateSequenceModel left, TemplateSequenceModel right) {
            this.left = left;
            this.right = right;
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            int totalSize = 0;
            ConcatenatedSequence[] concSeqsWithRightPending = new ConcatenatedSequence[2];
            int concSeqsWithRightPendingLength = 0;
            ConcatenatedSequence concatenatedSequence = this;
            while (true) {
                ConcatenatedSequence concSeqInFocus = concatenatedSequence;
                TemplateSequenceModel left = concSeqInFocus.left;
                if (left instanceof ConcatenatedSequence) {
                    if (concSeqsWithRightPendingLength == concSeqsWithRightPending.length) {
                        concSeqsWithRightPending = (ConcatenatedSequence[]) Arrays.copyOf(concSeqsWithRightPending, concSeqsWithRightPendingLength * 2);
                    }
                    int i = concSeqsWithRightPendingLength;
                    concSeqsWithRightPendingLength++;
                    concSeqsWithRightPending[i] = concSeqInFocus;
                    concatenatedSequence = (ConcatenatedSequence) left;
                } else {
                    totalSize += left.size();
                    while (true) {
                        TemplateSequenceModel right = concSeqInFocus.right;
                        if (right instanceof ConcatenatedSequence) {
                            concatenatedSequence = (ConcatenatedSequence) right;
                            break;
                        }
                        totalSize += right.size();
                        if (concSeqsWithRightPendingLength == 0) {
                            return totalSize;
                        }
                        concSeqsWithRightPendingLength--;
                        concSeqInFocus = concSeqsWithRightPending[concSeqsWithRightPendingLength];
                    }
                }
            }
        }

        @Override // freemarker.template.TemplateCollectionModelEx
        public boolean isEmpty() throws TemplateModelException {
            ConcatenatedSequence[] concSeqsWithRightPending = new ConcatenatedSequence[2];
            int concSeqsWithRightPendingLength = 0;
            ConcatenatedSequence concatenatedSequence = this;
            while (true) {
                ConcatenatedSequence concSeqInFocus = concatenatedSequence;
                TemplateSequenceModel left = concSeqInFocus.left;
                if (left instanceof ConcatenatedSequence) {
                    if (concSeqsWithRightPendingLength == concSeqsWithRightPending.length) {
                        concSeqsWithRightPending = (ConcatenatedSequence[]) Arrays.copyOf(concSeqsWithRightPending, concSeqsWithRightPendingLength * 2);
                    }
                    int i = concSeqsWithRightPendingLength;
                    concSeqsWithRightPendingLength++;
                    concSeqsWithRightPending[i] = concSeqInFocus;
                    concatenatedSequence = (ConcatenatedSequence) left;
                } else {
                    if (!isEmpty(left)) {
                        return false;
                    }
                    while (true) {
                        TemplateSequenceModel right = concSeqInFocus.right;
                        if (right instanceof ConcatenatedSequence) {
                            concatenatedSequence = (ConcatenatedSequence) right;
                            break;
                        }
                        if (!isEmpty(right)) {
                            return false;
                        }
                        if (concSeqsWithRightPendingLength == 0) {
                            return true;
                        }
                        concSeqsWithRightPendingLength--;
                        concSeqInFocus = concSeqsWithRightPending[concSeqsWithRightPendingLength];
                    }
                }
            }
        }

        private static boolean isEmpty(TemplateSequenceModel seq) throws TemplateModelException {
            return seq instanceof TemplateCollectionModelEx ? ((TemplateCollectionModelEx) seq).isEmpty() : seq.size() == 0;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int index) throws TemplateModelException {
            if (index < 0) {
                return null;
            }
            int totalSize = 0;
            ConcatenatedSequence[] concSeqsWithRightPending = new ConcatenatedSequence[2];
            int concSeqsWithRightPendingLength = 0;
            ConcatenatedSequence concatenatedSequence = this;
            while (true) {
                ConcatenatedSequence concSeqInFocus = concatenatedSequence;
                TemplateSequenceModel left = concSeqInFocus.left;
                if (left instanceof ConcatenatedSequence) {
                    if (concSeqsWithRightPendingLength == concSeqsWithRightPending.length) {
                        concSeqsWithRightPending = (ConcatenatedSequence[]) Arrays.copyOf(concSeqsWithRightPending, concSeqsWithRightPendingLength * 2);
                    }
                    int i = concSeqsWithRightPendingLength;
                    concSeqsWithRightPendingLength++;
                    concSeqsWithRightPending[i] = concSeqInFocus;
                    concatenatedSequence = (ConcatenatedSequence) left;
                } else {
                    int segmentSize = left.size();
                    totalSize += segmentSize;
                    if (totalSize > index) {
                        return left.get(index - (totalSize - segmentSize));
                    }
                    while (true) {
                        TemplateSequenceModel right = concSeqInFocus.right;
                        if (right instanceof ConcatenatedSequence) {
                            concatenatedSequence = (ConcatenatedSequence) right;
                            break;
                        }
                        int segmentSize2 = right.size();
                        totalSize += segmentSize2;
                        if (totalSize > index) {
                            return right.get(index - (totalSize - segmentSize2));
                        }
                        if (concSeqsWithRightPendingLength == 0) {
                            return null;
                        }
                        concSeqsWithRightPendingLength--;
                        concSeqInFocus = concSeqsWithRightPending[concSeqsWithRightPendingLength];
                    }
                }
            }
        }

        @Override // freemarker.template.TemplateCollectionModel
        public TemplateModelIterator iterator() throws TemplateModelException {
            return new ConcatenatedSequenceIterator(this);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AddConcatExpression$ConcatenatedSequenceIterator.class */
    private static class ConcatenatedSequenceIterator implements TemplateModelIterator {
        private final List<ConcatenatedSequence> concSeqsWithRightPending = new ArrayList();
        private ConcatenatedSequence concSeqWithLeftDescentPending;
        private TemplateSequenceModel currentSegment;
        private int currentSegmentNextIndex;
        private TemplateModelIterator currentSegmentIterator;
        private boolean hasPrefetchedResult;
        private TemplateModel prefetchedNext;
        private boolean prefetchedHasNext;

        public ConcatenatedSequenceIterator(ConcatenatedSequence concatSeq) throws TemplateModelException {
            this.concSeqWithLeftDescentPending = concatSeq;
        }

        @Override // freemarker.template.TemplateModelIterator
        public TemplateModel next() throws TemplateModelException {
            ensureHasPrefetchedResult();
            if (!this.prefetchedHasNext) {
                throw new TemplateModelException("The collection has no more elements.");
            }
            TemplateModel result = this.prefetchedNext;
            this.hasPrefetchedResult = false;
            this.prefetchedNext = null;
            return result;
        }

        @Override // freemarker.template.TemplateModelIterator
        public boolean hasNext() throws TemplateModelException {
            ensureHasPrefetchedResult();
            return this.prefetchedHasNext;
        }

        private void ensureHasPrefetchedResult() throws TemplateModelException {
            TemplateSequenceModel leftSeq;
            if (this.hasPrefetchedResult) {
                return;
            }
            while (true) {
                if (this.currentSegmentIterator != null) {
                    boolean hasNext = this.currentSegmentIterator.hasNext();
                    if (hasNext) {
                        this.prefetchedNext = this.currentSegmentIterator.next();
                        this.prefetchedHasNext = true;
                        this.hasPrefetchedResult = true;
                        return;
                    }
                    this.currentSegmentIterator = null;
                } else if (this.currentSegment != null) {
                    int size = this.currentSegment.size();
                    if (this.currentSegmentNextIndex < size) {
                        TemplateSequenceModel templateSequenceModel = this.currentSegment;
                        int i = this.currentSegmentNextIndex;
                        this.currentSegmentNextIndex = i + 1;
                        this.prefetchedNext = templateSequenceModel.get(i);
                        this.prefetchedHasNext = true;
                        this.hasPrefetchedResult = true;
                        return;
                    }
                    this.currentSegment = null;
                } else if (this.concSeqWithLeftDescentPending != null) {
                    ConcatenatedSequence leftDescentCurrentConcSeq = this.concSeqWithLeftDescentPending;
                    this.concSeqWithLeftDescentPending = null;
                    this.concSeqsWithRightPending.add(leftDescentCurrentConcSeq);
                    while (true) {
                        leftSeq = leftDescentCurrentConcSeq.left;
                        if (!(leftSeq instanceof ConcatenatedSequence)) {
                            break;
                        }
                        leftDescentCurrentConcSeq = (ConcatenatedSequence) leftSeq;
                        this.concSeqsWithRightPending.add(leftDescentCurrentConcSeq);
                    }
                    setCurrentSegment(leftSeq);
                }
                if (!this.concSeqsWithRightPending.isEmpty()) {
                    TemplateSequenceModel right = this.concSeqsWithRightPending.remove(this.concSeqsWithRightPending.size() - 1).right;
                    if (right instanceof ConcatenatedSequence) {
                        this.concSeqWithLeftDescentPending = (ConcatenatedSequence) right;
                    } else {
                        setCurrentSegment(right);
                    }
                } else {
                    this.prefetchedNext = null;
                    this.prefetchedHasNext = false;
                    this.hasPrefetchedResult = true;
                    return;
                }
            }
        }

        private void setCurrentSegment(TemplateSequenceModel currentSegment) throws TemplateModelException {
            if (currentSegment instanceof TemplateCollectionModel) {
                this.currentSegmentIterator = ((TemplateCollectionModel) currentSegment).iterator();
                this.currentSegment = null;
            } else {
                this.currentSegment = currentSegment;
                this.currentSegmentNextIndex = 0;
                this.currentSegmentIterator = null;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AddConcatExpression$ConcatenatedHash.class */
    private static class ConcatenatedHash implements TemplateHashModel {
        protected final TemplateHashModel left;
        protected final TemplateHashModel right;

        ConcatenatedHash(TemplateHashModel left, TemplateHashModel right) {
            this.left = left;
            this.right = right;
        }

        @Override // freemarker.template.TemplateHashModel
        public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel model = this.right.get(key);
            return model != null ? model : this.left.get(key);
        }

        @Override // freemarker.template.TemplateHashModel
        public boolean isEmpty() throws TemplateModelException {
            return this.left.isEmpty() && this.right.isEmpty();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/AddConcatExpression$ConcatenatedHashEx.class */
    private static final class ConcatenatedHashEx extends ConcatenatedHash implements TemplateHashModelEx {
        private CollectionAndSequence keys;
        private CollectionAndSequence values;

        ConcatenatedHashEx(TemplateHashModelEx left, TemplateHashModelEx right) {
            super(left, right);
        }

        @Override // freemarker.template.TemplateHashModelEx
        public int size() throws TemplateModelException {
            initKeys();
            return this.keys.size();
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel keys() throws TemplateModelException {
            initKeys();
            return this.keys;
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel values() throws TemplateModelException {
            initValues();
            return this.values;
        }

        private void initKeys() throws TemplateModelException {
            if (this.keys == null) {
                HashSet keySet = new HashSet();
                SimpleSequence keySeq = new SimpleSequence(32, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
                addKeys(keySet, keySeq, (TemplateHashModelEx) this.left);
                addKeys(keySet, keySeq, (TemplateHashModelEx) this.right);
                this.keys = new CollectionAndSequence(keySeq);
            }
        }

        private static void addKeys(Set keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
            TemplateModelIterator it = hash.keys().iterator();
            while (it.hasNext()) {
                TemplateScalarModel tsm = (TemplateScalarModel) it.next();
                if (keySet.add(tsm.getAsString())) {
                    keySeq.add(tsm);
                }
            }
        }

        private void initValues() throws TemplateModelException {
            if (this.values == null) {
                SimpleSequence seq = new SimpleSequence(size(), _ObjectWrappers.SAFE_OBJECT_WRAPPER);
                int ln = this.keys.size();
                for (int i = 0; i < ln; i++) {
                    seq.add(get(((TemplateScalarModel) this.keys.get(i)).getAsString()));
                }
                this.values = new CollectionAndSequence(seq);
            }
        }
    }
}
