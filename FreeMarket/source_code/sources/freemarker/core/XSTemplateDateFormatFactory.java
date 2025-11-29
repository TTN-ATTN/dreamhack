package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/XSTemplateDateFormatFactory.class */
class XSTemplateDateFormatFactory extends ISOLikeTemplateDateFormatFactory {
    static final XSTemplateDateFormatFactory INSTANCE = new XSTemplateDateFormatFactory();

    private XSTemplateDateFormatFactory() {
    }

    @Override // freemarker.core.TemplateDateFormatFactory
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new XSTemplateDateFormat(params, 2, dateType, zonelessInput, timeZone, this, env);
    }
}
