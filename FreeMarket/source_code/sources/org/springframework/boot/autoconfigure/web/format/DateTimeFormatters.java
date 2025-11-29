package org.springframework.boot.autoconfigure.web.format;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/format/DateTimeFormatters.class */
public class DateTimeFormatters {
    private DateTimeFormatter dateFormatter;
    private String datePattern;
    private DateTimeFormatter timeFormatter;
    private DateTimeFormatter dateTimeFormatter;

    public DateTimeFormatters dateFormat(String pattern) {
        if (isIso(pattern)) {
            this.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            this.datePattern = "yyyy-MM-dd";
        } else {
            this.dateFormatter = formatter(pattern);
            this.datePattern = pattern;
        }
        return this;
    }

    public DateTimeFormatters timeFormat(String pattern) {
        DateTimeFormatter dateTimeFormatter;
        if (isIso(pattern)) {
            dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        } else {
            dateTimeFormatter = isIsoOffset(pattern) ? DateTimeFormatter.ISO_OFFSET_TIME : formatter(pattern);
        }
        this.timeFormatter = dateTimeFormatter;
        return this;
    }

    public DateTimeFormatters dateTimeFormat(String pattern) {
        DateTimeFormatter dateTimeFormatter;
        if (isIso(pattern)) {
            dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        } else {
            dateTimeFormatter = isIsoOffset(pattern) ? DateTimeFormatter.ISO_OFFSET_DATE_TIME : formatter(pattern);
        }
        this.dateTimeFormatter = dateTimeFormatter;
        return this;
    }

    DateTimeFormatter getDateFormatter() {
        return this.dateFormatter;
    }

    String getDatePattern() {
        return this.datePattern;
    }

    DateTimeFormatter getTimeFormatter() {
        return this.timeFormatter;
    }

    DateTimeFormatter getDateTimeFormatter() {
        return this.dateTimeFormatter;
    }

    boolean isCustomized() {
        return (this.dateFormatter == null && this.timeFormatter == null && this.dateTimeFormatter == null) ? false : true;
    }

    private static DateTimeFormatter formatter(String pattern) {
        if (StringUtils.hasText(pattern)) {
            return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.SMART);
        }
        return null;
    }

    private static boolean isIso(String pattern) {
        return "iso".equalsIgnoreCase(pattern);
    }

    private static boolean isIsoOffset(String pattern) {
        return "isooffset".equalsIgnoreCase(pattern) || "iso-offset".equalsIgnoreCase(pattern);
    }
}
