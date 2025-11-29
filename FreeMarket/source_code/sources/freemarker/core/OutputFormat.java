package freemarker.core;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/OutputFormat.class */
public abstract class OutputFormat {
    public abstract String getName();

    public abstract String getMimeType();

    public abstract boolean isOutputFormatMixingAllowed();

    public final String toString() {
        String extras = toStringExtraProperties();
        return getName() + "(mimeType=" + StringUtil.jQuote(getMimeType()) + ", class=" + ClassUtil.getShortClassNameOfObject(this, true) + (extras.length() != 0 ? ", " : "") + extras + ")";
    }

    protected String toStringExtraProperties() {
        return "";
    }
}
