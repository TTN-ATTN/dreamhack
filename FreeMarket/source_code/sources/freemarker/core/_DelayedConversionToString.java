package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedConversionToString.class */
public abstract class _DelayedConversionToString {
    private static final String NOT_SET = new String();
    private Object object;
    private volatile String stringValue = NOT_SET;

    protected abstract String doConversion(Object obj);

    public _DelayedConversionToString(Object object) {
        this.object = object;
    }

    public String toString() {
        String stringValue = this.stringValue;
        if (stringValue == NOT_SET) {
            synchronized (this) {
                stringValue = this.stringValue;
                if (stringValue == NOT_SET) {
                    stringValue = doConversion(this.object);
                    this.stringValue = stringValue;
                    this.object = null;
                }
            }
        }
        return stringValue;
    }
}
