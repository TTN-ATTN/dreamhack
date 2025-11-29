package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedJoinWithComma.class */
public class _DelayedJoinWithComma extends _DelayedConversionToString {
    public _DelayedJoinWithComma(String[] items) {
        super(items);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        String[] items = (String[]) obj;
        int totalLength = 0;
        for (int i = 0; i < items.length; i++) {
            if (i != 0) {
                totalLength += 2;
            }
            totalLength += items[i].length();
        }
        StringBuilder sb = new StringBuilder(totalLength);
        for (int i2 = 0; i2 < items.length; i2++) {
            if (i2 != 0) {
                sb.append(", ");
            }
            sb.append(items[i2]);
        }
        return sb.toString();
    }
}
