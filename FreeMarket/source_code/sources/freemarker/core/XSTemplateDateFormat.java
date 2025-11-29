package freemarker.core;

import freemarker.template.utility.DateUtil;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/XSTemplateDateFormat.class */
final class XSTemplateDateFormat extends ISOLikeTemplateDateFormat {
    XSTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
        return DateUtil.dateToXSString(date, datePart, timePart, offsetPart, accuracy, timeZone, calendarFactory);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSDate(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSTime(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
        return DateUtil.parseXSDateTime(s, tz, calToDateConverter);
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getDateDescription() {
        return "W3C XML Schema date";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getTimeDescription() {
        return "W3C XML Schema time";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected String getDateTimeDescription() {
        return "W3C XML Schema dateTime";
    }

    @Override // freemarker.core.ISOLikeTemplateDateFormat
    protected boolean isXSMode() {
        return true;
    }
}
