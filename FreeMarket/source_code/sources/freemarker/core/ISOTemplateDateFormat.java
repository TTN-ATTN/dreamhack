package freemarker.core;

import freemarker.template.utility.DateUtil;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ISOTemplateDateFormat.class */
final class ISOTemplateDateFormat extends ISOLikeTemplateDateFormat {
    ISOTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
        return DateUtil.dateToISO8601String(date, datePart, timePart, timePart && offsetPart, accuracy, timeZone, calendarFactory);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseISO8601Date(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseISO8601Time(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseISO8601DateTime(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getDateDescription() {
        return "ISO 8601 (subset) date";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getTimeDescription() {
        return "ISO 8601 (subset) time";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getDateTimeDescription() {
        return "ISO 8601 (subset) date-time";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected boolean isXSMode() {
        return false;
    }
}
