package org.springframework.scheduling.support;

import java.time.temporal.Temporal;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/support/CompositeCronField.class */
final class CompositeCronField extends CronField {
    private final CronField[] fields;
    private final String value;

    private CompositeCronField(CronField.Type type, CronField[] fields, String value) {
        super(type);
        this.fields = fields;
        this.value = value;
    }

    public static CronField compose(CronField[] fields, CronField.Type type, String value) {
        Assert.notEmpty(fields, "Fields must not be empty");
        Assert.hasLength(value, "Value must not be empty");
        if (fields.length == 1) {
            return fields[0];
        }
        return new CompositeCronField(type, fields, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [java.time.temporal.Temporal] */
    @Override // org.springframework.scheduling.support.CronField
    @Nullable
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        T result = null;
        for (CronField field : this.fields) {
            ?? NextOrSame = field.nextOrSame(temporal);
            if (result == null || (NextOrSame != 0 && ((Comparable) NextOrSame).compareTo(result) < 0)) {
                result = NextOrSame;
            }
        }
        return result;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeCronField)) {
            return false;
        }
        CompositeCronField other = (CompositeCronField) o;
        return type() == other.type() && this.value.equals(other.value);
    }

    public String toString() {
        return type() + " '" + this.value + "'";
    }
}
