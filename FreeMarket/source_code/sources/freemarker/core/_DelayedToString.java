package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedToString.class */
public class _DelayedToString extends _DelayedConversionToString {
    public _DelayedToString(Object object) {
        super(object);
    }

    public _DelayedToString(int object) {
        super(Integer.valueOf(object));
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        return String.valueOf(obj);
    }
}
