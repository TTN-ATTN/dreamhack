package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedAOrAn.class */
public class _DelayedAOrAn extends _DelayedConversionToString {
    public _DelayedAOrAn(Object object) {
        super(object);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        String s = obj.toString();
        return _MessageUtil.getAOrAn(s) + " " + s;
    }
}
