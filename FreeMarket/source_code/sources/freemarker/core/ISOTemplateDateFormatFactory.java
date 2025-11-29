package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ISOTemplateDateFormatFactory.class */
class ISOTemplateDateFormatFactory extends ISOLikeTemplateDateFormatFactory {
    static final ISOTemplateDateFormatFactory INSTANCE = new ISOTemplateDateFormatFactory();

    private ISOTemplateDateFormatFactory() {
    }

    @Override // freemarker.core.TemplateDateFormatFactory
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new ISOTemplateDateFormat(params, 3, dateType, zonelessInput, timeZone, this, env);
    }
}
