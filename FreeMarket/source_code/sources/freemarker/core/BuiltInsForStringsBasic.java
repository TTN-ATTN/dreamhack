package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.StringUtil;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic.class */
class BuiltInsForStringsBasic {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$cap_firstBI.class */
    static class cap_firstBI extends BuiltInForString {
        cap_firstBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            int i = 0;
            int ln = s.length();
            while (i < ln && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
            if (i < ln) {
                StringBuilder b = new StringBuilder(s);
                b.setCharAt(i, Character.toUpperCase(s.charAt(i)));
                s = b.toString();
            }
            return new SimpleScalar(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$capitalizeBI.class */
    static class capitalizeBI extends BuiltInForString {
        capitalizeBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.capitalize(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$chop_linebreakBI.class */
    static class chop_linebreakBI extends BuiltInForString {
        chop_linebreakBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.chomp(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$containsBI.class */
    static class containsBI extends BuiltIn {
        containsBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$containsBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private final String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                containsBI.this.checkMethodArgCount(args, 1);
                return this.s.indexOf(containsBI.this.getStringMethodArg(args, 0)) != -1 ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            return new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_contains\" instead."));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ends_withBI.class */
    static class ends_withBI extends BuiltInForString {
        ends_withBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ends_withBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                ends_withBI.this.checkMethodArgCount(args, 1);
                return this.s.endsWith(ends_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ensure_ends_withBI.class */
    static class ensure_ends_withBI extends BuiltInForString {
        ensure_ends_withBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ensure_ends_withBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                ensure_ends_withBI.this.checkMethodArgCount(args, 1);
                String suffix = ensure_ends_withBI.this.getStringMethodArg(args, 0);
                return new SimpleScalar(this.s.endsWith(suffix) ? this.s : this.s + suffix);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ensure_starts_withBI.class */
    static class ensure_starts_withBI extends BuiltInForString {
        ensure_starts_withBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$ensure_starts_withBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                boolean startsWithPrefix;
                String addedPrefix;
                ensure_starts_withBI.this.checkMethodArgCount(args, 1, 3);
                String checkedPrefix = ensure_starts_withBI.this.getStringMethodArg(args, 0);
                if (args.size() > 1) {
                    addedPrefix = ensure_starts_withBI.this.getStringMethodArg(args, 1);
                    long flags = args.size() > 2 ? RegexpHelper.parseFlagString(ensure_starts_withBI.this.getStringMethodArg(args, 2)) : 4294967296L;
                    if ((flags & 4294967296L) == 0) {
                        RegexpHelper.checkOnlyHasNonRegexpFlags(ensure_starts_withBI.this.key, flags, true);
                        if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0) {
                            startsWithPrefix = this.s.startsWith(checkedPrefix);
                        } else {
                            startsWithPrefix = this.s.toLowerCase().startsWith(checkedPrefix.toLowerCase());
                        }
                    } else {
                        Pattern pattern = RegexpHelper.getPattern(checkedPrefix, (int) flags);
                        Matcher matcher = pattern.matcher(this.s);
                        startsWithPrefix = matcher.lookingAt();
                    }
                } else {
                    startsWithPrefix = this.s.startsWith(checkedPrefix);
                    addedPrefix = checkedPrefix;
                }
                return new SimpleScalar(startsWithPrefix ? this.s : addedPrefix + this.s);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$index_ofBI.class */
    static class index_ofBI extends BuiltIn {
        private final boolean findLast;

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$index_ofBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private final String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int argCnt = args.size();
                index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
                String subStr = index_ofBI.this.getStringMethodArg(args, 0);
                if (argCnt > 1) {
                    int startIdx = index_ofBI.this.getNumberMethodArg(args, 1).intValue();
                    return new SimpleNumber(index_ofBI.this.findLast ? this.s.lastIndexOf(subStr, startIdx) : this.s.indexOf(subStr, startIdx));
                }
                return new SimpleNumber(index_ofBI.this.findLast ? this.s.lastIndexOf(subStr) : this.s.indexOf(subStr));
            }
        }

        index_ofBI(boolean findLast) {
            this.findLast = findLast;
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            return new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_index_of\" instead."));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_afterBI.class */
    static class keep_afterBI extends BuiltInForString {
        keep_afterBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_afterBI$KeepAfterMethod.class */
        class KeepAfterMethod implements TemplateMethodModelEx {
            private String s;

            KeepAfterMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int startIndex;
                int argCnt = args.size();
                keep_afterBI.this.checkMethodArgCount(argCnt, 1, 2);
                String separatorString = keep_afterBI.this.getStringMethodArg(args, 0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_afterBI.this.getStringMethodArg(args, 1)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkOnlyHasNonRegexpFlags(keep_afterBI.this.key, flags, true);
                    if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0) {
                        startIndex = this.s.indexOf(separatorString);
                    } else {
                        startIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
                    }
                    if (startIndex >= 0) {
                        startIndex += separatorString.length();
                    }
                } else {
                    Pattern pattern = RegexpHelper.getPattern(separatorString, (int) flags);
                    Matcher matcher = pattern.matcher(this.s);
                    if (matcher.find()) {
                        startIndex = matcher.end();
                    } else {
                        startIndex = -1;
                    }
                }
                return startIndex == -1 ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new KeepAfterMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_after_lastBI.class */
    static class keep_after_lastBI extends BuiltInForString {
        keep_after_lastBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_after_lastBI$KeepAfterMethod.class */
        class KeepAfterMethod implements TemplateMethodModelEx {
            private String s;

            KeepAfterMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int startIndex;
                int argCnt = args.size();
                keep_after_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
                String separatorString = keep_after_lastBI.this.getStringMethodArg(args, 0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_after_lastBI.this.getStringMethodArg(args, 1)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkOnlyHasNonRegexpFlags(keep_after_lastBI.this.key, flags, true);
                    if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0) {
                        startIndex = this.s.lastIndexOf(separatorString);
                    } else {
                        startIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
                    }
                    if (startIndex >= 0) {
                        startIndex += separatorString.length();
                    }
                } else if (separatorString.length() == 0) {
                    startIndex = this.s.length();
                } else {
                    Pattern pattern = RegexpHelper.getPattern(separatorString, (int) flags);
                    Matcher matcher = pattern.matcher(this.s);
                    if (matcher.find()) {
                        int iEnd = matcher.end();
                        while (true) {
                            startIndex = iEnd;
                            if (!matcher.find(matcher.start() + 1)) {
                                break;
                            }
                            iEnd = matcher.end();
                        }
                    } else {
                        startIndex = -1;
                    }
                }
                return startIndex == -1 ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new KeepAfterMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_beforeBI.class */
    static class keep_beforeBI extends BuiltInForString {
        keep_beforeBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_beforeBI$KeepUntilMethod.class */
        class KeepUntilMethod implements TemplateMethodModelEx {
            private String s;

            KeepUntilMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int stopIndex;
                int argCnt = args.size();
                keep_beforeBI.this.checkMethodArgCount(argCnt, 1, 2);
                String separatorString = keep_beforeBI.this.getStringMethodArg(args, 0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_beforeBI.this.getStringMethodArg(args, 1)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkOnlyHasNonRegexpFlags(keep_beforeBI.this.key, flags, true);
                    if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0) {
                        stopIndex = this.s.indexOf(separatorString);
                    } else {
                        stopIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
                    }
                } else {
                    Pattern pattern = RegexpHelper.getPattern(separatorString, (int) flags);
                    Matcher matcher = pattern.matcher(this.s);
                    if (matcher.find()) {
                        stopIndex = matcher.start();
                    } else {
                        stopIndex = -1;
                    }
                }
                return stopIndex == -1 ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new KeepUntilMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_before_lastBI.class */
    static class keep_before_lastBI extends BuiltInForString {
        keep_before_lastBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$keep_before_lastBI$KeepUntilMethod.class */
        class KeepUntilMethod implements TemplateMethodModelEx {
            private String s;

            KeepUntilMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int stopIndex;
                int argCnt = args.size();
                keep_before_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
                String separatorString = keep_before_lastBI.this.getStringMethodArg(args, 0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_before_lastBI.this.getStringMethodArg(args, 1)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkOnlyHasNonRegexpFlags(keep_before_lastBI.this.key, flags, true);
                    if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0) {
                        stopIndex = this.s.lastIndexOf(separatorString);
                    } else {
                        stopIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
                    }
                } else if (separatorString.length() == 0) {
                    stopIndex = this.s.length();
                } else {
                    Pattern pattern = RegexpHelper.getPattern(separatorString, (int) flags);
                    Matcher matcher = pattern.matcher(this.s);
                    if (matcher.find()) {
                        int iStart = matcher.start();
                        while (true) {
                            stopIndex = iStart;
                            if (!matcher.find(stopIndex + 1)) {
                                break;
                            }
                            iStart = matcher.start();
                        }
                    } else {
                        stopIndex = -1;
                    }
                }
                return stopIndex == -1 ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new KeepUntilMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$lengthBI.class */
    static class lengthBI extends BuiltInForString {
        lengthBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new SimpleNumber(s.length());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$lower_caseBI.class */
    static class lower_caseBI extends BuiltInForString {
        lower_caseBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.toLowerCase(env.getLocale()));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$c_lower_caseBI.class */
    static class c_lower_caseBI extends BuiltInForString {
        c_lower_caseBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.toLowerCase(Locale.ROOT));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$padBI.class */
    static class padBI extends BuiltInForString {
        private final boolean leftPadder;

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$padBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private final String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                String strRightPad;
                int argCnt = args.size();
                padBI.this.checkMethodArgCount(argCnt, 1, 2);
                int width = padBI.this.getNumberMethodArg(args, 0).intValue();
                if (argCnt > 1) {
                    String filling = padBI.this.getStringMethodArg(args, 1);
                    try {
                        if (padBI.this.leftPadder) {
                            strRightPad = StringUtil.leftPad(this.s, width, filling);
                        } else {
                            strRightPad = StringUtil.rightPad(this.s, width, filling);
                        }
                        return new SimpleScalar(strRightPad);
                    } catch (IllegalArgumentException e) {
                        if (filling.length() == 0) {
                            throw new _TemplateModelException(CallerData.NA, padBI.this.key, "(...) argument #2 can't be a 0-length string.");
                        }
                        throw new _TemplateModelException(e, CallerData.NA, padBI.this.key, "(...) failed: ", e);
                    }
                }
                return new SimpleScalar(padBI.this.leftPadder ? StringUtil.leftPad(this.s, width) : StringUtil.rightPad(this.s, width));
            }
        }

        padBI(boolean leftPadder) {
            this.leftPadder = leftPadder;
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$remove_beginningBI.class */
    static class remove_beginningBI extends BuiltInForString {
        remove_beginningBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$remove_beginningBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                remove_beginningBI.this.checkMethodArgCount(args, 1);
                String prefix = remove_beginningBI.this.getStringMethodArg(args, 0);
                return new SimpleScalar(this.s.startsWith(prefix) ? this.s.substring(prefix.length()) : this.s);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$remove_endingBI.class */
    static class remove_endingBI extends BuiltInForString {
        remove_endingBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$remove_endingBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                remove_endingBI.this.checkMethodArgCount(args, 1);
                String suffix = remove_endingBI.this.getStringMethodArg(args, 0);
                return new SimpleScalar(this.s.endsWith(suffix) ? this.s.substring(0, this.s.length() - suffix.length()) : this.s);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$split_BI.class */
    static class split_BI extends BuiltInForString {
        split_BI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$split_BI$SplitMethod.class */
        class SplitMethod implements TemplateMethodModel {
            private String s;

            SplitMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                String[] result;
                int argCnt = args.size();
                split_BI.this.checkMethodArgCount(argCnt, 1, 2);
                String splitString = (String) args.get(0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString((String) args.get(1)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkNonRegexpFlags(split_BI.this.key, flags);
                    result = StringUtil.split(this.s, splitString, (flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0);
                } else {
                    Pattern pattern = RegexpHelper.getPattern(splitString, (int) flags);
                    result = pattern.split(this.s);
                }
                return ObjectWrapper.DEFAULT_WRAPPER.wrap(result);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new SplitMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$starts_withBI.class */
    static class starts_withBI extends BuiltInForString {
        starts_withBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$starts_withBI$BIMethod.class */
        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                starts_withBI.this.checkMethodArgCount(args, 1);
                return this.s.startsWith(starts_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new BIMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$substringBI.class */
    static class substringBI extends BuiltInForString {
        substringBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(final String s, Environment env) throws TemplateException {
            return new TemplateMethodModelEx() { // from class: freemarker.core.BuiltInsForStringsBasic.substringBI.1
                @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
                public Object exec(List args) throws TemplateModelException {
                    int argCount = args.size();
                    substringBI.this.checkMethodArgCount(argCount, 1, 2);
                    int beginIdx = substringBI.this.getNumberMethodArg(args, 0).intValue();
                    int len = s.length();
                    if (beginIdx < 0) {
                        throw newIndexLessThan0Exception(0, beginIdx);
                    }
                    if (beginIdx > len) {
                        throw newIndexGreaterThanLengthException(0, beginIdx, len);
                    }
                    if (argCount > 1) {
                        int endIdx = substringBI.this.getNumberMethodArg(args, 1).intValue();
                        if (endIdx < 0) {
                            throw newIndexLessThan0Exception(1, endIdx);
                        }
                        if (endIdx > len) {
                            throw newIndexGreaterThanLengthException(1, endIdx, len);
                        }
                        if (beginIdx > endIdx) {
                            throw _MessageUtil.newMethodArgsInvalidValueException(CallerData.NA + substringBI.this.key, "The begin index argument, ", Integer.valueOf(beginIdx), ", shouldn't be greater than the end index argument, ", Integer.valueOf(endIdx), ".");
                        }
                        return new SimpleScalar(s.substring(beginIdx, endIdx));
                    }
                    return new SimpleScalar(s.substring(beginIdx));
                }

                private TemplateModelException newIndexGreaterThanLengthException(int argIdx, int idx, int len) throws TemplateModelException {
                    return _MessageUtil.newMethodArgInvalidValueException(CallerData.NA + substringBI.this.key, argIdx, "The index mustn't be greater than the length of the string, ", Integer.valueOf(len), ", but it was ", Integer.valueOf(idx), ".");
                }

                private TemplateModelException newIndexLessThan0Exception(int argIdx, int idx) throws TemplateModelException {
                    return _MessageUtil.newMethodArgInvalidValueException(CallerData.NA + substringBI.this.key, argIdx, "The index must be at least 0, but was ", Integer.valueOf(idx), ".");
                }
            };
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$trimBI.class */
    static class trimBI extends BuiltInForString {
        trimBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.trim());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$AbstractTruncateBI.class */
    static abstract class AbstractTruncateBI extends BuiltInForString {
        protected abstract TemplateModel truncate(TruncateBuiltinAlgorithm truncateBuiltinAlgorithm, String str, int i, TemplateModel templateModel, Integer num, Environment environment) throws TemplateException;

        protected abstract boolean allowMarkupTerminator();

        AbstractTruncateBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(final String s, final Environment env) {
            return new TemplateMethodModelEx() { // from class: freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI.1
                @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
                public Object exec(List args) throws TemplateModelException {
                    TemplateModel terminator;
                    Integer terminatorLength;
                    int argCount = args.size();
                    AbstractTruncateBI.this.checkMethodArgCount(argCount, 1, 3);
                    int maxLength = AbstractTruncateBI.this.getNumberMethodArg(args, 0).intValue();
                    if (maxLength < 0) {
                        throw new _TemplateModelException(CallerData.NA, AbstractTruncateBI.this.key, "(...) argument #1 can't be negative.");
                    }
                    if (argCount > 1) {
                        terminator = (TemplateModel) args.get(1);
                        if (!(terminator instanceof TemplateScalarModel)) {
                            if (AbstractTruncateBI.this.allowMarkupTerminator()) {
                                if (!(terminator instanceof TemplateMarkupOutputModel)) {
                                    throw _MessageUtil.newMethodArgMustBeStringOrMarkupOutputException(CallerData.NA + AbstractTruncateBI.this.key, 1, terminator);
                                }
                            } else {
                                throw _MessageUtil.newMethodArgMustBeStringException(CallerData.NA + AbstractTruncateBI.this.key, 1, terminator);
                            }
                        }
                        Number terminatorLengthNum = AbstractTruncateBI.this.getOptNumberMethodArg(args, 2);
                        terminatorLength = terminatorLengthNum != null ? Integer.valueOf(terminatorLengthNum.intValue()) : null;
                        if (terminatorLength != null && terminatorLength.intValue() < 0) {
                            throw new _TemplateModelException(CallerData.NA, AbstractTruncateBI.this.key, "(...) argument #3 can't be negative.");
                        }
                    } else {
                        terminator = null;
                        terminatorLength = null;
                    }
                    try {
                        TruncateBuiltinAlgorithm algorithm = env.getTruncateBuiltinAlgorithm();
                        return AbstractTruncateBI.this.truncate(algorithm, s, maxLength, terminator, terminatorLength, env);
                    } catch (TemplateException e) {
                        throw new _TemplateModelException(AbstractTruncateBI.this, e, env, "Truncation failed; see cause exception");
                    }
                }
            };
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncateBI.class */
    static class truncateBI extends AbstractTruncateBI {
        truncateBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncate(s, maxLength, (TemplateScalarModel) terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncate_wBI.class */
    static class truncate_wBI extends AbstractTruncateBI {
        truncate_wBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncateW(s, maxLength, (TemplateScalarModel) terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncate_cBI.class */
    static class truncate_cBI extends AbstractTruncateBI {
        truncate_cBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncateC(s, maxLength, (TemplateScalarModel) terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncate_mBI.class */
    static class truncate_mBI extends AbstractTruncateBI {
        truncate_mBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncateM(s, maxLength, terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return true;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncate_w_mBI.class */
    static class truncate_w_mBI extends AbstractTruncateBI {
        truncate_w_mBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncateWM(s, maxLength, terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return true;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$truncate_c_mBI.class */
    static class truncate_c_mBI extends AbstractTruncateBI {
        truncate_c_mBI() {
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
            return algorithm.truncateCM(s, maxLength, terminator, terminatorLength, env);
        }

        @Override // freemarker.core.BuiltInsForStringsBasic.AbstractTruncateBI
        protected boolean allowMarkupTerminator() {
            return true;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$uncap_firstBI.class */
    static class uncap_firstBI extends BuiltInForString {
        uncap_firstBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            int i = 0;
            int ln = s.length();
            while (i < ln && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
            if (i < ln) {
                StringBuilder b = new StringBuilder(s);
                b.setCharAt(i, Character.toLowerCase(s.charAt(i)));
                s = b.toString();
            }
            return new SimpleScalar(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$upper_caseBI.class */
    static class upper_caseBI extends BuiltInForString {
        upper_caseBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.toUpperCase(env.getLocale()));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$c_upper_caseBI.class */
    static class c_upper_caseBI extends BuiltInForString {
        c_upper_caseBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.toUpperCase(Locale.ROOT));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsBasic$word_listBI.class */
    static class word_listBI extends BuiltInForString {
        word_listBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            SimpleSequence result = new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            StringTokenizer st = new StringTokenizer(s);
            while (st.hasMoreTokens()) {
                result.add(st.nextToken());
            }
            return result;
        }
    }

    private BuiltInsForStringsBasic() {
    }
}
