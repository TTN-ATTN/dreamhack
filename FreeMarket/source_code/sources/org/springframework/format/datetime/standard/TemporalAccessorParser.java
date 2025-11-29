package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.springframework.format.Parser;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/format/datetime/standard/TemporalAccessorParser.class */
public final class TemporalAccessorParser implements Parser<TemporalAccessor> {
    private final Class<? extends TemporalAccessor> temporalAccessorType;
    private final DateTimeFormatter formatter;

    @Nullable
    private final String[] fallbackPatterns;

    @Nullable
    private final Object source;

    public TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter) {
        this(temporalAccessorType, formatter, null, null);
    }

    TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter, @Nullable String[] fallbackPatterns, @Nullable Object source) {
        this.temporalAccessorType = temporalAccessorType;
        this.formatter = formatter;
        this.fallbackPatterns = fallbackPatterns;
        this.source = source;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.format.Parser
    public TemporalAccessor parse(String text, Locale locale) throws ParseException {
        try {
            return doParse(text, locale, this.formatter);
        } catch (DateTimeParseException ex) {
            if (!ObjectUtils.isEmpty((Object[]) this.fallbackPatterns)) {
                for (String pattern : this.fallbackPatterns) {
                    try {
                        DateTimeFormatter fallbackFormatter = DateTimeFormatterUtils.createStrictDateTimeFormatter(pattern);
                        return doParse(text, locale, fallbackFormatter);
                    } catch (DateTimeParseException e) {
                    }
                }
            }
            if (this.source != null) {
                throw new DateTimeParseException(String.format("Unable to parse date time value \"%s\" using configuration from %s", text, this.source), text, ex.getErrorIndex(), ex);
            }
            throw ex;
        }
    }

    private TemporalAccessor doParse(String text, Locale locale, DateTimeFormatter formatter) throws DateTimeParseException {
        DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(formatter, locale);
        if (LocalDate.class == this.temporalAccessorType) {
            return LocalDate.parse(text, formatterToUse);
        }
        if (LocalTime.class == this.temporalAccessorType) {
            return LocalTime.parse(text, formatterToUse);
        }
        if (LocalDateTime.class == this.temporalAccessorType) {
            return LocalDateTime.parse(text, formatterToUse);
        }
        if (ZonedDateTime.class == this.temporalAccessorType) {
            return ZonedDateTime.parse(text, formatterToUse);
        }
        if (OffsetDateTime.class == this.temporalAccessorType) {
            return OffsetDateTime.parse(text, formatterToUse);
        }
        if (OffsetTime.class == this.temporalAccessorType) {
            return OffsetTime.parse(text, formatterToUse);
        }
        throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
    }
}
