package ch.qos.logback.core.pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/pattern/LiteralConverter.class */
public final class LiteralConverter<E> extends Converter<E> {
    String literal;

    public LiteralConverter(String literal) {
        this.literal = literal;
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(E o) {
        return this.literal;
    }
}
