package org.apache.el.parser;

import java.math.BigInteger;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/parser/AstInteger.class */
public final class AstInteger extends SimpleNode {
    private volatile Number number;

    public AstInteger(int id) {
        super(id);
    }

    protected Number getInteger() {
        if (this.number == null) {
            try {
                try {
                    this.number = Long.valueOf(this.image);
                } catch (NumberFormatException e) {
                    this.number = new BigInteger(this.image);
                }
            } catch (ArithmeticException | NumberFormatException e2) {
                throw new ELException(e2);
            }
        }
        return this.number;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return getInteger().getClass();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getInteger();
    }
}
