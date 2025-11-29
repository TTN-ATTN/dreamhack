package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedOrdinal.class */
public class _DelayedOrdinal extends _DelayedConversionToString {
    public _DelayedOrdinal(Object object) {
        super(object);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        if (obj instanceof Number) {
            long n = ((Number) obj).longValue();
            if (n % 10 == 1 && n % 100 != 11) {
                return n + "st";
            }
            if (n % 10 == 2 && n % 100 != 12) {
                return n + "nd";
            }
            if (n % 10 == 3 && n % 100 != 13) {
                return n + "rd";
            }
            return n + "th";
        }
        return "" + obj;
    }
}
