package freemarker.template.utility;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil.class */
public class DateUtil {
    public static final int ACCURACY_HOURS = 4;
    public static final int ACCURACY_MINUTES = 5;
    public static final int ACCURACY_SECONDS = 6;
    public static final int ACCURACY_MILLISECONDS = 7;
    public static final int ACCURACY_MILLISECONDS_FORCED = 8;
    private static final String REGEX_ISO8601_BASIC_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?:[0-9]{2})?)";
    private static final String REGEX_ISO8601_EXTENDED_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?::[0-9]{2})?)";
    private static final String REGEX_XS_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}:[0-9]{2}))?";
    private static final String REGEX_ISO8601_BASIC_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?";
    private static final String REGEX_ISO8601_EXTENDED_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?";
    private static final String REGEX_XS_DATE_BASE = "(-?[0-9]+)-([0-9]{2})-([0-9]{2})";
    private static final String REGEX_XS_TIME_BASE = "([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?";
    private static final String REGEX_ISO8601_BASIC_TIME_BASE = "([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
    private static final String REGEX_ISO8601_EXTENDED_TIME_BASE = "([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
    private static final String MSG_YEAR_0_NOT_ALLOWED = "Year 0 is not allowed in XML schema dates. BC 1 is -1, AD 1 is 1.";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Pattern PATTERN_XS_DATE = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final String REGEX_ISO8601_BASIC_DATE_BASE = "(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})";
    private static final Pattern PATTERN_ISO8601_BASIC_DATE = Pattern.compile(REGEX_ISO8601_BASIC_DATE_BASE);
    private static final String REGEX_ISO8601_EXTENDED_DATE_BASE = "(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})";
    private static final Pattern PATTERN_ISO8601_EXTENDED_DATE = Pattern.compile(REGEX_ISO8601_EXTENDED_DATE_BASE);
    private static final Pattern PATTERN_XS_TIME = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final Pattern PATTERN_ISO8601_BASIC_TIME = Pattern.compile("([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
    private static final Pattern PATTERN_ISO8601_EXTENDED_TIME = Pattern.compile("([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
    private static final Pattern PATTERN_XS_DATE_TIME = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final Pattern PATTERN_ISO8601_BASIC_DATE_TIME = Pattern.compile("(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})T([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
    private static final Pattern PATTERN_ISO8601_EXTENDED_DATE_TIME = Pattern.compile("(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})T([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
    private static final String REGEX_XS_TIME_ZONE = "Z|(?:[-+][0-9]{2}:[0-9]{2})";
    private static final Pattern PATTERN_XS_TIME_ZONE = Pattern.compile(REGEX_XS_TIME_ZONE);

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil$CalendarFieldsToDateConverter.class */
    public interface CalendarFieldsToDateConverter {
        Date calculate(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z, TimeZone timeZone);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil$DateToISO8601CalendarFactory.class */
    public interface DateToISO8601CalendarFactory {
        GregorianCalendar get(TimeZone timeZone, Date date);
    }

    private DateUtil() {
    }

    public static TimeZone getTimeZone(String name) throws UnrecognizedTimeZoneException {
        if (isGMTish(name)) {
            if (name.equalsIgnoreCase("UTC")) {
                return UTC;
            }
            return TimeZone.getTimeZone(name);
        }
        TimeZone tz = TimeZone.getTimeZone(name);
        if (isGMTish(tz.getID())) {
            throw new UnrecognizedTimeZoneException(name);
        }
        return tz;
    }

    private static boolean isGMTish(String name) {
        if (name.length() < 3) {
            return false;
        }
        char c1 = name.charAt(0);
        char c2 = name.charAt(1);
        char c3 = name.charAt(2);
        if (((c1 != 'G' && c1 != 'g') || ((c2 != 'M' && c2 != 'm') || (c3 != 'T' && c3 != 't'))) && ((c1 != 'U' && c1 != 'u') || ((c2 != 'T' && c2 != 't') || (c3 != 'C' && c3 != 'c')))) {
            if (c1 != 'U' && c1 != 'u') {
                return false;
            }
            if ((c2 != 'T' && c2 != 't') || c3 != '1') {
                return false;
            }
        }
        if (name.length() == 3) {
            return true;
        }
        String offset = name.substring(3);
        return offset.startsWith(Marker.ANY_NON_NULL_MARKER) ? offset.equals("+0") || offset.equals("+00") || offset.equals("+00:00") : offset.equals("-0") || offset.equals("-00") || offset.equals("-00:00");
    }

    public static String dateToISO8601String(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
        return dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, false, calendarFactory);
    }

    public static String dateToXSString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
        return dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, true, calendarFactory);
    }

    private static String dateToString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, boolean xsMode, DateToISO8601CalendarFactory calendarFactory) {
        int maxLength;
        boolean positive;
        if (!xsMode && !timePart && offsetPart) {
            throw new IllegalArgumentException("ISO 8601:2004 doesn't specify any formats where the offset is shown but the time isn't.");
        }
        if (timeZone == null) {
            timeZone = UTC;
        }
        GregorianCalendar cal = calendarFactory.get(timeZone, date);
        if (!timePart) {
            maxLength = 10 + (xsMode ? 6 : 0);
        } else if (!datePart) {
            maxLength = 18;
        } else {
            maxLength = 29;
        }
        char[] res = new char[maxLength];
        int dstIdx = 0;
        if (datePart) {
            int x = cal.get(1);
            if (x > 0 && cal.get(0) == 0) {
                x = (-x) + (xsMode ? 0 : 1);
            }
            if (x >= 0 && x < 9999) {
                int dstIdx2 = 0 + 1;
                res[0] = (char) (48 + (x / 1000));
                int dstIdx3 = dstIdx2 + 1;
                res[dstIdx2] = (char) (48 + ((x % 1000) / 100));
                int dstIdx4 = dstIdx3 + 1;
                res[dstIdx3] = (char) (48 + ((x % 100) / 10));
                dstIdx = dstIdx4 + 1;
                res[dstIdx4] = (char) (48 + (x % 10));
            } else {
                String yearString = String.valueOf(x);
                res = new char[(maxLength - 4) + yearString.length()];
                for (int i = 0; i < yearString.length(); i++) {
                    int i2 = dstIdx;
                    dstIdx++;
                    res[i2] = yearString.charAt(i);
                }
            }
            res[dstIdx] = '-';
            int x2 = cal.get(2) + 1;
            int dstIdx5 = append00(res, dstIdx + 1, x2);
            int dstIdx6 = dstIdx5 + 1;
            res[dstIdx5] = '-';
            int x3 = cal.get(5);
            dstIdx = append00(res, dstIdx6, x3);
            if (timePart) {
                dstIdx++;
                res[dstIdx] = 'T';
            }
        }
        if (timePart) {
            int x4 = cal.get(11);
            dstIdx = append00(res, dstIdx, x4);
            if (accuracy >= 5) {
                res[dstIdx] = ':';
                int x5 = cal.get(12);
                dstIdx = append00(res, dstIdx + 1, x5);
                if (accuracy >= 6) {
                    res[dstIdx] = ':';
                    int x6 = cal.get(13);
                    dstIdx = append00(res, dstIdx + 1, x6);
                    if (accuracy >= 7) {
                        int x7 = cal.get(14);
                        int forcedDigits = accuracy == 8 ? 3 : 0;
                        if (x7 != 0 || forcedDigits != 0) {
                            if (x7 > 999) {
                                throw new RuntimeException("Calendar.MILLISECOND > 999");
                            }
                            dstIdx++;
                            res[dstIdx] = '.';
                            while (true) {
                                int i3 = dstIdx;
                                dstIdx++;
                                res[i3] = (char) (48 + (x7 / 100));
                                forcedDigits--;
                                x7 = (x7 % 100) * 10;
                                if (x7 == 0 && forcedDigits <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (offsetPart) {
            if (timeZone == UTC) {
                int i4 = dstIdx;
                dstIdx++;
                res[i4] = 'Z';
            } else {
                int dt = timeZone.getOffset(date.getTime());
                if (dt < 0) {
                    positive = false;
                    dt = -dt;
                } else {
                    positive = true;
                }
                int dt2 = dt / 1000;
                int offS = dt2 % 60;
                int dt3 = dt2 / 60;
                int offM = dt3 % 60;
                int dt4 = dt3 / 60;
                if (offS == 0 && offM == 0 && dt4 == 0) {
                    int i5 = dstIdx;
                    dstIdx++;
                    res[i5] = 'Z';
                } else {
                    int i6 = dstIdx;
                    int dstIdx7 = dstIdx + 1;
                    res[i6] = positive ? '+' : '-';
                    int dstIdx8 = append00(res, dstIdx7, dt4);
                    res[dstIdx8] = ':';
                    dstIdx = append00(res, dstIdx8 + 1, offM);
                    if (offS != 0) {
                        res[dstIdx] = ':';
                        dstIdx = append00(res, dstIdx + 1, offS);
                    }
                }
            }
        }
        return new String(res, 0, dstIdx);
    }

    private static int append00(char[] res, int dstIdx, int x) {
        int dstIdx2 = dstIdx + 1;
        res[dstIdx] = (char) (48 + (x / 10));
        int dstIdx3 = dstIdx2 + 1;
        res[dstIdx2] = (char) (48 + (x % 10));
        return dstIdx3;
    }

    public static Date parseXSDate(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_DATE.matcher(dateStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE);
        }
        return parseDate_parseMatcher(m, defaultTimeZone, true, calToDateConverter);
    }

    public static Date parseISO8601Date(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_DATE.matcher(dateStr);
        if (!m.matches()) {
            m = PATTERN_ISO8601_BASIC_DATE.matcher(dateStr);
            if (!m.matches()) {
                throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE + " or " + PATTERN_ISO8601_BASIC_DATE);
            }
        }
        return parseDate_parseMatcher(m, defaultTimeZone, false, calToDateConverter);
    }

    private static Date parseDate_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        int era;
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            int year = groupToInt(m.group(1), "year", Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (year <= 0) {
                era = 0;
                year = (-year) + (xsMode ? 0 : 1);
                if (year == 0) {
                    throw new DateParseException(MSG_YEAR_0_NOT_ALLOWED);
                }
            } else {
                era = 1;
            }
            int month = groupToInt(m.group(2), "month", 1, 12) - 1;
            int day = groupToInt(m.group(3), "day-of-month", 1, 31);
            TimeZone tz = xsMode ? parseMatchingTimeZone(m.group(4), defaultTZ) : defaultTZ;
            return calToDateConverter.calculate(era, year, month, day, 0, 0, 0, 0, false, tz);
        } catch (IllegalArgumentException e) {
            throw new DateParseException("Date calculation faliure. Probably the date is formally correct, but refers to an unexistent date (like February 30).");
        }
    }

    public static Date parseXSTime(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_TIME.matcher(timeStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_TIME);
        }
        return parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
    }

    public static Date parseISO8601Time(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_TIME.matcher(timeStr);
        if (!m.matches()) {
            m = PATTERN_ISO8601_BASIC_TIME.matcher(timeStr);
            if (!m.matches()) {
                throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_TIME + " or " + PATTERN_ISO8601_BASIC_TIME);
            }
        }
        return parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
    }

    private static Date parseTime_parseMatcher(Matcher m, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        boolean hourWas24;
        int day;
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            int hours = groupToInt(m.group(1), "hour-of-day", 0, 24);
            if (hours == 24) {
                hours = 0;
                hourWas24 = true;
            } else {
                hourWas24 = false;
            }
            String minutesStr = m.group(2);
            int minutes = minutesStr != null ? groupToInt(minutesStr, "minute", 0, 59) : 0;
            String secsStr = m.group(3);
            int secs = secsStr != null ? groupToInt(secsStr, "second", 0, 60) : 0;
            int millisecs = groupToMillisecond(m.group(4));
            TimeZone tz = parseMatchingTimeZone(m.group(5), defaultTZ);
            if (hourWas24) {
                if (minutes == 0 && secs == 0 && millisecs == 0) {
                    day = 2;
                } else {
                    throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
                }
            } else {
                day = 1;
            }
            return calToDateConverter.calculate(1, 1970, 0, day, hours, minutes, secs, millisecs, false, tz);
        } catch (IllegalArgumentException e) {
            throw new DateParseException("Unexpected time calculation faliure.");
        }
    }

    public static Date parseXSDateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_DATE_TIME.matcher(dateTimeStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE_TIME);
        }
        return parseDateTime_parseMatcher(m, defaultTZ, true, calToDateConverter);
    }

    public static Date parseISO8601DateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_DATE_TIME.matcher(dateTimeStr);
        if (!m.matches()) {
            m = PATTERN_ISO8601_BASIC_DATE_TIME.matcher(dateTimeStr);
            if (!m.matches()) {
                throw new DateParseException("The value (" + dateTimeStr + ") didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE_TIME + " or " + PATTERN_ISO8601_BASIC_DATE_TIME);
            }
        }
        return parseDateTime_parseMatcher(m, defaultTZ, false, calToDateConverter);
    }

    private static Date parseDateTime_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        int era;
        boolean hourWas24;
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            int year = groupToInt(m.group(1), "year", Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (year <= 0) {
                era = 0;
                year = (-year) + (xsMode ? 0 : 1);
                if (year == 0) {
                    throw new DateParseException(MSG_YEAR_0_NOT_ALLOWED);
                }
            } else {
                era = 1;
            }
            int month = groupToInt(m.group(2), "month", 1, 12) - 1;
            int day = groupToInt(m.group(3), "day-of-month", 1, 31);
            int hours = groupToInt(m.group(4), "hour-of-day", 0, 24);
            if (hours == 24) {
                hours = 0;
                hourWas24 = true;
            } else {
                hourWas24 = false;
            }
            String minutesStr = m.group(5);
            int minutes = minutesStr != null ? groupToInt(minutesStr, "minute", 0, 59) : 0;
            String secsStr = m.group(6);
            int secs = secsStr != null ? groupToInt(secsStr, "second", 0, 60) : 0;
            int millisecs = groupToMillisecond(m.group(7));
            TimeZone tz = parseMatchingTimeZone(m.group(8), defaultTZ);
            if (hourWas24 && (minutes != 0 || secs != 0 || millisecs != 0)) {
                throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
            }
            return calToDateConverter.calculate(era, year, month, day, hours, minutes, secs, millisecs, hourWas24, tz);
        } catch (IllegalArgumentException e) {
            throw new DateParseException("Date-time calculation faliure. Probably the date-time is formally correct, but refers to an unexistent date-time (like February 30).");
        }
    }

    public static TimeZone parseXSTimeZone(String timeZoneStr) throws DateParseException {
        Matcher m = PATTERN_XS_TIME_ZONE.matcher(timeZoneStr);
        if (!m.matches()) {
            throw new DateParseException("The time zone offset didn't match the expected pattern: " + PATTERN_XS_TIME_ZONE);
        }
        return parseMatchingTimeZone(timeZoneStr, null);
    }

    private static int groupToInt(String g, String gName, int min, int max) throws NumberFormatException, DateParseException {
        boolean negative;
        int start;
        if (g == null) {
            throw new DateParseException("The " + gName + " part is missing.");
        }
        if (g.startsWith("-")) {
            negative = true;
            start = 1;
        } else {
            negative = false;
            start = 0;
        }
        while (start < g.length() - 1 && g.charAt(start) == '0') {
            start++;
        }
        if (start != 0) {
            g = g.substring(start);
        }
        try {
            int r = Integer.parseInt(g);
            if (negative) {
                r = -r;
            }
            if (r < min) {
                throw new DateParseException("The " + gName + " part must be at least " + min + ".");
            }
            if (r > max) {
                throw new DateParseException("The " + gName + " part can't be more than " + max + ".");
            }
            return r;
        } catch (NumberFormatException e) {
            throw new DateParseException("The " + gName + " part is a malformed integer.");
        }
    }

    private static TimeZone parseMatchingTimeZone(String s, TimeZone defaultZone) throws NumberFormatException, DateParseException {
        if (s == null) {
            return defaultZone;
        }
        if (s.equals("Z")) {
            return UTC;
        }
        StringBuilder sb = new StringBuilder(9);
        sb.append("GMT");
        sb.append(s.charAt(0));
        String h = s.substring(1, 3);
        groupToInt(h, "offset-hours", 0, 23);
        sb.append(h);
        int ln = s.length();
        if (ln > 3) {
            int startIdx = s.charAt(3) == ':' ? 4 : 3;
            String m = s.substring(startIdx, startIdx + 2);
            groupToInt(m, "offset-minutes", 0, 59);
            sb.append(':');
            sb.append(m);
        }
        return TimeZone.getTimeZone(sb.toString());
    }

    private static int groupToMillisecond(String g) throws NumberFormatException, DateParseException {
        if (g == null) {
            return 0;
        }
        if (g.length() > 3) {
            g = g.substring(0, 3);
        }
        int i = groupToInt(g, "partial-seconds", 0, Integer.MAX_VALUE);
        return g.length() == 1 ? i * 100 : g.length() == 2 ? i * 10 : i;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil$TrivialDateToISO8601CalendarFactory.class */
    public static final class TrivialDateToISO8601CalendarFactory implements DateToISO8601CalendarFactory {
        private GregorianCalendar calendar;
        private TimeZone lastlySetTimeZone;

        @Override // freemarker.template.utility.DateUtil.DateToISO8601CalendarFactory
        public GregorianCalendar get(TimeZone tz, Date date) {
            if (this.calendar == null) {
                this.calendar = new GregorianCalendar(tz, Locale.US);
                this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
            } else if (this.lastlySetTimeZone != tz) {
                this.calendar.setTimeZone(tz);
                this.lastlySetTimeZone = tz;
            }
            this.calendar.setTime(date);
            return this.calendar;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil$TrivialCalendarFieldsToDateConverter.class */
    public static final class TrivialCalendarFieldsToDateConverter implements CalendarFieldsToDateConverter {
        private GregorianCalendar calendar;
        private TimeZone lastlySetTimeZone;

        @Override // freemarker.template.utility.DateUtil.CalendarFieldsToDateConverter
        public Date calculate(int era, int year, int month, int day, int hours, int minutes, int secs, int millisecs, boolean addOneDay, TimeZone tz) {
            if (this.calendar == null) {
                this.calendar = new GregorianCalendar(tz, Locale.US);
                this.calendar.setLenient(false);
                this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
            } else if (this.lastlySetTimeZone != tz) {
                this.calendar.setTimeZone(tz);
                this.lastlySetTimeZone = tz;
            }
            this.calendar.set(0, era);
            this.calendar.set(1, year);
            this.calendar.set(2, month);
            this.calendar.set(5, day);
            this.calendar.set(11, hours);
            this.calendar.set(12, minutes);
            this.calendar.set(13, secs);
            this.calendar.set(14, millisecs);
            if (addOneDay) {
                this.calendar.add(5, 1);
            }
            return this.calendar.getTime();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/DateUtil$DateParseException.class */
    public static final class DateParseException extends ParseException {
        public DateParseException(String message) {
            super(message, 0);
        }
    }
}
