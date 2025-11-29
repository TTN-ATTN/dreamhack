package freemarker.core;

import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedJQuote.class */
public class _DelayedJQuote extends _DelayedConversionToString {
    public _DelayedJQuote(Object object) {
        super(object);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        return StringUtil.jQuote(_ErrorDescriptionBuilder.toString(obj));
    }
}
