package org.apache.el.lang;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/lang/LambdaExpressionNestedState.class */
public final class LambdaExpressionNestedState {
    private int nestingCount = 0;
    private boolean hasFormalParameters = false;

    public void incrementNestingCount() {
        this.nestingCount++;
    }

    public int getNestingCount() {
        return this.nestingCount;
    }

    public void setHasFormalParameters() {
        this.hasFormalParameters = true;
    }

    public boolean getHasFormalParameters() {
        return this.hasFormalParameters;
    }
}
