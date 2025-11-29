package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import freemarker.core.IntermediateStreamOperationLikeBuiltIn;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.Constants;
import freemarker.template.utility.StringUtil;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences.class */
class BuiltInsForSequences {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$chunkBI.class */
    static class chunkBI extends BuiltInForSequence {
        chunkBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$chunkBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private final TemplateSequenceModel tsm;

            private BIMethod(TemplateSequenceModel tsm) {
                this.tsm = tsm;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                chunkBI.this.checkMethodArgCount(args, 1, 2);
                int chunkSize = chunkBI.this.getNumberMethodArg(args, 0).intValue();
                if (chunkSize < 1) {
                    throw new _TemplateModelException("The 1st argument to ?", chunkBI.this.key, " (...) must be at least 1.");
                }
                return new ChunkedSequence(this.tsm, chunkSize, args.size() > 1 ? (TemplateModel) args.get(1) : null);
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$chunkBI$ChunkedSequence.class */
        private static class ChunkedSequence implements TemplateSequenceModel {
            private final TemplateSequenceModel wrappedTsm;
            private final int chunkSize;
            private final TemplateModel fillerItem;
            private final int numberOfChunks;

            private ChunkedSequence(TemplateSequenceModel wrappedTsm, int chunkSize, TemplateModel fillerItem) throws TemplateModelException {
                this.wrappedTsm = wrappedTsm;
                this.chunkSize = chunkSize;
                this.fillerItem = fillerItem;
                this.numberOfChunks = ((wrappedTsm.size() + chunkSize) - 1) / chunkSize;
            }

            @Override // freemarker.template.TemplateSequenceModel
            public TemplateModel get(final int chunkIndex) throws TemplateModelException {
                if (chunkIndex >= this.numberOfChunks) {
                    return null;
                }
                return new TemplateSequenceModel() { // from class: freemarker.core.BuiltInsForSequences.chunkBI.ChunkedSequence.1
                    private final int baseIndex;

                    {
                        this.baseIndex = chunkIndex * ChunkedSequence.this.chunkSize;
                    }

                    @Override // freemarker.template.TemplateSequenceModel
                    public TemplateModel get(int relIndex) throws TemplateModelException {
                        int absIndex = this.baseIndex + relIndex;
                        if (absIndex < ChunkedSequence.this.wrappedTsm.size()) {
                            return ChunkedSequence.this.wrappedTsm.get(absIndex);
                        }
                        if (absIndex < ChunkedSequence.this.numberOfChunks * ChunkedSequence.this.chunkSize) {
                            return ChunkedSequence.this.fillerItem;
                        }
                        return null;
                    }

                    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
                    public int size() throws TemplateModelException {
                        return (ChunkedSequence.this.fillerItem != null || chunkIndex + 1 < ChunkedSequence.this.numberOfChunks) ? ChunkedSequence.this.chunkSize : ChunkedSequence.this.wrappedTsm.size() - this.baseIndex;
                    }
                };
            }

            @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
            public int size() throws TemplateModelException {
                return this.numberOfChunks;
            }
        }

        @Override // freemarker.core.BuiltInForSequence
        TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
            return new BIMethod(tsm);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$firstBI.class */
    static class firstBI extends BuiltIn {
        firstBI() {
        }

        @Override // freemarker.core.BuiltIn
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if ((model instanceof TemplateSequenceModel) && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return calculateResultForSequence((TemplateSequenceModel) model);
            }
            if (model instanceof TemplateCollectionModel) {
                return calculateResultForColletion((TemplateCollectionModel) model);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private TemplateModel calculateResultForSequence(TemplateSequenceModel seq) throws TemplateModelException {
            if (seq.size() == 0) {
                return null;
            }
            return seq.get(0);
        }

        private TemplateModel calculateResultForColletion(TemplateCollectionModel coll) throws TemplateModelException {
            TemplateModelIterator iter = coll.iterator();
            if (!iter.hasNext()) {
                return null;
            }
            return iter.next();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$joinBI.class */
    static class joinBI extends BuiltInWithDirectCallOptimization {
        joinBI() {
        }

        @Override // freemarker.core.BuiltInWithDirectCallOptimization
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$joinBI$BIMethodForCollection.class */
        private class BIMethodForCollection implements TemplateMethodModelEx {
            private final Environment env;
            private final TemplateCollectionModel coll;

            private BIMethodForCollection(Environment env, TemplateCollectionModel coll) {
                this.env = env;
                this.coll = coll;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                joinBI.this.checkMethodArgCount(args, 1, 3);
                String separator = joinBI.this.getStringMethodArg(args, 0);
                String whenEmpty = joinBI.this.getOptStringMethodArg(args, 1);
                String afterLast = joinBI.this.getOptStringMethodArg(args, 2);
                StringBuilder sb = new StringBuilder();
                TemplateModelIterator it = this.coll.iterator();
                int idx = 0;
                boolean hadItem = false;
                while (it.hasNext()) {
                    TemplateModel item = it.next();
                    if (item != null) {
                        if (hadItem) {
                            sb.append(separator);
                        } else {
                            hadItem = true;
                        }
                        try {
                            sb.append(EvalUtil.coerceModelToStringOrUnsupportedMarkup(item, null, null, this.env));
                        } catch (TemplateException e) {
                            throw new _TemplateModelException(e, "\"?", joinBI.this.key, "\" failed at index ", Integer.valueOf(idx), " with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---");
                        }
                    }
                    idx++;
                }
                if (hadItem) {
                    if (afterLast != null) {
                        sb.append(afterLast);
                    }
                } else if (whenEmpty != null) {
                    sb.append(whenEmpty);
                }
                return new SimpleScalar(sb.toString());
            }
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateCollectionModel) {
                BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
                return new BIMethodForCollection(env, (TemplateCollectionModel) model);
            }
            if (model instanceof TemplateSequenceModel) {
                return new BIMethodForCollection(env, new CollectionAndSequence((TemplateSequenceModel) model));
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$lastBI.class */
    static class lastBI extends BuiltInForSequence {
        lastBI() {
        }

        @Override // freemarker.core.BuiltInForSequence
        TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
            int size = tsm.size();
            if (size == 0) {
                return null;
            }
            return tsm.get(size - 1);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$reverseBI.class */
    static class reverseBI extends BuiltInForSequence {

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$reverseBI$ReverseSequence.class */
        private static class ReverseSequence implements TemplateSequenceModel {
            private final TemplateSequenceModel seq;

            ReverseSequence(TemplateSequenceModel seq) {
                this.seq = seq;
            }

            @Override // freemarker.template.TemplateSequenceModel
            public TemplateModel get(int index) throws TemplateModelException {
                return this.seq.get((this.seq.size() - 1) - index);
            }

            @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
            public int size() throws TemplateModelException {
                return this.seq.size();
            }
        }

        reverseBI() {
        }

        @Override // freemarker.core.BuiltInForSequence
        TemplateModel calculateResult(TemplateSequenceModel tsm) {
            if (tsm instanceof ReverseSequence) {
                return ((ReverseSequence) tsm).seq;
            }
            return new ReverseSequence(tsm);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$seq_containsBI.class */
    static class seq_containsBI extends BuiltInWithDirectCallOptimization {
        seq_containsBI() {
        }

        @Override // freemarker.core.BuiltInWithDirectCallOptimization
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$seq_containsBI$BIMethodForCollection.class */
        private class BIMethodForCollection implements TemplateMethodModelEx {
            private TemplateCollectionModel m_coll;
            private Environment m_env;

            private BIMethodForCollection(TemplateCollectionModel coll, Environment env) {
                this.m_coll = coll;
                this.m_env = env;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                seq_containsBI.this.checkMethodArgCount(args, 1);
                TemplateModel arg = (TemplateModel) args.get(0);
                TemplateModelIterator it = this.m_coll.iterator();
                int idx = 0;
                while (it.hasNext()) {
                    if (BuiltInsForSequences.modelsEqual(idx, it.next(), arg, this.m_env)) {
                        return TemplateBooleanModel.TRUE;
                    }
                    idx++;
                }
                return TemplateBooleanModel.FALSE;
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$seq_containsBI$BIMethodForSequence.class */
        private class BIMethodForSequence implements TemplateMethodModelEx {
            private TemplateSequenceModel m_seq;
            private Environment m_env;

            private BIMethodForSequence(TemplateSequenceModel seq, Environment env) {
                this.m_seq = seq;
                this.m_env = env;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                seq_containsBI.this.checkMethodArgCount(args, 1);
                TemplateModel arg = (TemplateModel) args.get(0);
                int size = this.m_seq.size();
                for (int i = 0; i < size; i++) {
                    if (BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), arg, this.m_env)) {
                        return TemplateBooleanModel.TRUE;
                    }
                }
                return TemplateBooleanModel.FALSE;
            }
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if ((model instanceof TemplateSequenceModel) && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return new BIMethodForSequence((TemplateSequenceModel) model, env);
            }
            if (model instanceof TemplateCollectionModel) {
                return new BIMethodForCollection((TemplateCollectionModel) model, env);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$seq_index_ofBI.class */
    static class seq_index_ofBI extends BuiltInWithDirectCallOptimization {
        private boolean findFirst;

        @Override // freemarker.core.BuiltInWithDirectCallOptimization
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$seq_index_ofBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            protected final TemplateSequenceModel m_seq;
            protected final TemplateCollectionModel m_col;
            protected final Environment m_env;

            private BIMethod(Environment env) throws TemplateException {
                TemplateModel model = seq_index_ofBI.this.target.eval(env);
                this.m_seq = (!(model instanceof TemplateSequenceModel) || BuiltInsForSequences.isBuggySeqButGoodCollection(model)) ? null : (TemplateSequenceModel) model;
                this.m_col = (this.m_seq == null && (model instanceof TemplateCollectionModel)) ? (TemplateCollectionModel) model : null;
                if (this.m_seq == null && this.m_col == null) {
                    throw new NonSequenceOrCollectionException(seq_index_ofBI.this.target, model, env);
                }
                this.m_env = env;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public final Object exec(List args) throws TemplateModelException {
                int iFindInCol;
                int foundAtIdx;
                int iFindInCol2;
                int argCnt = args.size();
                seq_index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
                TemplateModel searched = (TemplateModel) args.get(0);
                if (argCnt > 1) {
                    int startIndex = seq_index_ofBI.this.getNumberMethodArg(args, 1).intValue();
                    if (this.m_seq != null) {
                        iFindInCol2 = findInSeq(searched, startIndex);
                    } else {
                        iFindInCol2 = findInCol(searched, startIndex);
                    }
                    foundAtIdx = iFindInCol2;
                } else {
                    if (this.m_seq != null) {
                        iFindInCol = findInSeq(searched);
                    } else {
                        iFindInCol = findInCol(searched);
                    }
                    foundAtIdx = iFindInCol;
                }
                return foundAtIdx == -1 ? Constants.MINUS_ONE : new SimpleNumber(foundAtIdx);
            }

            int findInCol(TemplateModel searched) throws TemplateModelException {
                return findInCol(searched, 0, Integer.MAX_VALUE);
            }

            protected int findInCol(TemplateModel searched, int startIndex) throws TemplateModelException {
                if (seq_index_ofBI.this.findFirst) {
                    return findInCol(searched, startIndex, Integer.MAX_VALUE);
                }
                return findInCol(searched, 0, startIndex);
            }

            protected int findInCol(TemplateModel searched, int allowedRangeStart, int allowedRangeEnd) throws TemplateModelException {
                if (allowedRangeEnd < 0) {
                    return -1;
                }
                TemplateModelIterator it = this.m_col.iterator();
                int foundAtIdx = -1;
                for (int idx = 0; it.hasNext() && idx <= allowedRangeEnd; idx++) {
                    TemplateModel current = it.next();
                    if (idx >= allowedRangeStart && BuiltInsForSequences.modelsEqual(idx, current, searched, this.m_env)) {
                        foundAtIdx = idx;
                        if (seq_index_ofBI.this.findFirst) {
                            break;
                        }
                    }
                }
                return foundAtIdx;
            }

            int findInSeq(TemplateModel searched) throws TemplateModelException {
                int actualStartIndex;
                int seqSize = this.m_seq.size();
                if (seq_index_ofBI.this.findFirst) {
                    actualStartIndex = 0;
                } else {
                    actualStartIndex = seqSize - 1;
                }
                return findInSeq(searched, actualStartIndex, seqSize);
            }

            private int findInSeq(TemplateModel searched, int startIndex) throws TemplateModelException {
                int seqSize = this.m_seq.size();
                if (seq_index_ofBI.this.findFirst) {
                    if (startIndex >= seqSize) {
                        return -1;
                    }
                    if (startIndex < 0) {
                        startIndex = 0;
                    }
                } else {
                    if (startIndex >= seqSize) {
                        startIndex = seqSize - 1;
                    }
                    if (startIndex < 0) {
                        return -1;
                    }
                }
                return findInSeq(searched, startIndex, seqSize);
            }

            private int findInSeq(TemplateModel target, int scanStartIndex, int seqSize) throws TemplateModelException {
                if (seq_index_ofBI.this.findFirst) {
                    for (int i = scanStartIndex; i < seqSize; i++) {
                        if (BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), target, this.m_env)) {
                            return i;
                        }
                    }
                    return -1;
                }
                for (int i2 = scanStartIndex; i2 >= 0; i2--) {
                    if (BuiltInsForSequences.modelsEqual(i2, this.m_seq.get(i2), target, this.m_env)) {
                        return i2;
                    }
                }
                return -1;
            }
        }

        seq_index_ofBI(boolean findFirst) {
            this.findFirst = findFirst;
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            return new BIMethod(env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sort_byBI.class */
    static class sort_byBI extends sortBI {
        sort_byBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sort_byBI$BIMethod.class */
        class BIMethod implements TemplateMethodModelEx {
            TemplateSequenceModel seq;

            BIMethod(TemplateSequenceModel seq) {
                this.seq = seq;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                String[] subvars;
                if (args.size() < 1) {
                    throw _MessageUtil.newArgCntError(CallerData.NA + sort_byBI.this.key, args.size(), 1);
                }
                Object obj = args.get(0);
                if (obj instanceof TemplateScalarModel) {
                    subvars = new String[]{((TemplateScalarModel) obj).getAsString()};
                } else if (obj instanceof TemplateSequenceModel) {
                    TemplateSequenceModel seq = (TemplateSequenceModel) obj;
                    int ln = seq.size();
                    subvars = new String[ln];
                    for (int i = 0; i < ln; i++) {
                        Object item = seq.get(i);
                        try {
                            subvars[i] = ((TemplateScalarModel) item).getAsString();
                        } catch (ClassCastException e) {
                            if (!(item instanceof TemplateScalarModel)) {
                                throw new _TemplateModelException("The argument to ?", sort_byBI.this.key, "(key), when it's a sequence, must be a sequence of strings, but the item at index ", Integer.valueOf(i), " is not a string.");
                            }
                        }
                    }
                } else {
                    throw new _TemplateModelException("The argument to ?", sort_byBI.this.key, "(key) must be a string (the name of the subvariable), or a sequence of strings (the \"path\" to the subvariable).");
                }
                return sortBI.sort(this.seq, subvars);
            }
        }

        @Override // freemarker.core.BuiltInsForSequences.sortBI, freemarker.core.BuiltInForSequence
        TemplateModel calculateResult(TemplateSequenceModel seq) {
            return new BIMethod(seq);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI.class */
    static class sortBI extends BuiltInForSequence {
        static final int KEY_TYPE_NOT_YET_DETECTED = 0;
        static final int KEY_TYPE_STRING = 1;
        static final int KEY_TYPE_NUMBER = 2;
        static final int KEY_TYPE_DATE = 3;
        static final int KEY_TYPE_BOOLEAN = 4;

        sortBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI$BooleanKVPComparator.class */
        private static class BooleanKVPComparator implements Comparator, Serializable {
            private BooleanKVPComparator() {
            }

            @Override // java.util.Comparator
            public int compare(Object arg0, Object arg1) {
                boolean b0 = ((Boolean) ((KVP) arg0).key).booleanValue();
                boolean b1 = ((Boolean) ((KVP) arg1).key).booleanValue();
                return b0 ? b1 ? 0 : 1 : b1 ? -1 : 0;
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI$DateKVPComparator.class */
        private static class DateKVPComparator implements Comparator, Serializable {
            private DateKVPComparator() {
            }

            @Override // java.util.Comparator
            public int compare(Object arg0, Object arg1) {
                return ((Date) ((KVP) arg0).key).compareTo((Date) ((KVP) arg1).key);
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI$KVP.class */
        private static class KVP {
            private Object key;
            private Object value;

            private KVP(Object key, Object value) {
                this.key = key;
                this.value = value;
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI$LexicalKVPComparator.class */
        private static class LexicalKVPComparator implements Comparator {
            private Collator collator;

            LexicalKVPComparator(Collator collator) {
                this.collator = collator;
            }

            @Override // java.util.Comparator
            public int compare(Object arg0, Object arg1) {
                return this.collator.compare(((KVP) arg0).key, ((KVP) arg1).key);
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sortBI$NumericalKVPComparator.class */
        private static class NumericalKVPComparator implements Comparator {
            private ArithmeticEngine ae;

            private NumericalKVPComparator(ArithmeticEngine ae) {
                this.ae = ae;
            }

            @Override // java.util.Comparator
            public int compare(Object arg0, Object arg1) {
                try {
                    return this.ae.compareNumbers((Number) ((KVP) arg0).key, (Number) ((KVP) arg1).key);
                } catch (TemplateException e) {
                    throw new ClassCastException("Failed to compare numbers: " + e);
                }
            }
        }

        static TemplateModelException newInconsistentSortKeyTypeException(int keyNamesLn, String firstType, String firstTypePlural, int index, TemplateModel key) {
            String valueInMsg;
            String valuesInMsg;
            if (keyNamesLn == 0) {
                valueInMsg = "value";
                valuesInMsg = "values";
            } else {
                valueInMsg = "key value";
                valuesInMsg = "key values";
            }
            return new _TemplateModelException(startErrorMessage(keyNamesLn, index), "All ", valuesInMsg, " in the sequence must be ", firstTypePlural, ", because the first ", valueInMsg, " was that. However, the ", valueInMsg, " of the current item isn't a ", firstType, " but a ", new _DelayedFTLTypeDescription(key), ".");
        }

        static TemplateSequenceModel sort(TemplateSequenceModel seq, String[] keyNames) throws TemplateModelException {
            int ln = seq.size();
            if (ln == 0) {
                return seq;
            }
            ArrayList res = new ArrayList(ln);
            int keyNamesLn = keyNames == null ? 0 : keyNames.length;
            int keyType = 0;
            Comparator keyComparator = null;
            for (int i = 0; i < ln; i++) {
                TemplateModel item = seq.get(i);
                TemplateModel key = item;
                int keyNameI = 0;
                while (keyNameI < keyNamesLn) {
                    try {
                        key = ((TemplateHashModel) key).get(keyNames[keyNameI]);
                        if (key != null) {
                            keyNameI++;
                        } else {
                            throw new _TemplateModelException(startErrorMessage(keyNamesLn, i), "The " + StringUtil.jQuote(keyNames[keyNameI]), " subvariable was null or missing.");
                        }
                    } catch (ClassCastException e) {
                        if (!(key instanceof TemplateHashModel)) {
                            Object[] objArr = new Object[6];
                            objArr[0] = startErrorMessage(keyNamesLn, i);
                            objArr[1] = keyNameI == 0 ? "Sequence items must be hashes when using ?sort_by. " : "The " + StringUtil.jQuote(keyNames[keyNameI - 1]);
                            objArr[2] = " subvariable is not a hash, so ?sort_by ";
                            objArr[3] = "can't proceed with getting the ";
                            objArr[4] = new _DelayedJQuote(keyNames[keyNameI]);
                            objArr[5] = " subvariable.";
                            throw new _TemplateModelException(objArr);
                        }
                        throw e;
                    }
                }
                if (keyType == 0) {
                    if (key instanceof TemplateScalarModel) {
                        keyType = 1;
                        keyComparator = new LexicalKVPComparator(Environment.getCurrentEnvironment().getCollator());
                    } else if (key instanceof TemplateNumberModel) {
                        keyType = 2;
                        keyComparator = new NumericalKVPComparator(Environment.getCurrentEnvironment().getArithmeticEngine());
                    } else if (key instanceof TemplateDateModel) {
                        keyType = 3;
                        keyComparator = new DateKVPComparator();
                    } else if (key instanceof TemplateBooleanModel) {
                        keyType = 4;
                        keyComparator = new BooleanKVPComparator();
                    } else {
                        throw new _TemplateModelException(startErrorMessage(keyNamesLn, i), "Values used for sorting must be numbers, strings, date/times or booleans.");
                    }
                }
                switch (keyType) {
                    case 1:
                        try {
                            res.add(new KVP(((TemplateScalarModel) key).getAsString(), item));
                            break;
                        } catch (ClassCastException e2) {
                            if (!(key instanceof TemplateScalarModel)) {
                                throw newInconsistentSortKeyTypeException(keyNamesLn, "string", "strings", i, key);
                            }
                            throw e2;
                        }
                    case 2:
                        try {
                            res.add(new KVP(((TemplateNumberModel) key).getAsNumber(), item));
                            break;
                        } catch (ClassCastException e3) {
                            if (!(key instanceof TemplateNumberModel)) {
                                throw newInconsistentSortKeyTypeException(keyNamesLn, "number", "numbers", i, key);
                            }
                            break;
                        }
                    case 3:
                        try {
                            res.add(new KVP(((TemplateDateModel) key).getAsDate(), item));
                            break;
                        } catch (ClassCastException e4) {
                            if (!(key instanceof TemplateDateModel)) {
                                throw newInconsistentSortKeyTypeException(keyNamesLn, "date/time", "date/times", i, key);
                            }
                            break;
                        }
                    case 4:
                        try {
                            res.add(new KVP(Boolean.valueOf(((TemplateBooleanModel) key).getAsBoolean()), item));
                            break;
                        } catch (ClassCastException e5) {
                            if (!(key instanceof TemplateBooleanModel)) {
                                throw newInconsistentSortKeyTypeException(keyNamesLn, "boolean", "booleans", i, key);
                            }
                            break;
                        }
                    default:
                        throw new BugException("Unexpected key type");
                }
            }
            try {
                Collections.sort(res, keyComparator);
                for (int i2 = 0; i2 < ln; i2++) {
                    res.set(i2, ((KVP) res.get(i2)).value);
                }
                return new TemplateModelListSequence(res);
            } catch (Exception exc) {
                throw new _TemplateModelException(exc, startErrorMessage(keyNamesLn), "Unexpected error while sorting:" + exc);
            }
        }

        static Object[] startErrorMessage(int keyNamesLn) {
            Object[] objArr = new Object[2];
            objArr[0] = keyNamesLn == 0 ? "?sort" : "?sort_by(...)";
            objArr[1] = " failed: ";
            return objArr;
        }

        static Object[] startErrorMessage(int keyNamesLn, int index) {
            Object[] objArr = new Object[4];
            objArr[0] = keyNamesLn == 0 ? "?sort" : "?sort_by(...)";
            objArr[1] = " failed at sequence index ";
            objArr[2] = Integer.valueOf(index);
            objArr[3] = index == 0 ? ": " : " (0-based): ";
            return objArr;
        }

        @Override // freemarker.core.BuiltInForSequence
        TemplateModel calculateResult(TemplateSequenceModel seq) throws TemplateModelException {
            return sort(seq, null);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$sequenceBI.class */
    static class sequenceBI extends BuiltIn {
        private boolean lazilyGeneratedResultEnabled;

        sequenceBI() {
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if ((model instanceof TemplateSequenceModel) && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return model;
            }
            if (!(model instanceof TemplateCollectionModel)) {
                throw new NonSequenceOrCollectionException(this.target, model, env);
            }
            TemplateCollectionModel coll = (TemplateCollectionModel) model;
            if (!this.lazilyGeneratedResultEnabled) {
                SimpleSequence seq = coll instanceof TemplateCollectionModelEx ? new SimpleSequence(((TemplateCollectionModelEx) coll).size(), _ObjectWrappers.SAFE_OBJECT_WRAPPER) : new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                TemplateModelIterator iter = coll.iterator();
                while (iter.hasNext()) {
                    seq.add(iter.next());
                }
                return seq;
            }
            if (coll instanceof LazilyGeneratedCollectionModel) {
                return ((LazilyGeneratedCollectionModel) coll).withIsSequenceTrue();
            }
            return coll instanceof TemplateCollectionModelEx ? new LazilyGeneratedCollectionModelWithSameSizeCollEx(new LazyCollectionTemplateModelIterator(coll), (TemplateCollectionModelEx) coll, true) : new LazilyGeneratedCollectionModelWithUnknownSize(new LazyCollectionTemplateModelIterator(coll), true);
        }

        @Override // freemarker.core.Expression
        void enableLazilyGeneratedResult() {
            this.lazilyGeneratedResultEnabled = true;
        }

        @Override // freemarker.core.BuiltIn
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isBuggySeqButGoodCollection(TemplateModel model) {
        return (model instanceof CollectionModel) && !((CollectionModel) model).getSupportsIndexedAccess();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkNotRightUnboundedNumericalRange(TemplateModel model) throws TemplateModelException {
        if (model instanceof RightUnboundedRangeModel) {
            throw new _TemplateModelException("The input sequence is a right-unbounded numerical range, thus, it's infinitely long, and can't processed with this built-in.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean modelsEqual(int seqItemIndex, TemplateModel seqItem, TemplateModel searchedItem, Environment env) throws TemplateModelException {
        try {
            return EvalUtil.compare(seqItem, null, 1, null, searchedItem, null, null, false, true, true, true, env);
        } catch (TemplateException ex) {
            throw new _TemplateModelException(ex, "This error has occurred when comparing sequence item at 0-based index ", Integer.valueOf(seqItemIndex), " to the searched item:\n", new _DelayedGetMessage(ex));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$MinOrMaxBI.class */
    private static abstract class MinOrMaxBI extends BuiltIn {
        private final int comparatorOperator;

        protected MinOrMaxBI(int comparatorOperator) {
            this.comparatorOperator = comparatorOperator;
        }

        @Override // freemarker.core.BuiltIn
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateCollectionModel) {
                BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
                return calculateResultForCollection((TemplateCollectionModel) model, env);
            }
            if (model instanceof TemplateSequenceModel) {
                return calculateResultForSequence((TemplateSequenceModel) model, env);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private TemplateModel calculateResultForCollection(TemplateCollectionModel coll, Environment env) throws TemplateException {
            TemplateModel best = null;
            TemplateModelIterator iter = coll.iterator();
            while (iter.hasNext()) {
                TemplateModel cur = iter.next();
                if (cur != null && (best == null || EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env))) {
                    best = cur;
                }
            }
            return best;
        }

        private TemplateModel calculateResultForSequence(TemplateSequenceModel seq, Environment env) throws TemplateException {
            TemplateModel best = null;
            for (int i = 0; i < seq.size(); i++) {
                TemplateModel cur = seq.get(i);
                if (cur != null && (best == null || EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env))) {
                    best = cur;
                }
            }
            return best;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$maxBI.class */
    static class maxBI extends MinOrMaxBI {
        public maxBI() {
            super(4);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$minBI.class */
    static class minBI extends MinOrMaxBI {
        public minBI() {
            super(3);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$FilterLikeBI.class */
    private static abstract class FilterLikeBI extends IntermediateStreamOperationLikeBuiltIn {
        private FilterLikeBI() {
        }

        protected final boolean elementMatches(TemplateModel element, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
            TemplateModel transformedElement = elementTransformer.transformElement(element, env);
            if (!(transformedElement instanceof TemplateBooleanModel)) {
                if (transformedElement == null) {
                    throw new _TemplateModelException(getElementTransformerExp(), env, "The filter expression has returned no value (has returned null), rather than a boolean.");
                }
                throw new _TemplateModelException(getElementTransformerExp(), env, "The filter expression had to return a boolean value, but it returned ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(transformedElement)), " instead.");
            }
            return ((TemplateBooleanModel) transformedElement).getAsBoolean();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$filterBI.class */
    static class filterBI extends FilterLikeBI {
        filterBI() {
            super();
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                List<TemplateModel> resultList = new ArrayList<>();
                while (lhoIterator.hasNext()) {
                    TemplateModel element = lhoIterator.next();
                    if (elementMatches(element, elementTransformer, env)) {
                        resultList.add(element);
                    }
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForSequences.filterBI.1
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() throws TemplateModelException {
                    ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                private void ensurePrefetchDone() throws TemplateModelException {
                    if (this.prefetchDone) {
                        return;
                    }
                    boolean conclusionReached = false;
                    do {
                        if (lhoIterator.hasNext()) {
                            TemplateModel element2 = lhoIterator.next();
                            try {
                                boolean elementMatched = filterBI.this.elementMatches(element2, elementTransformer, env);
                                if (elementMatched) {
                                    this.prefetchedElement = element2;
                                    conclusionReached = true;
                                }
                            } catch (TemplateException e) {
                                throw new _TemplateModelException(e, env, "Failed to transform element");
                            }
                        } else {
                            this.prefetchedEndOfIterator = true;
                            this.prefetchedElement = null;
                            conclusionReached = true;
                        }
                    } while (!conclusionReached);
                    this.prefetchDone = true;
                }
            }, lhoIsSequence);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$take_whileBI.class */
    static class take_whileBI extends FilterLikeBI {
        take_whileBI() {
            super();
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                List<TemplateModel> resultList = new ArrayList<>();
                while (lhoIterator.hasNext()) {
                    TemplateModel element = lhoIterator.next();
                    if (!elementMatches(element, elementTransformer, env)) {
                        break;
                    }
                    resultList.add(element);
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForSequences.take_whileBI.1
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() throws TemplateModelException {
                    ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                private void ensurePrefetchDone() throws TemplateModelException {
                    if (this.prefetchDone) {
                        return;
                    }
                    if (lhoIterator.hasNext()) {
                        TemplateModel element2 = lhoIterator.next();
                        try {
                            boolean elementMatched = take_whileBI.this.elementMatches(element2, elementTransformer, env);
                            if (elementMatched) {
                                this.prefetchedElement = element2;
                            } else {
                                this.prefetchedEndOfIterator = true;
                                this.prefetchedElement = null;
                            }
                        } catch (TemplateException e) {
                            throw new _TemplateModelException(e, env, "Failed to transform element");
                        }
                    } else {
                        this.prefetchedEndOfIterator = true;
                        this.prefetchedElement = null;
                    }
                    this.prefetchDone = true;
                }
            }, lhoIsSequence);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$mapBI.class */
    static class mapBI extends IntermediateStreamOperationLikeBuiltIn {
        mapBI() {
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                List<TemplateModel> resultList = new ArrayList<>();
                while (lhoIterator.hasNext()) {
                    resultList.add(fetchAndMapNextElement(lhoIterator, elementTransformer, env));
                }
                return new TemplateModelListSequence(resultList);
            }
            TemplateModelIterator mappedLhoIterator = new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForSequences.mapBI.1
                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    try {
                        return mapBI.this.fetchAndMapNextElement(lhoIterator, elementTransformer, env);
                    } catch (TemplateException e) {
                        throw new _TemplateModelException(e, env, "Failed to transform element");
                    }
                }

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() throws TemplateModelException {
                    return lhoIterator.hasNext();
                }
            };
            if (lho instanceof TemplateCollectionModelEx) {
                return new LazilyGeneratedCollectionModelWithSameSizeCollEx(mappedLhoIterator, (TemplateCollectionModelEx) lho, lhoIsSequence);
            }
            if (lho instanceof TemplateSequenceModel) {
                return new LazilyGeneratedCollectionModelWithSameSizeSeq(mappedLhoIterator, (TemplateSequenceModel) lho);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(mappedLhoIterator, lhoIsSequence);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public TemplateModel fetchAndMapNextElement(TemplateModelIterator lhoIterator, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
            TemplateModel transformedElement = elementTransformer.transformElement(lhoIterator.next(), env);
            if (transformedElement == null) {
                throw new _TemplateModelException(getElementTransformerExp(), env, "The element mapper function has returned no return value (has returned null).");
            }
            return transformedElement;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForSequences$drop_whileBI.class */
    static class drop_whileBI extends FilterLikeBI {
        drop_whileBI() {
            super();
        }

        @Override // freemarker.core.IntermediateStreamOperationLikeBuiltIn
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                List<TemplateModel> resultList = new ArrayList<>();
                while (true) {
                    if (!lhoIterator.hasNext()) {
                        break;
                    }
                    TemplateModel element = lhoIterator.next();
                    if (!elementMatches(element, elementTransformer, env)) {
                        resultList.add(element);
                        while (lhoIterator.hasNext()) {
                            resultList.add(lhoIterator.next());
                        }
                    }
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForSequences.drop_whileBI.1
                boolean dropMode = true;
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() throws TemplateModelException {
                    ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                /* JADX WARN: Code restructure failed: missing block: B:13:0x003a, code lost:
                
                    r6.prefetchedElement = r0;
                    r7 = true;
                 */
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                private void ensurePrefetchDone() throws freemarker.template.TemplateModelException {
                    /*
                        r6 = this;
                        r0 = r6
                        boolean r0 = r0.prefetchDone
                        if (r0 == 0) goto L8
                        return
                    L8:
                        r0 = r6
                        boolean r0 = r0.dropMode
                        if (r0 == 0) goto L70
                        r0 = 0
                        r7 = r0
                    L11:
                        r0 = r6
                        freemarker.template.TemplateModelIterator r0 = r5
                        boolean r0 = r0.hasNext()
                        if (r0 == 0) goto L5a
                        r0 = r6
                        freemarker.template.TemplateModelIterator r0 = r5
                        freemarker.template.TemplateModel r0 = r0.next()
                        r8 = r0
                        r0 = r6
                        freemarker.core.BuiltInsForSequences$drop_whileBI r0 = freemarker.core.BuiltInsForSequences.drop_whileBI.this     // Catch: freemarker.template.TemplateException -> L47
                        r1 = r8
                        r2 = r6
                        freemarker.core.IntermediateStreamOperationLikeBuiltIn$ElementTransformer r2 = r6     // Catch: freemarker.template.TemplateException -> L47
                        r3 = r6
                        freemarker.core.Environment r3 = r7     // Catch: freemarker.template.TemplateException -> L47
                        boolean r0 = r0.elementMatches(r1, r2, r3)     // Catch: freemarker.template.TemplateException -> L47
                        if (r0 != 0) goto L44
                        r0 = r6
                        r1 = r8
                        r0.prefetchedElement = r1     // Catch: freemarker.template.TemplateException -> L47
                        r0 = 1
                        r7 = r0
                        goto L5a
                    L44:
                        goto L57
                    L47:
                        r9 = move-exception
                        freemarker.core._TemplateModelException r0 = new freemarker.core._TemplateModelException
                        r1 = r0
                        r2 = r9
                        r3 = r6
                        freemarker.core.Environment r3 = r7
                        java.lang.String r4 = "Failed to transform element"
                        r1.<init>(r2, r3, r4)
                        throw r0
                    L57:
                        goto L11
                    L5a:
                        r0 = r6
                        r1 = 0
                        r0.dropMode = r1
                        r0 = r7
                        if (r0 != 0) goto L6d
                        r0 = r6
                        r1 = 1
                        r0.prefetchedEndOfIterator = r1
                        r0 = r6
                        r1 = 0
                        r0.prefetchedElement = r1
                    L6d:
                        goto L98
                    L70:
                        r0 = r6
                        freemarker.template.TemplateModelIterator r0 = r5
                        boolean r0 = r0.hasNext()
                        if (r0 == 0) goto L8e
                        r0 = r6
                        freemarker.template.TemplateModelIterator r0 = r5
                        freemarker.template.TemplateModel r0 = r0.next()
                        r7 = r0
                        r0 = r6
                        r1 = r7
                        r0.prefetchedElement = r1
                        goto L98
                    L8e:
                        r0 = r6
                        r1 = 1
                        r0.prefetchedEndOfIterator = r1
                        r0 = r6
                        r1 = 0
                        r0.prefetchedElement = r1
                    L98:
                        r0 = r6
                        r1 = 1
                        r0.prefetchDone = r1
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: freemarker.core.BuiltInsForSequences.drop_whileBI.AnonymousClass1.ensurePrefetchDone():void");
                }
            }, lhoIsSequence);
        }
    }

    private BuiltInsForSequences() {
    }
}
