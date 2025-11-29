package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.pattern.parser.Parser;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp.class */
class BuiltInsForStringsRegexp {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$groupsBI.class */
    static class groupsBI extends BuiltIn {
        groupsBI() {
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel targetModel = this.target.eval(env);
            assertNonNull(targetModel, env);
            if (targetModel instanceof RegexMatchModel) {
                return ((RegexMatchModel) targetModel).getGroups();
            }
            if (targetModel instanceof RegexMatchModel.MatchWithGroups) {
                return ((RegexMatchModel.MatchWithGroups) targetModel).groupsSeq;
            }
            throw new UnexpectedTypeException(this.target, targetModel, "regular expression matcher", new Class[]{RegexMatchModel.class, RegexMatchModel.MatchWithGroups.class}, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$matchesBI.class */
    static class matchesBI extends BuiltInForString {
        matchesBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$matchesBI$MatcherBuilder.class */
        class MatcherBuilder implements TemplateMethodModel {
            String matchString;

            MatcherBuilder(String matchString) throws TemplateModelException {
                this.matchString = matchString;
            }

            @Override // freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int argCnt = args.size();
                matchesBI.this.checkMethodArgCount(argCnt, 1, 2);
                String patternString = (String) args.get(0);
                long flags = argCnt > 1 ? RegexpHelper.parseFlagString((String) args.get(1)) : 0L;
                if ((flags & 8589934592L) != 0) {
                    RegexpHelper.logFlagWarning(CallerData.NA + matchesBI.this.key + " doesn't support the \"f\" flag.");
                }
                Pattern pattern = RegexpHelper.getPattern(patternString, (int) flags);
                return new RegexMatchModel(pattern, this.matchString);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new MatcherBuilder(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$replace_reBI.class */
    static class replace_reBI extends BuiltInForString {
        replace_reBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$replace_reBI$ReplaceMethod.class */
        class ReplaceMethod implements TemplateMethodModel {
            private String s;

            ReplaceMethod(String s) {
                this.s = s;
            }

            @Override // freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                String strReplaceAll;
                String result;
                int argCnt = args.size();
                replace_reBI.this.checkMethodArgCount(argCnt, 2, 3);
                String arg1 = (String) args.get(0);
                String arg2 = (String) args.get(1);
                long flags = argCnt > 2 ? RegexpHelper.parseFlagString((String) args.get(2)) : 0L;
                if ((flags & 4294967296L) == 0) {
                    RegexpHelper.checkNonRegexpFlags(Parser.REPLACE_CONVERTER_WORD, flags);
                    result = StringUtil.replace(this.s, arg1, arg2, (flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0, (flags & 8589934592L) != 0);
                } else {
                    Pattern pattern = RegexpHelper.getPattern(arg1, (int) flags);
                    Matcher matcher = pattern.matcher(this.s);
                    if ((flags & 8589934592L) != 0) {
                        strReplaceAll = matcher.replaceFirst(arg2);
                    } else {
                        strReplaceAll = matcher.replaceAll(arg2);
                    }
                    result = strReplaceAll;
                }
                return new SimpleScalar(result);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new ReplaceMethod(s);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$RegexMatchModel.class */
    static class RegexMatchModel implements TemplateBooleanModel, TemplateCollectionModel, TemplateSequenceModel {
        final Pattern pattern;
        final String input;
        private Matcher firedEntireInputMatcher;
        private Boolean entireInputMatched;
        private TemplateSequenceModel entireInputMatchGroups;
        private ArrayList matchingInputParts;

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsRegexp$RegexMatchModel$MatchWithGroups.class */
        static class MatchWithGroups implements TemplateScalarModel {
            final String matchedInputPart;
            final SimpleSequence groupsSeq;

            MatchWithGroups(String input, Matcher matcher) {
                this.matchedInputPart = input.substring(matcher.start(), matcher.end());
                int grpCount = matcher.groupCount() + 1;
                this.groupsSeq = new SimpleSequence(grpCount, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
                for (int i = 0; i < grpCount; i++) {
                    this.groupsSeq.add(matcher.group(i));
                }
            }

            @Override // freemarker.template.TemplateScalarModel
            public String getAsString() {
                return this.matchedInputPart;
            }
        }

        RegexMatchModel(Pattern pattern, String input) {
            this.pattern = pattern;
            this.input = input;
        }

        @Override // freemarker.template.TemplateSequenceModel
        public TemplateModel get(int i) throws TemplateModelException {
            ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                matchingInputParts = getMatchingInputPartsAndStoreResults();
            }
            return (TemplateModel) matchingInputParts.get(i);
        }

        @Override // freemarker.template.TemplateBooleanModel
        public boolean getAsBoolean() {
            Boolean result = this.entireInputMatched;
            return result != null ? result.booleanValue() : isEntrieInputMatchesAndStoreResults();
        }

        TemplateModel getGroups() {
            TemplateSequenceModel entireInputMatchGroups = this.entireInputMatchGroups;
            if (entireInputMatchGroups == null) {
                Matcher t = this.firedEntireInputMatcher;
                if (t == null) {
                    isEntrieInputMatchesAndStoreResults();
                    t = this.firedEntireInputMatcher;
                }
                final Matcher firedEntireInputMatcher = t;
                entireInputMatchGroups = new TemplateSequenceModel() { // from class: freemarker.core.BuiltInsForStringsRegexp.RegexMatchModel.1
                    @Override // freemarker.template.TemplateSequenceModel
                    public TemplateModel get(int i) throws TemplateModelException {
                        try {
                            return new SimpleScalar(firedEntireInputMatcher.group(i));
                        } catch (Exception e) {
                            throw new _TemplateModelException(e, "Failed to read regular expression match group");
                        }
                    }

                    @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
                    public int size() throws TemplateModelException {
                        try {
                            return firedEntireInputMatcher.groupCount() + 1;
                        } catch (Exception e) {
                            throw new _TemplateModelException(e, "Failed to get regular expression match group count");
                        }
                    }
                };
                this.entireInputMatchGroups = entireInputMatchGroups;
            }
            return entireInputMatchGroups;
        }

        private ArrayList getMatchingInputPartsAndStoreResults() throws TemplateModelException {
            ArrayList matchingInputParts = new ArrayList();
            Matcher matcher = this.pattern.matcher(this.input);
            while (matcher.find()) {
                matchingInputParts.add(new MatchWithGroups(this.input, matcher));
            }
            this.matchingInputParts = matchingInputParts;
            return matchingInputParts;
        }

        private boolean isEntrieInputMatchesAndStoreResults() {
            Matcher matcher = this.pattern.matcher(this.input);
            boolean matches = matcher.matches();
            this.firedEntireInputMatcher = matcher;
            this.entireInputMatched = Boolean.valueOf(matches);
            return matches;
        }

        @Override // freemarker.template.TemplateCollectionModel
        public TemplateModelIterator iterator() {
            final ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                final Matcher matcher = this.pattern.matcher(this.input);
                return new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForStringsRegexp.RegexMatchModel.2
                    private int nextIdx = 0;
                    boolean hasFindInfo;

                    {
                        this.hasFindInfo = matcher.find();
                    }

                    @Override // freemarker.template.TemplateModelIterator
                    public boolean hasNext() {
                        ArrayList matchingInputParts2 = RegexMatchModel.this.matchingInputParts;
                        if (matchingInputParts2 == null) {
                            return this.hasFindInfo;
                        }
                        return this.nextIdx < matchingInputParts2.size();
                    }

                    @Override // freemarker.template.TemplateModelIterator
                    public TemplateModel next() throws TemplateModelException {
                        ArrayList matchingInputParts2 = RegexMatchModel.this.matchingInputParts;
                        if (matchingInputParts2 == null) {
                            if (!this.hasFindInfo) {
                                throw new _TemplateModelException("There were no more regular expression matches");
                            }
                            MatchWithGroups result = new MatchWithGroups(RegexMatchModel.this.input, matcher);
                            this.nextIdx++;
                            this.hasFindInfo = matcher.find();
                            return result;
                        }
                        try {
                            int i = this.nextIdx;
                            this.nextIdx = i + 1;
                            return (TemplateModel) matchingInputParts2.get(i);
                        } catch (IndexOutOfBoundsException e) {
                            throw new _TemplateModelException(e, "There were no more regular expression matches");
                        }
                    }
                };
            }
            return new TemplateModelIterator() { // from class: freemarker.core.BuiltInsForStringsRegexp.RegexMatchModel.3
                private int nextIdx = 0;

                @Override // freemarker.template.TemplateModelIterator
                public boolean hasNext() {
                    return this.nextIdx < matchingInputParts.size();
                }

                @Override // freemarker.template.TemplateModelIterator
                public TemplateModel next() throws TemplateModelException {
                    try {
                        ArrayList arrayList = matchingInputParts;
                        int i = this.nextIdx;
                        this.nextIdx = i + 1;
                        return (TemplateModel) arrayList.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        throw new _TemplateModelException(e, "There were no more regular expression matches");
                    }
                }
            };
        }

        @Override // freemarker.template.TemplateSequenceModel, freemarker.template.TemplateCollectionModelEx
        public int size() throws TemplateModelException {
            ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                matchingInputParts = getMatchingInputPartsAndStoreResults();
            }
            return matchingInputParts.size();
        }
    }

    private BuiltInsForStringsRegexp() {
    }
}
