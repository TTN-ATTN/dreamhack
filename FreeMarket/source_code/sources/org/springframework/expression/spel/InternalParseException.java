package org.springframework.expression.spel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/spel/InternalParseException.class */
public class InternalParseException extends RuntimeException {
    public InternalParseException(SpelParseException cause) {
        super(cause);
    }

    @Override // java.lang.Throwable
    public SpelParseException getCause() {
        return (SpelParseException) super.getCause();
    }
}
