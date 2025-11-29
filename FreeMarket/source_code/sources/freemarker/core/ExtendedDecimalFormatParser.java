package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ExtendedDecimalFormatParser.class */
class ExtendedDecimalFormatParser {
    private static final String PARAM_ROUNDING_MODE = "roundingMode";
    private static final String PARAM_MULTIPIER = "multipier";
    private static final String PARAM_MULTIPLIER = "multiplier";
    private static final String PARAM_DECIMAL_SEPARATOR = "decimalSeparator";
    private static final String PARAM_MONETARY_DECIMAL_SEPARATOR = "monetaryDecimalSeparator";
    private static final String PARAM_GROUP_SEPARATOR = "groupingSeparator";
    private static final String PARAM_EXPONENT_SEPARATOR = "exponentSeparator";
    private static final String PARAM_MINUS_SIGN = "minusSign";
    private static final String PARAM_INFINITY = "infinity";
    private static final String PARAM_NAN = "nan";
    private static final String PARAM_PERCENT = "percent";
    private static final String PARAM_PER_MILL = "perMill";
    private static final String PARAM_ZERO_DIGIT = "zeroDigit";
    private static final String PARAM_CURRENCY_CODE = "currencyCode";
    private static final String PARAM_CURRENCY_SYMBOL = "currencySymbol";
    private static final String PARAM_VALUE_RND_UP = "up";
    private static final String PARAM_VALUE_RND_DOWN = "down";
    private static final String PARAM_VALUE_RND_CEILING = "ceiling";
    private static final String PARAM_VALUE_RND_FLOOR = "floor";
    private static final String PARAM_VALUE_RND_HALF_DOWN = "halfDown";
    private static final String PARAM_VALUE_RND_HALF_EVEN = "halfEven";
    private static final String PARAM_VALUE_RND_HALF_UP = "halfUp";
    private static final String PARAM_VALUE_RND_UNNECESSARY = "unnecessary";
    private static final HashMap<String, ? extends ParameterHandler> PARAM_HANDLERS;
    private static final String SNIP_MARK = "[...]";
    private static final int MAX_QUOTATION_LENGTH = 10;
    private final String src;
    private int pos = 0;
    private final DecimalFormatSymbols symbols;
    private RoundingMode roundingMode;
    private Integer multiplier;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ExtendedDecimalFormatParser$ParameterHandler.class */
    private interface ParameterHandler {
        void handle(ExtendedDecimalFormatParser extendedDecimalFormatParser, String str) throws InvalidParameterValueException;
    }

