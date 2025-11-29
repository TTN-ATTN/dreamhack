package org.springframework.expression.spel.ast;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.Constants;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/spel/ast/InlineList.class */
public class InlineList extends SpelNodeImpl {

    @Nullable
    private TypedValue constant;

    public InlineList(int startPos, int endPos, SpelNodeImpl... args) {
        super(startPos, endPos, args);
        checkIfConstant();
    }

    private void checkIfConstant() {
        boolean isConstant = true;
        int max = getChildCount();
        for (int c = 0; c < max; c++) {
            SpelNode child = getChild(c);
            if (!(child instanceof Literal)) {
                if (child instanceof InlineList) {
                    InlineList inlineList = (InlineList) child;
                    if (!inlineList.isConstant()) {
                        isConstant = false;
                    }
                } else {
                    isConstant = false;
                }
            }
        }
        if (isConstant) {
            List<Object> constantList = new ArrayList<>();
            int childcount = getChildCount();
            for (int c2 = 0; c2 < childcount; c2++) {
                SpelNode child2 = getChild(c2);
                if (child2 instanceof Literal) {
                    constantList.add(((Literal) child2).getLiteralValue().getValue());
                } else if (child2 instanceof InlineList) {
                    constantList.add(((InlineList) child2).getConstantValue());
                }
            }
            this.constant = new TypedValue(Collections.unmodifiableList(constantList));
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        if (this.constant != null) {
            return this.constant;
        }
        int childCount = getChildCount();
        List<Object> returnValue = new ArrayList<>(childCount);
        for (int c = 0; c < childCount; c++) {
            returnValue.add(getChild(c).getValue(expressionState));
        }
        return new TypedValue(returnValue);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringJoiner sj = new StringJoiner(",", "{", "}");
        int count = getChildCount();
        for (int c = 0; c < count; c++) {
            sj.add(getChild(c).toStringAST());
        }
        return sj.toString();
    }

    public boolean isConstant() {
        return this.constant != null;
    }

    @Nullable
    public List<Object> getConstantValue() {
        Assert.state(this.constant != null, "No constant");
        return (List) this.constant.getValue();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return isConstant();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow codeflow) {
        String constantFieldName = "inlineList$" + codeflow.nextFieldId();
        String className = codeflow.getClassName();
        codeflow.registerNewField((cw, cflow) -> {
            cw.visitField(26, constantFieldName, "Ljava/util/List;", null, null);
        });
        codeflow.registerNewClinit((mVisitor, cflow2) -> {
            generateClinitCode(className, constantFieldName, mVisitor, cflow2, false);
        });
        mv.visitFieldInsn(Opcodes.GETSTATIC, className, constantFieldName, "Ljava/util/List;");
        codeflow.pushDescriptor("Ljava/util/List");
    }

    void generateClinitCode(String clazzname, String constantFieldName, MethodVisitor mv, CodeFlow codeflow, boolean nested) {
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(89);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", Constants.CONSTRUCTOR_NAME, "()V", false);
        if (!nested) {
            mv.visitFieldInsn(Opcodes.PUTSTATIC, clazzname, constantFieldName, "Ljava/util/List;");
        }
        int childCount = getChildCount();
        for (int c = 0; c < childCount; c++) {
            if (!nested) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, clazzname, constantFieldName, "Ljava/util/List;");
            } else {
                mv.visitInsn(89);
            }
            if (this.children[c] instanceof InlineList) {
                ((InlineList) this.children[c]).generateClinitCode(clazzname, constantFieldName, mv, codeflow, true);
            } else {
                this.children[c].generateCode(mv, codeflow);
                String lastDesc = codeflow.lastDescriptor();
                if (CodeFlow.isPrimitive(lastDesc)) {
                    CodeFlow.insertBoxIfNecessary(mv, lastDesc.charAt(0));
                }
            }
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", BeanUtil.PREFIX_ADDER, "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(87);
        }
    }
}
