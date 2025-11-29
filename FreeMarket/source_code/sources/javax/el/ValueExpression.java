package javax.el;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/ValueExpression.class */
public abstract class ValueExpression extends Expression {
    private static final long serialVersionUID = 8577809572381654673L;

    public abstract Object getValue(ELContext eLContext);

    public abstract void setValue(ELContext eLContext, Object obj);

    public abstract boolean isReadOnly(ELContext eLContext);

    public abstract Class<?> getType(ELContext eLContext);

    public abstract Class<?> getExpectedType();

    public ValueReference getValueReference(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return null;
    }
}
