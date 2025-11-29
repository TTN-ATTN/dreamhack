package ch.qos.logback.core.util;

import freemarker.core.FMParserConstants;
import java.text.DateFormatSymbols;
import org.springframework.asm.Opcodes;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/util/CharSequenceToRegexMapper.class */
class CharSequenceToRegexMapper {
    DateFormatSymbols symbols = DateFormatSymbols.getInstance();

    CharSequenceToRegexMapper() {
    }

    String toRegex(CharSequenceState css) {
        int occurrences = css.occurrences;
        char c = css.c;
        switch (css.c) {
            case '\'':
                if (occurrences == 1) {
                    return "";
                }
                throw new IllegalStateException("Too many single quotes");
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'I':
            case 'J':
            case 'L':
            case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case 'X':
            case Opcodes.DUP /* 89 */:
            case '[':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'c':
            case 'e':
            case 'f':
            case 'g':
            case 'i':
            case 'j':
            case 'l':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 't':
            case 'u':
            case 'v':
            case 'x':
            default:
                if (occurrences == 1) {
                    return "" + c;
                }
                return c + "{" + occurrences + "}";
            case '.':
                return "\\.";
            case 'D':
            case 'F':
            case 'H':
            case 'K':
            case 'S':
            case Opcodes.POP /* 87 */:
            case 'd':
            case 'h':
            case 'k':
            case 'm':
            case 's':
            case 'w':
            case 'y':
                return number(occurrences);
            case 'E':
                if (occurrences >= 4) {
                    return getRegexForLongDaysOfTheWeek();
                }
                return getRegexForShortDaysOfTheWeek();
            case 'G':
            case 'z':
                return ".*";
            case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
                if (occurrences <= 2) {
                    return number(occurrences);
                }
                if (occurrences == 3) {
                    return getRegexForShortMonths();
                }
                return getRegexForLongMonths();
            case 'Z':
                return "(\\+|-)\\d{4}";
            case '\\':
                throw new IllegalStateException("Forward slashes are not allowed");
            case 'a':
                return getRegexForAmPms();
        }
    }

    private String number(int occurrences) {
        return "\\d{" + occurrences + "}";
    }

    private String getRegexForAmPms() {
        return symbolArrayToRegex(this.symbols.getAmPmStrings());
    }

    private String getRegexForLongDaysOfTheWeek() {
        return symbolArrayToRegex(this.symbols.getWeekdays());
    }

    private String getRegexForShortDaysOfTheWeek() {
        return symbolArrayToRegex(this.symbols.getShortWeekdays());
    }

    private String getRegexForLongMonths() {
        return symbolArrayToRegex(this.symbols.getMonths());
    }

    String getRegexForShortMonths() {
        return symbolArrayToRegex(this.symbols.getShortMonths());
    }

    private String symbolArrayToRegex(String[] symbolArray) {
        int[] minMax = findMinMaxLengthsInSymbols(symbolArray);
        return ".{" + minMax[0] + "," + minMax[1] + "}";
    }

    static int[] findMinMaxLengthsInSymbols(String[] symbols) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (String symbol : symbols) {
            int len = symbol.length();
            if (len != 0) {
                min = Math.min(min, len);
                max = Math.max(max, len);
            }
        }
        return new int[]{min, max};
    }
}
