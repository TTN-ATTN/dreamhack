package org.springframework.util.unit;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/unit/DataSize.class */
public final class DataSize implements Comparable<DataSize>, Serializable {
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_TB = 1099511627776L;
    private final long bytes;

    private DataSize(long bytes) {
        this.bytes = bytes;
    }

    public static DataSize ofBytes(long bytes) {
        return new DataSize(bytes);
    }

    public static DataSize ofKilobytes(long kilobytes) {
        return new DataSize(Math.multiplyExact(kilobytes, 1024L));
    }

    public static DataSize ofMegabytes(long megabytes) {
        return new DataSize(Math.multiplyExact(megabytes, 1048576L));
    }

    public static DataSize ofGigabytes(long gigabytes) {
        return new DataSize(Math.multiplyExact(gigabytes, 1073741824L));
    }

    public static DataSize ofTerabytes(long terabytes) {
        return new DataSize(Math.multiplyExact(terabytes, BYTES_PER_TB));
    }

    public static DataSize of(long amount, DataUnit unit) {
        Assert.notNull(unit, "Unit must not be null");
        return new DataSize(Math.multiplyExact(amount, unit.size().toBytes()));
    }

    public static DataSize parse(CharSequence text) {
        return parse(text, null);
    }

    public static DataSize parse(CharSequence text, @Nullable DataUnit defaultUnit) throws NumberFormatException {
        Assert.notNull(text, "Text must not be null");
        try {
            Matcher matcher = DataSizeUtils.PATTERN.matcher(StringUtils.trimAllWhitespace(text));
            Assert.state(matcher.matches(), "Does not match data size pattern");
            DataUnit unit = DataSizeUtils.determineDataUnit(matcher.group(2), defaultUnit);
            long amount = Long.parseLong(matcher.group(1));
            return of(amount, unit);
        } catch (Exception ex) {
            throw new IllegalArgumentException("'" + ((Object) text) + "' is not a valid data size", ex);
        }
    }

    public boolean isNegative() {
        return this.bytes < 0;
    }

    public long toBytes() {
        return this.bytes;
    }

    public long toKilobytes() {
        return this.bytes / 1024;
    }

    public long toMegabytes() {
        return this.bytes / 1048576;
    }

    public long toGigabytes() {
        return this.bytes / 1073741824;
    }

    public long toTerabytes() {
        return this.bytes / BYTES_PER_TB;
    }

    @Override // java.lang.Comparable
    public int compareTo(DataSize other) {
        return Long.compare(this.bytes, other.bytes);
    }

    public String toString() {
        return String.format("%dB", Long.valueOf(this.bytes));
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DataSize otherSize = (DataSize) other;
        return this.bytes == otherSize.bytes;
    }

    public int hashCode() {
        return Long.hashCode(this.bytes);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/unit/DataSize$DataSizeUtils.class */
    private static class DataSizeUtils {
        private static final Pattern PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

        private DataSizeUtils() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static DataUnit determineDataUnit(String suffix, @Nullable DataUnit defaultUnit) {
            DataUnit defaultUnitToUse = defaultUnit != null ? defaultUnit : DataUnit.BYTES;
            return StringUtils.hasLength(suffix) ? DataUnit.fromSuffix(suffix) : defaultUnitToUse;
        }
    }
}
