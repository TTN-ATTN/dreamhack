package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/PropertySetting.class */
final class PropertySetting extends TemplateElement {
    private final String key;
    private final Expression value;
    private final ValueSafetyChecker valueSafetyChecker;
    static final String[] SETTING_NAMES = {Configurable.BOOLEAN_FORMAT_KEY_CAMEL_CASE, "boolean_format", Configurable.C_FORMAT_KEY_CAMEL_CASE, "c_format", Configurable.CLASSIC_COMPATIBLE_KEY_CAMEL_CASE, "classic_compatible", Configurable.DATE_FORMAT_KEY_CAMEL_CASE, "date_format", Configurable.DATETIME_FORMAT_KEY_CAMEL_CASE, "datetime_format", "locale", Configurable.NUMBER_FORMAT_KEY_CAMEL_CASE, "number_format", Configurable.OUTPUT_ENCODING_KEY_CAMEL_CASE, "output_encoding", Configurable.SQL_DATE_AND_TIME_TIME_ZONE_KEY_CAMEL_CASE, "sql_date_and_time_time_zone", Configurable.TIME_FORMAT_KEY_CAMEL_CASE, Configurable.TIME_ZONE_KEY_CAMEL_CASE, "time_format", "time_zone", Configurable.URL_ESCAPING_CHARSET_KEY_CAMEL_CASE, "url_escaping_charset"};

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/PropertySetting$ValueSafetyChecker.class */
    private interface ValueSafetyChecker {
        void check(Environment environment, String str) throws TemplateException;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x00b5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    PropertySetting(freemarker.core.Token r7, freemarker.core.FMParserTokenManager r8, freemarker.core.Expression r9, freemarker.template.Configuration r10) throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 287
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.PropertySetting.<init>(freemarker.core.Token, freemarker.core.FMParserTokenManager, freemarker.core.Expression, freemarker.template.Configuration):void");
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException {
        String strval;
        TemplateModel mval = this.value.eval(env);
        if (mval instanceof TemplateScalarModel) {
            strval = ((TemplateScalarModel) mval).getAsString();
        } else if (mval instanceof TemplateBooleanModel) {
            strval = ((TemplateBooleanModel) mval).getAsBoolean() ? "true" : "false";
        } else if (mval instanceof TemplateNumberModel) {
            strval = ((TemplateNumberModel) mval).getAsNumber().toString();
        } else {
            strval = this.value.evalAndCoerceToStringOrUnsupportedMarkup(env);
        }
        if (this.valueSafetyChecker != null) {
            this.valueSafetyChecker.check(env, strval);
        }
        env.setSetting(this.key, strval);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        sb.append(' ');
        sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.key));
        sb.append('=');
        sb.append(this.value.getCanonicalForm());
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#setting";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.key;
            case 1:
                return this.value;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.ITEM_KEY;
            case 1:
                return ParameterRole.ITEM_VALUE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
