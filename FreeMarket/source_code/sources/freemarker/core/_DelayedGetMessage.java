package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedGetMessage.class */
public class _DelayedGetMessage extends _DelayedConversionToString {
    public _DelayedGetMessage(Throwable exception) {
        super(exception);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        String message = ((Throwable) obj).getMessage();
        return (message == null || message.length() == 0) ? "[No exception message]" : message;
    }
}
