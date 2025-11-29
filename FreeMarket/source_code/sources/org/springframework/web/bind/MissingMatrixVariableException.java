package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/MissingMatrixVariableException.class */
public class MissingMatrixVariableException extends MissingRequestValueException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
        this(variableName, parameter, false);
    }

    public MissingMatrixVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.variableName = variableName;
        this.parameter = parameter;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Required matrix variable '" + this.variableName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}
