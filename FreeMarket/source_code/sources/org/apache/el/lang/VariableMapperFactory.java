package org.apache.el.lang;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.el.util.MessageFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/lang/VariableMapperFactory.class */
public class VariableMapperFactory extends VariableMapper {
    private final VariableMapper target;
    private VariableMapper momento;

    public VariableMapperFactory(VariableMapper target) {
        if (target == null) {
            throw new NullPointerException(MessageFactory.get("error.noVariableMapperTarget"));
        }
        this.target = target;
    }

    public VariableMapper create() {
        return this.momento;
    }

    @Override // javax.el.VariableMapper
    public ValueExpression resolveVariable(String variable) {
        ValueExpression expr = this.target.resolveVariable(variable);
        if (expr != null) {
            if (this.momento == null) {
                this.momento = new VariableMapperImpl();
            }
            this.momento.setVariable(variable, expr);
        }
        return expr;
    }

    @Override // javax.el.VariableMapper
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        throw new UnsupportedOperationException(MessageFactory.get("error.cannotSetVariables"));
    }
}
