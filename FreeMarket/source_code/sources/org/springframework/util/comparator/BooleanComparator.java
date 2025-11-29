package org.springframework.util.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/comparator/BooleanComparator.class */
public class BooleanComparator implements Comparator<Boolean>, Serializable {
    public static final BooleanComparator TRUE_LOW = new BooleanComparator(true);
    public static final BooleanComparator TRUE_HIGH = new BooleanComparator(false);
    private final boolean trueLow;

    public BooleanComparator(boolean trueLow) {
        this.trueLow = trueLow;
    }

    @Override // java.util.Comparator
    public int compare(Boolean v1, Boolean v2) {
        if (v1.booleanValue() ^ v2.booleanValue()) {
            return v1.booleanValue() ^ this.trueLow ? 1 : -1;
        }
        return 0;
    }

    @Override // java.util.Comparator
    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof BooleanComparator) && this.trueLow == ((BooleanComparator) other).trueLow);
    }

    public int hashCode() {
        return getClass().hashCode() * (this.trueLow ? -1 : 1);
    }

    public String toString() {
        return "BooleanComparator: " + (this.trueLow ? "true low" : "true high");
    }
}
