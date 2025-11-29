package freemarker.template;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleScalar.class */
public final class SimpleScalar implements TemplateScalarModel, Serializable {
    private final String value;

    public SimpleScalar(String value) {
        this.value = value;
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return this.value == null ? "" : this.value;
    }

    public String toString() {
        return this.value;
    }

    public static SimpleScalar newInstanceOrNull(String s) {
        if (s != null) {
            return new SimpleScalar(s);
        }
        return null;
    }
}
