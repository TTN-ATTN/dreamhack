package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import freemarker.cache.MruCacheStorage;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RegexpHelper.class */
final class RegexpHelper {
    private static final int MAX_FLAG_WARNINGS_LOGGED = 25;
    private static int flagWarningsCnt;
    static final long RE_FLAG_REGEXP = 4294967296L;
    static final long RE_FLAG_FIRST_ONLY = 8589934592L;
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static volatile boolean flagWarningsEnabled = LOG.isWarnEnabled();
    private static final Object flagWarningsCntSync = new Object();
    private static final MruCacheStorage patternCache = new MruCacheStorage(50, 150);
    static final long RE_FLAG_CASE_INSENSITIVE = intFlagToLong(2);
    static final long RE_FLAG_MULTILINE = intFlagToLong(8);
    static final long RE_FLAG_COMMENTS = intFlagToLong(4);
    static final long RE_FLAG_DOTALL = intFlagToLong(32);

    private static long intFlagToLong(int flag) {
        return flag & 65535;
    }

    private RegexpHelper() {
    }

    static Pattern getPattern(String patternString, int flags) throws TemplateModelException {
        Pattern result;
        PatternCacheKey patternKey = new PatternCacheKey(patternString, flags);
        synchronized (patternCache) {
            result = (Pattern) patternCache.get(patternKey);
        }
        if (result != null) {
            return result;
        }
        try {
            Pattern result2 = Pattern.compile(patternString, flags);
            synchronized (patternCache) {
                patternCache.put(patternKey, result2);
            }
            return result2;
        } catch (PatternSyntaxException e) {
            throw new _TemplateModelException(e, "Malformed regular expression: ", new _DelayedGetMessage(e));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/RegexpHelper$PatternCacheKey.class */
    private static class PatternCacheKey {
        private final String patternString;
        private final int flags;
        private final int hashCode;

        public PatternCacheKey(String patternString, int flags) {
            this.patternString = patternString;
            this.flags = flags;
            this.hashCode = patternString.hashCode() + (31 * flags);
        }

        public boolean equals(Object that) {
            if (that instanceof PatternCacheKey) {
                PatternCacheKey thatPCK = (PatternCacheKey) that;
                return thatPCK.flags == this.flags && thatPCK.patternString.equals(this.patternString);
            }
            return false;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    static long parseFlagString(String flagString) {
        long flags = 0;
        for (int i = 0; i < flagString.length(); i++) {
            char c = flagString.charAt(i);
            switch (c) {
                case 'c':
                    flags |= RE_FLAG_COMMENTS;
                    break;
                case 'd':
                case 'e':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                default:
                    if (flagWarningsEnabled) {
                        logFlagWarning("Unrecognized regular expression flag: " + StringUtil.jQuote(String.valueOf(c)) + ".");
                        break;
                    } else {
                        break;
                    }
                case 'f':
                    flags |= RE_FLAG_FIRST_ONLY;
                    break;
                case 'i':
                    flags |= RE_FLAG_CASE_INSENSITIVE;
                    break;
                case 'm':
                    flags |= RE_FLAG_MULTILINE;
                    break;
                case 'r':
                    flags |= RE_FLAG_REGEXP;
                    break;
                case 's':
                    flags |= RE_FLAG_DOTALL;
                    break;
            }
        }
        return flags;
    }

    static void logFlagWarning(String message) {
        if (flagWarningsEnabled) {
            synchronized (flagWarningsCntSync) {
                int cnt = flagWarningsCnt;
                if (cnt < 25) {
                    flagWarningsCnt++;
                    String message2 = message + " This will be an error in some later FreeMarker version!";
                    if (cnt + 1 == 25) {
                        message2 = message2 + " [Will not log more regular expression flag problems until restart!]";
                    }
                    LOG.warn(message2);
                    return;
                }
                flagWarningsEnabled = false;
            }
        }
    }

    static void checkNonRegexpFlags(String biName, long flags) throws _TemplateModelException {
        checkOnlyHasNonRegexpFlags(biName, flags, false);
    }

    static void checkOnlyHasNonRegexpFlags(String biName, long flags, boolean strict) throws _TemplateModelException {
        String flag;
        if (strict || flagWarningsEnabled) {
            if ((flags & RE_FLAG_MULTILINE) != 0) {
                flag = ANSIConstants.ESC_END;
            } else if ((flags & RE_FLAG_DOTALL) != 0) {
                flag = "s";
            } else if ((flags & RE_FLAG_COMMENTS) != 0) {
                flag = "c";
            } else {
                return;
            }
            Object[] msg = {CallerData.NA, biName, " doesn't support the \"", flag, "\" flag without the \"r\" flag."};
            if (strict) {
                throw new _TemplateModelException(msg);
            }
            logFlagWarning(new _ErrorDescriptionBuilder(msg).toString());
        }
    }
}
