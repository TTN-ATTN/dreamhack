package org.springframework.expression.spel.ast;

import java.util.StringJoiner;
import java.util.function.Supplier;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/spel/ast/CompoundExpression.class */
public class CompoundExpression extends SpelNodeImpl {
    public CompoundExpression(int startPos, int endPos, SpelNodeImpl... expressionComponents) {
        super(startPos, endPos, expressionComponents);
        if (expressionComponents.length < 2) {
            throw new IllegalStateException("Do not build compound expressions with less than two entries: " + expressionComponents.length);
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        if (getChildCount() == 1) {
            return this.children[0].getValueRef(state);
        }
        SpelNodeImpl nextNode = this.children[0];
        try {
            TypedValue result = nextNode.getValueInternal(state);
            int cc = getChildCount();
            for (int i = 1; i < cc - 1; i++) {
                try {
                    state.pushActiveContextObject(result);
                    nextNode = this.children[i];
                    result = nextNode.getValueInternal(state);
                    state.popActiveContextObject();
                } finally {
                }
            }
            try {
                state.pushActiveContextObject(result);
                nextNode = this.children[cc - 1];
                ValueRef valueRef = nextNode.getValueRef(state);
                state.popActiveContextObject();
                return valueRef;
            } finally {
            }
        } catch (SpelEvaluationException ex) {
            ex.setPosition(nextNode.getStartPosition());
            throw ex;
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        ValueRef ref = getValueRef(state);
        TypedValue result = ref.getValue();
        this.exitTypeDescriptor = this.children[this.children.length - 1].exitTypeDescriptor;
        return result;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue setValueInternal(ExpressionState state, Supplier<TypedValue> valueSupplier) throws EvaluationException {
        TypedValue typedValue = valueSupplier.get();
        getValueRef(state).setValue(typedValue.getValue());
        return typedValue;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState state) throws EvaluationException {
        return getValueRef(state).isWritable();
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringJoiner sj = new StringJoiner(".");
        for (int i = 0; i < getChildCount(); i++) {
            sj.add(getChild(i).toStringAST());
        }
        return sj.toString();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        for (SpelNodeImpl child : this.children) {
            if (!child.isCompilable()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        for (SpelNodeImpl child : this.children) {
            child.generateCode(mv, cf);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
