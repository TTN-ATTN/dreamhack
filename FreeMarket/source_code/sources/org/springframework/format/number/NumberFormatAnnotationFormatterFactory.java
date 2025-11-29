package org.springframework.format.number;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/format/number/NumberFormatAnnotationFormatterFactory.class */
public class NumberFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<NumberFormat> {
    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(Annotation annotation, Class fieldType) {
        return getParser((NumberFormat) annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(Annotation annotation, Class fieldType) {
        return getPrinter((NumberFormat) annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public Set<Class<?>> getFieldTypes() {
        return NumberUtils.STANDARD_NUMBER_TYPES;
    }

    public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    private Formatter<Number> configureFormatterFrom(NumberFormat annotation) {
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            return new NumberStyleFormatter(pattern);
        }
        NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.CURRENCY) {
            return new CurrencyStyleFormatter();
        }
        if (style == NumberFormat.Style.PERCENT) {
            return new PercentStyleFormatter();
        }
        return new NumberStyleFormatter();
    }
}
