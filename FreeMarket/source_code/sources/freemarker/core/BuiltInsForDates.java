package freemarker.core;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.DateUtil;
import java.sql.Time;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates.class */
class BuiltInsForDates {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates$dateType_if_unknownBI.class */
    static class dateType_if_unknownBI extends BuiltIn {
        private final int dateType;

        dateType_if_unknownBI(int dateType) {
            this.dateType = dateType;
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateDateModel) {
                TemplateDateModel tdm = (TemplateDateModel) model;
                int tdmDateType = tdm.getDateType();
                if (tdmDateType != 0) {
                    return tdm;
                }
                return new SimpleDate(EvalUtil.modelToDate(tdm, this.target), this.dateType);
            }
            throw BuiltInForDate.newNonDateException(env, model, this.target);
        }

        protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates$iso_BI.class */
    static class iso_BI extends AbstractISOBI {

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates$iso_BI$Result.class */
        class Result implements TemplateMethodModelEx {
            private final Date date;
            private final int dateType;
            private final Environment env;

            Result(Date date, int dateType, Environment env) {
                this.date = date;
                this.dateType = dateType;
                this.env = env;
            }

            /* JADX WARN: Removed duplicated region for block: B:7:0x0038  */
            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public java.lang.Object exec(java.util.List r11) throws freemarker.template.TemplateModelException {
                /*
                    Method dump skipped, instructions count: 240
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: freemarker.core.BuiltInsForDates.iso_BI.Result.exec(java.util.List):java.lang.Object");
            }
        }

        iso_BI(Boolean showOffset, int accuracy) {
            super(showOffset, accuracy);
        }

        @Override // freemarker.core.BuiltInForDate
        protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
            checkDateTypeNotUnknown(dateType);
            return new Result(date, dateType, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates$iso_utc_or_local_BI.class */
    static class iso_utc_or_local_BI extends AbstractISOBI {
        private final boolean useUTC;

        iso_utc_or_local_BI(Boolean showOffset, int accuracy, boolean useUTC) {
            super(showOffset, accuracy);
            this.useUTC = useUTC;
        }

        @Override // freemarker.core.BuiltInForDate
        protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
            TimeZone timeZone;
            checkDateTypeNotUnknown(dateType);
            boolean z = dateType != 1;
            boolean z2 = dateType != 2;
            boolean zShouldShowOffset = shouldShowOffset(date, dateType, env);
            int i = this.accuracy;
            if (this.useUTC) {
                timeZone = DateUtil.UTC;
            } else if (env.shouldUseSQLDTTZ(date.getClass())) {
                timeZone = env.getSQLDateAndTimeTimeZone();
            } else {
                timeZone = env.getTimeZone();
            }
            return new SimpleScalar(DateUtil.dateToISO8601String(date, z, z2, zShouldShowOffset, i, timeZone, env.getISOBuiltInCalendarFactory()));
        }
    }

    private BuiltInsForDates() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForDates$AbstractISOBI.class */
    static abstract class AbstractISOBI extends BuiltInForDate {
        protected final Boolean showOffset;
        protected final int accuracy;

        protected AbstractISOBI(Boolean showOffset, int accuracy) {
            this.showOffset = showOffset;
            this.accuracy = accuracy;
        }

        protected void checkDateTypeNotUnknown(int dateType) throws TemplateException {
            if (dateType == 0) {
                throw new _MiscTemplateException(new _ErrorDescriptionBuilder("The value of the following has unknown date type, but ?", this.key, " needs a value where it's known if it's a date (no time part), time, or date-time value:").blame(this.target).tip("Use ?date, ?time, or ?datetime to tell FreeMarker the exact type."));
            }
        }

        protected boolean shouldShowOffset(Date date, int dateType, Environment env) {
            if (dateType == 2) {
                return false;
            }
            if (this.showOffset != null) {
                return this.showOffset.booleanValue();
            }
            return !(date instanceof Time) || _TemplateAPI.getTemplateLanguageVersionAsInt(this) < _VersionInts.V_2_3_21;
        }
    }
}