    static {
        HashMap<String, ParameterHandler> m = new HashMap<>();
        m.put(PARAM_ROUNDING_MODE, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.1
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                RoundingMode parsedValue;
                if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_UP)) {
                    parsedValue = RoundingMode.UP;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_DOWN)) {
                    parsedValue = RoundingMode.DOWN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_CEILING)) {
                    parsedValue = RoundingMode.CEILING;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_FLOOR)) {
                    parsedValue = RoundingMode.FLOOR;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_DOWN)) {
                    parsedValue = RoundingMode.HALF_DOWN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_EVEN)) {
                    parsedValue = RoundingMode.HALF_EVEN;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_HALF_UP)) {
                    parsedValue = RoundingMode.HALF_UP;
                } else if (value.equals(ExtendedDecimalFormatParser.PARAM_VALUE_RND_UNNECESSARY)) {
                    parsedValue = RoundingMode.UNNECESSARY;
                } else {
                    throw new InvalidParameterValueException("Should be one of: up, down, ceiling, floor, halfDown, halfEven, unnecessary");
                }
                parser.roundingMode = parsedValue;
            }
        });
        ParameterHandler multiplierParamHandler = new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.2
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                try {
                    parser.multiplier = Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    throw new InvalidParameterValueException("Malformed integer.");
                }
            }
        };
        m.put(PARAM_MULTIPLIER, multiplierParamHandler);
        m.put(PARAM_MULTIPIER, multiplierParamHandler);
        m.put(PARAM_DECIMAL_SEPARATOR, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.3
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setDecimalSeparator(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_MONETARY_DECIMAL_SEPARATOR, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.4
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setMonetaryDecimalSeparator(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_GROUP_SEPARATOR, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.5
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setGroupingSeparator(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_EXPONENT_SEPARATOR, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.6
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setExponentSeparator(value);
            }
        });
        m.put(PARAM_MINUS_SIGN, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.7
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setMinusSign(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_INFINITY, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.8
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setInfinity(value);
            }
        });
        m.put(PARAM_NAN, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.9
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                parser.symbols.setNaN(value);
            }
        });
        m.put(PARAM_PERCENT, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.10
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setPercent(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_PER_MILL, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.11
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setPerMill(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_ZERO_DIGIT, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.12
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                if (value.length() == 1) {
                    parser.symbols.setZeroDigit(value.charAt(0));
                    return;
                }
                throw new InvalidParameterValueException("Must contain exactly 1 character.");
            }
        });
        m.put(PARAM_CURRENCY_CODE, new ParameterHandler() { // from class: freemarker.core.ExtendedDecimalFormatParser.13
            @Override // freemarker.core.ExtendedDecimalFormatParser.ParameterHandler
            public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
                try {
                    Currency currency = Currency.getInstance(value);
                    parser.symbols.setCurrency(currency);
                } catch (IllegalArgumentException e) {
                    throw new InvalidParameterValueException("Not a known ISO 4217 code.");
                }
            }
        });
        PARAM_HANDLERS = m;
    }

    static DecimalFormat parse(String formatString, Locale locale) throws java.text.ParseException {
        return new ExtendedDecimalFormatParser(formatString, locale).parse();
    }

    private DecimalFormat parse() throws java.text.ParseException {
        String stdPattern = fetchStandardPattern();
        skipWS();
        parseFormatStringExtension();
        try {
            DecimalFormat decimalFormat = new DecimalFormat(stdPattern, this.symbols);
            if (this.roundingMode != null) {
                decimalFormat.setRoundingMode(this.roundingMode);
            }
            if (this.multiplier != null) {
                decimalFormat.setMultiplier(this.multiplier.intValue());
            }
            return decimalFormat;
        } catch (IllegalArgumentException e) {
            java.text.ParseException pe = new java.text.ParseException(e.getMessage(), 0);
            if (e.getCause() != null) {
                try {
                    e.initCause(e.getCause());
                } catch (Exception e2) {
                }
            }
            throw pe;
        }
    }

    private void parseFormatStringExtension() throws java.text.ParseException {
        int ln = this.src.length();
        if (this.pos == ln) {
            return;
        }
        String currencySymbol = null;
        while (true) {
            int namePos = this.pos;
            String name = fetchName();
            if (name == null) {
                throw newExpectedSgParseException("name");
            }
            skipWS();
            if (!fetchChar('=')) {
                throw newExpectedSgParseException("\"=\"");
            }
            skipWS();
            int valuePos = this.pos;
            String value = fetchValue();
            if (value == null) {
                throw newExpectedSgParseException("value");
            }
            int paramEndPos = this.pos;
            ParameterHandler handler = PARAM_HANDLERS.get(name);
            if (handler == null) {
                if (name.equals(PARAM_CURRENCY_SYMBOL)) {
                    currencySymbol = value;
                } else {
                    throw newUnknownParameterException(name, namePos);
                }
            } else {
                try {
                    handler.handle(this, value);
                } catch (InvalidParameterValueException e) {
                    throw newInvalidParameterValueException(name, value, valuePos, e);
                }
            }
            skipWS();
            if (fetchChar(',')) {
                skipWS();
            } else if (this.pos != ln) {
                if (this.pos == paramEndPos) {
                    throw newExpectedSgParseException("parameter separator whitespace or comma");
                }
            } else {
                if (currencySymbol != null) {
                    this.symbols.setCurrencySymbol(currencySymbol);
                    return;
                }
                return;
            }
        }
    }

    private java.text.ParseException newInvalidParameterValueException(String name, String value, int valuePos, InvalidParameterValueException e) {
        return new java.text.ParseException(StringUtil.jQuote(value) + " is an invalid value for the \"" + name + "\" parameter: " + e.message, valuePos);
    }

    private java.text.ParseException newUnknownParameterException(String name, int namePos) throws java.text.ParseException {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Unsupported parameter name, ").append(StringUtil.jQuote(name));
        sb.append(". The supported names are: ");
        Set<String> legalNames = PARAM_HANDLERS.keySet();
        String[] legalNameArr = (String[]) legalNames.toArray(new String[legalNames.size()]);
        Arrays.sort(legalNameArr);
        for (int i = 0; i < legalNameArr.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(legalNameArr[i]);
        }
        return new java.text.ParseException(sb.toString(), namePos);
    }

    private void skipWS() {
        int ln = this.src.length();
        while (this.pos < ln && isWS(this.src.charAt(this.pos))) {
            this.pos++;
        }
    }

    private boolean fetchChar(char fetchedChar) {
        if (this.pos < this.src.length() && this.src.charAt(this.pos) == fetchedChar) {
            this.pos++;
            return true;
        }
        return false;
    }

    private boolean isWS(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == 160;
    }

    private String fetchName() throws java.text.ParseException {
        int ln = this.src.length();
        int startPos = this.pos;
        boolean firstChar = true;
        while (this.pos < ln) {
            char c = this.src.charAt(this.pos);
            if (firstChar) {
                if (!Character.isJavaIdentifierStart(c)) {
                    break;
                }
                firstChar = false;
                this.pos++;
            } else {
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }
                this.pos++;
            }
        }
        if (firstChar) {
            return null;
        }
        return this.src.substring(startPos, this.pos);
    }

    private String fetchValue() throws java.text.ParseException {
        int ln = this.src.length();
        int startPos = this.pos;
        char openedQuot = 0;
        boolean needsUnescaping = false;
        while (this.pos < ln) {
            char c = this.src.charAt(this.pos);
            if (c == '\'' || c == '\"') {
                if (openedQuot == 0) {
                    if (startPos != this.pos) {
                        throw new java.text.ParseException("The " + c + " character can only be used for quoting values, but it was in the middle of an non-quoted value.", this.pos);
                    }
                    openedQuot = c;
                } else if (c != openedQuot) {
                    continue;
                } else if (this.pos + 1 < ln && this.src.charAt(this.pos + 1) == openedQuot) {
                    this.pos++;
                    needsUnescaping = true;
                } else {
                    String str = this.src.substring(startPos + 1, this.pos);
                    this.pos++;
                    return needsUnescaping ? unescape(str, openedQuot) : str;
                }
            } else if (openedQuot == 0 && !Character.isJavaIdentifierPart(c)) {
                break;
            }
            this.pos++;
        }
        if (openedQuot != 0) {
            throw new java.text.ParseException("The " + openedQuot + " quotation wasn't closed when the end of the source was reached.", this.pos);
        }
        if (startPos == this.pos) {
            return null;
        }
        return this.src.substring(startPos, this.pos);
    }

    private String unescape(String s, char openedQuot) {
        return openedQuot == '\'' ? StringUtil.replace(s, "''", "'") : StringUtil.replace(s, "\"\"", "\"");
    }

    private String fetchStandardPattern() {
        String stdFormatStr;
        int pos = this.pos;
        int ln = this.src.length();
        int semicolonCnt = 0;
        boolean quotedMode = false;
        while (pos < ln) {
            char c = this.src.charAt(pos);
            if (c == ';' && !quotedMode) {
                semicolonCnt++;
                if (semicolonCnt == 2) {
                    break;
                }
            } else if (c == '\'') {
                if (quotedMode) {
                    if (pos + 1 < ln && this.src.charAt(pos + 1) == '\'') {
                        pos++;
                    } else {
                        quotedMode = false;
                    }
                } else {
                    quotedMode = true;
                }
            }
            pos++;
        }
        if (semicolonCnt < 2) {
            stdFormatStr = this.src;
        } else {
            int stdEndPos = pos;
            if (this.src.charAt(pos - 1) == ';') {
                stdEndPos--;
            }
            stdFormatStr = this.src.substring(0, stdEndPos);
        }
        if (pos < ln) {
            pos++;
        }
        this.pos = pos;
        return stdFormatStr;
    }

    private ExtendedDecimalFormatParser(String formatString, Locale locale) {
        this.src = formatString;
        this.symbols = DecimalFormatSymbols.getInstance(locale);
    }

    private java.text.ParseException newExpectedSgParseException(String expectedThing) {
        String quotation;
        int i = this.src.length() - 1;
        while (i >= 0 && Character.isWhitespace(this.src.charAt(i))) {
            i--;
        }
        int ln = i + 1;
        if (this.pos < ln) {
            int qEndPos = this.pos + 10;
            if (qEndPos >= ln) {
                quotation = this.src.substring(this.pos, ln);
            } else {
                quotation = this.src.substring(this.pos, qEndPos - "[...]".length()) + "[...]";
            }
        } else {
            quotation = null;
        }
        return new java.text.ParseException("Expected a(n) " + expectedThing + " at position " + this.pos + " (0-based), but " + (quotation == null ? "reached the end of the input." : "found: " + quotation), this.pos);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ExtendedDecimalFormatParser$InvalidParameterValueException.class */
    private static class InvalidParameterValueException extends Exception {
        private final String message;

        public InvalidParameterValueException(String message) {
            this.message = message;
        }
    }
}
