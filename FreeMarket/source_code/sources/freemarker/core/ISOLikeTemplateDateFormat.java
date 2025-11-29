package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DateUtil;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ISOLikeTemplateDateFormat.class */
abstract class ISOLikeTemplateDateFormat extends TemplateDateFormat {
    private static final String XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE = "Less than seconds accuracy isn't allowed by the XML Schema format";
    private final ISOLikeTemplateDateFormatFactory factory;
    private final Environment env;
    protected final int dateType;
    protected final boolean zonelessInput;
    protected final TimeZone timeZone;
    protected final Boolean forceUTC;
    protected final Boolean showZoneOffset;
    protected final int accuracy;

    protected abstract String format(Date date, boolean z, boolean z2, boolean z3, int i, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory dateToISO8601CalendarFactory);

    protected abstract Date parseDate(String str, TimeZone timeZone, DateUtil.CalendarFieldsToDateConverter calendarFieldsToDateConverter) throws DateUtil.DateParseException;

    protected abstract Date parseTime(String str, TimeZone timeZone, DateUtil.CalendarFieldsToDateConverter calendarFieldsToDateConverter) throws DateUtil.DateParseException;

    protected abstract Date parseDateTime(String str, TimeZone timeZone, DateUtil.CalendarFieldsToDateConverter calendarFieldsToDateConverter) throws DateUtil.DateParseException;

    protected abstract String getDateDescription();

    protected abstract String getTimeDescription();

    protected abstract String getDateTimeDescription();

    protected abstract boolean isXSMode();

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:48:0x01b0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ISOLikeTemplateDateFormat(java.lang.String r6, int r7, int r8, boolean r9, java.util.TimeZone r10, freemarker.core.ISOLikeTemplateDateFormatFactory r11, freemarker.core.Environment r12) throws freemarker.core.UnknownDateTypeFormattingUnsupportedException, freemarker.core.InvalidFormatParametersException {
        /*
            Method dump skipped, instructions count: 727
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.ISOLikeTemplateDateFormat.<init>(java.lang.String, int, int, boolean, java.util.TimeZone, freemarker.core.ISOLikeTemplateDateFormatFactory, freemarker.core.Environment):void");
    }

    private void checkForceUTCNotSet(Boolean fourceUTC) throws InvalidFormatParametersException {
        if (fourceUTC != Boolean.FALSE) {
            throw new InvalidFormatParametersException("The UTC usage option was already set earlier.");
        }
    }

    @Override // freemarker.core.TemplateDateFormat
    public final String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
        boolean zBooleanValue;
        Date date = TemplateFormatUtil.getNonNullDate(dateModel);
        boolean z = this.dateType != 1;
        boolean z2 = this.dateType != 2;
        if (this.showZoneOffset == null) {
            zBooleanValue = !this.zonelessInput;
        } else {
            zBooleanValue = this.showZoneOffset.booleanValue();
        }
        return format(date, z, z2, zBooleanValue, this.accuracy, (this.forceUTC != null ? !this.forceUTC.booleanValue() : this.zonelessInput) ? this.timeZone : DateUtil.UTC, this.factory.getISOBuiltInCalendar(this.env));
    }

    @Override // freemarker.core.TemplateDateFormat
    @SuppressFBWarnings(value = {"RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN"}, justification = "Known to use the singleton Boolean-s only")
    public final Date parse(String s, int dateType) throws UnparsableValueException {
        DateUtil.CalendarFieldsToDateConverter calToDateConverter = this.factory.getCalendarFieldsToDateCalculator(this.env);
        TimeZone tz = this.forceUTC != Boolean.FALSE ? DateUtil.UTC : this.timeZone;
        try {
            if (dateType == 2) {
                return parseDate(s, tz, calToDateConverter);
            }
            if (dateType == 1) {
                return parseTime(s, tz, calToDateConverter);
            }
            if (dateType == 3) {
                return parseDateTime(s, tz, calToDateConverter);
            }
            throw new BugException("Unexpected date type: " + dateType);
        } catch (DateUtil.DateParseException e) {
            throw new UnparsableValueException(e.getMessage(), e);
        }
    }

    @Override // freemarker.core.TemplateValueFormat
    public final String getDescription() {
        switch (this.dateType) {
            case 1:
                return getTimeDescription();
            case 2:
                return getDateDescription();
            case 3:
                return getDateTimeDescription();
            default:
                return "<error: wrong format dateType>";
        }
    }

    @Override // freemarker.core.TemplateDateFormat
    public final boolean isLocaleBound() {
        return false;
    }

    @Override // freemarker.core.TemplateDateFormat
    public boolean isTimeZoneBound() {
        return true;
    }
}
