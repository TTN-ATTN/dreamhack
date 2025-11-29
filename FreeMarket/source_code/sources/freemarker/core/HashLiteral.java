package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template._ObjectWrappers;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/HashLiteral.class */
final class HashLiteral extends Expression {
    private final List<? extends Expression> keys;
    private final List<? extends Expression> values;
    private final int size;

    HashLiteral(List<? extends Expression> keys, List<? extends Expression> values) {
        this.keys = keys;
        this.values = values;
        this.size = keys.size();
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return new SequenceHash(env);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder("{");
        for (int i = 0; i < this.size; i++) {
            Expression key = this.keys.get(i);
            Expression value = this.values.get(i);
            buf.append(key.getCanonicalForm());
            buf.append(": ");
            buf.append(value.getCanonicalForm());
            if (i != this.size - 1) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "{...}";
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        if (this.constantValue != null) {
            return true;
        }
        for (int i = 0; i < this.size; i++) {
            Expression key = this.keys.get(i);
            Expression value = this.values.get(i);
            if (!key.isLiteral() || !value.isLiteral()) {
                return false;
            }
        }
        return true;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        List<Expression> clonedKeys = new ArrayList<>(this.keys.size());
        for (Expression key : this.keys) {
            clonedKeys.add(key.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        List<Expression> clonedValues = new ArrayList<>(this.values.size());
        for (Expression value : this.values) {
            clonedValues.add(value.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        return new HashLiteral(clonedKeys, clonedValues);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/HashLiteral$SequenceHash.class */
    private class SequenceHash implements TemplateHashModelEx2 {
        private HashMap<String, TemplateModel> map;
        private TemplateCollectionModel keyCollection;
        private TemplateCollectionModel valueCollection;

        SequenceHash(Environment env) throws TemplateException {
            if (_TemplateAPI.getTemplateLanguageVersionAsInt(HashLiteral.this) >= _VersionInts.V_2_3_21) {
                this.map = new LinkedHashMap();
                for (int i = 0; i < HashLiteral.this.size; i++) {
                    Expression keyExp = (Expression) HashLiteral.this.keys.get(i);
                    Expression valExp = (Expression) HashLiteral.this.values.get(i);
                    String key = keyExp.evalAndCoerceToPlainText(env);
                    TemplateModel value = valExp.eval(env);
                    if (env == null || !env.isClassicCompatible()) {
                        valExp.assertNonNull(value, env);
                    }
                    this.map.put(key, value);
                }
                return;
            }
            this.map = new HashMap<>();
            SimpleSequence keyList = new SimpleSequence(HashLiteral.this.size, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
            SimpleSequence valueList = new SimpleSequence(HashLiteral.this.size, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
            for (int i2 = 0; i2 < HashLiteral.this.size; i2++) {
                Expression keyExp2 = (Expression) HashLiteral.this.keys.get(i2);
                Expression valExp2 = (Expression) HashLiteral.this.values.get(i2);
                String key2 = keyExp2.evalAndCoerceToPlainText(env);
                TemplateModel value2 = valExp2.eval(env);
                if (env == null || !env.isClassicCompatible()) {
                    valExp2.assertNonNull(value2, env);
                }
                this.map.put(key2, value2);
                keyList.add(key2);
                valueList.add(value2);
            }
            this.keyCollection = new CollectionAndSequence(keyList);
            this.valueCollection = new CollectionAndSequence(valueList);
        }

        @Override // freemarker.template.TemplateHashModelEx
        public int size() {
            return HashLiteral.this.size;
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel keys() {
            if (this.keyCollection == null) {
                this.keyCollection = new CollectionAndSequence(new SimpleSequence(this.map.keySet(), _ObjectWrappers.SAFE_OBJECT_WRAPPER));
            }
            return this.keyCollection;
        }

        @Override // freemarker.template.TemplateHashModelEx
        public TemplateCollectionModel values() {
            if (this.valueCollection == null) {
                this.valueCollection = new CollectionAndSequence(new SimpleSequence(this.map.values(), _ObjectWrappers.SAFE_OBJECT_WRAPPER));
            }
            return this.valueCollection;
        }

        @Override // freemarker.template.TemplateHashModel
        public TemplateModel get(String key) {
            return this.map.get(key);
        }

        @Override // freemarker.template.TemplateHashModel
        public boolean isEmpty() {
            return HashLiteral.this.size == 0;
        }

        public String toString() {
            return HashLiteral.this.getCanonicalForm();
        }

        @Override // freemarker.template.TemplateHashModelEx2
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
            return new TemplateHashModelEx2.KeyValuePairIterator() { // from class: freemarker.core.HashLiteral.SequenceHash.1
                private final TemplateModelIterator keyIterator;
                private final TemplateModelIterator valueIterator;

                {
                    this.keyIterator = SequenceHash.this.keys().iterator();
                    this.valueIterator = SequenceHash.this.values().iterator();
                }

                @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
                public boolean hasNext() throws TemplateModelException {
                    return this.keyIterator.hasNext();
                }

                @Override // freemarker.template.TemplateHashModelEx2.KeyValuePairIterator
                public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
                    return new TemplateHashModelEx2.KeyValuePair() { // from class: freemarker.core.HashLiteral.SequenceHash.1.1
                        private final TemplateModel key;
                        private final TemplateModel value;

                        {
                            this.key = AnonymousClass1.this.keyIterator.next();
                            this.value = AnonymousClass1.this.valueIterator.next();
                        }

                        @Override // freemarker.template.TemplateHashModelEx2.KeyValuePair
                        public TemplateModel getKey() {
                            return this.key;
                        }

                        @Override // freemarker.template.TemplateHashModelEx2.KeyValuePair
                        public TemplateModel getValue() {
                            return this.value;
                        }
                    };
                }
            };
        }
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return this.size * 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        checkIndex(idx);
        return idx % 2 == 0 ? this.keys.get(idx / 2) : this.values.get(idx / 2);
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        checkIndex(idx);
        return idx % 2 == 0 ? ParameterRole.ITEM_KEY : ParameterRole.ITEM_VALUE;
    }

    private void checkIndex(int idx) {
        if (idx >= this.size * 2) {
            throw new IndexOutOfBoundsException();
        }
    }
}
