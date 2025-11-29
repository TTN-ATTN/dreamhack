package org.apache.el.parser;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/parser/AstLambdaParameters.class */
public class AstLambdaParameters extends SimpleNode {
    public AstLambdaParameters(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        if (this.children != null) {
            for (Node n : this.children) {
                result.append(n.toString());
                result.append(',');
            }
        }
        result.append(")->");
        return result.toString();
    }
}
