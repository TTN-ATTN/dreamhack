package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.13.5.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310DateTimeDeserializerBase.class */
public abstract class JSR310DateTimeDeserializerBase<T> extends JSR310DeserializerBase<T> implements ContextualDeserializer {
    protected final DateTimeFormatter _formatter;
    protected final JsonFormat.Shape _shape;

    protected abstract JSR310DateTimeDeserializerBase<T> withDateFormat(DateTimeFormatter dateTimeFormatter);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public abstract JSR310DateTimeDeserializerBase<T> withLeniency(Boolean bool);

    protected abstract JSR310DateTimeDeserializerBase<T> withShape(JsonFormat.Shape shape);

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
        return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ LogicalType logicalType() {
        return super.logicalType();
    }

    protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f) {
        super(supportedType);
        this._formatter = f;
        this._shape = null;
    }

    public JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f, Boolean leniency) {
        super(supportedType, leniency);
        this._formatter = f;
        this._shape = null;
    }

    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base, DateTimeFormatter f) {
        super(base);
        this._formatter = f;
        this._shape = base._shape;
    }

    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base, Boolean leniency) {
        super(base, leniency);
        this._formatter = base._formatter;
        this._shape = base._shape;
    }

    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base, JsonFormat.Shape shape) {
        super(base);
        this._formatter = base._formatter;
        this._shape = shape;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        return format == null ? this : _withFormatOverrides(ctxt, property, format);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt, BeanProperty property, JsonFormat.Value formatOverrides) {
        DateTimeFormatter df;
        Boolean leniency;
        JSR310DateTimeDeserializerBase jSR310DateTimeDeserializerBaseWithShape = this;
        if (formatOverrides.hasLenient() && (leniency = formatOverrides.getLenient()) != null) {
            jSR310DateTimeDeserializerBaseWithShape = jSR310DateTimeDeserializerBaseWithShape.withLeniency(leniency);
        }
        if (formatOverrides.hasPattern()) {
            String pattern = formatOverrides.getPattern();
            Locale locale = formatOverrides.hasLocale() ? formatOverrides.getLocale() : ctxt.getLocale();
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            if (acceptCaseInsensitiveValues(ctxt, formatOverrides)) {
                builder.parseCaseInsensitive();
            }
            builder.appendPattern(pattern);
            if (locale == null) {
                df = builder.toFormatter();
            } else {
                df = builder.toFormatter(locale);
            }
            if (!jSR310DateTimeDeserializerBaseWithShape.isLenient()) {
                df = df.withResolverStyle(ResolverStyle.STRICT);
            }
            if (formatOverrides.hasTimeZone()) {
                df = df.withZone(formatOverrides.getTimeZone().toZoneId());
            }
            jSR310DateTimeDeserializerBaseWithShape = jSR310DateTimeDeserializerBaseWithShape.withDateFormat(df);
        }
        JsonFormat.Shape shape = formatOverrides.getShape();
        if (shape != null && shape != this._shape) {
            jSR310DateTimeDeserializerBaseWithShape = jSR310DateTimeDeserializerBaseWithShape.withShape(shape);
        }
        return jSR310DateTimeDeserializerBaseWithShape;
    }

    private boolean acceptCaseInsensitiveValues(DeserializationContext ctxt, JsonFormat.Value format) {
        Boolean enabled = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        if (enabled == null) {
            enabled = Boolean.valueOf(ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES));
        }
        return enabled.booleanValue();
    }

    protected void _throwNoNumericTimestampNeedTimeZone(JsonParser p, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(handledType(), "raw timestamp (%d) not allowed for `%s`: need additional information such as an offset or time-zone (see class Javadocs)", p.getNumberValue(), handledType().getName());
    }
}
