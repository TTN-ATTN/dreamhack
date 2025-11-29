package freemarker.template;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/SimpleNumber.class */
public final class SimpleNumber implements TemplateNumberModel, Serializable {
    private final Number value;

    public SimpleNumber(Number value) {
        this.value = value;
    }

    public SimpleNumber(byte val) {
        this.value = Byte.valueOf(val);
    }

    public SimpleNumber(short val) {
        this.value = Short.valueOf(val);
    }

    public SimpleNumber(int val) {
        this.value = Integer.valueOf(val);
    }

    public SimpleNumber(long val) {
        this.value = Long.valueOf(val);
    }

    public SimpleNumber(float val) {
        this.value = Float.valueOf(val);
    }

    public SimpleNumber(double val) {
        this.value = Double.valueOf(val);
    }

    @Override // freemarker.template.TemplateNumberModel
    public Number getAsNumber() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}
