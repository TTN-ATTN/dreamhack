package freemarker.template;

import freemarker.template.utility.StringUtil;
import java.io.Serializable;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Version.class */
public final class Version implements Serializable {
    private final int major;
    private final int minor;
    private final int micro;
    private final String extraInfo;
    private final String originalStringValue;
    private final Boolean gaeCompliant;
    private final Date buildDate;
    private final int intValue;
    private volatile String calculatedStringValue;
    private int hashCode;

    public Version(String stringValue) {
        this(stringValue, (Boolean) null, (Date) null);
    }

    public Version(String stringValue, Boolean gaeCompliant, Date buildDate) {
        char firstChar;
        String stringValue2 = stringValue.trim();
        this.originalStringValue = stringValue2;
        int[] parts = new int[3];
        String extraInfoTmp = null;
        int partIdx = 0;
        int i = 0;
        while (true) {
            if (i >= stringValue2.length()) {
                break;
            }
            char c = stringValue2.charAt(i);
            if (isNumber(c)) {
                parts[partIdx] = (parts[partIdx] * 10) + (c - '0');
            } else {
                if (i == 0) {
                    throw new IllegalArgumentException("The version number string " + StringUtil.jQuote(stringValue2) + " doesn't start with a number.");
                }
                if (c == '.') {
                    char nextC = i + 1 >= stringValue2.length() ? (char) 0 : stringValue2.charAt(i + 1);
                    if (nextC == '.') {
                        throw new IllegalArgumentException("The version number string " + StringUtil.jQuote(stringValue2) + " contains multiple dots after a number.");
                    }
                    if (partIdx == 2 || !isNumber(nextC)) {
                        break;
                    } else {
                        partIdx++;
                    }
                } else {
                    extraInfoTmp = stringValue2.substring(i);
                    break;
                }
            }
            i++;
        }
        extraInfoTmp = stringValue2.substring(i);
        if (extraInfoTmp != null && ((firstChar = extraInfoTmp.charAt(0)) == '.' || firstChar == '-' || firstChar == '_')) {
            extraInfoTmp = extraInfoTmp.substring(1);
            if (extraInfoTmp.length() == 0) {
                throw new IllegalArgumentException("The version number string " + StringUtil.jQuote(stringValue2) + " has an extra info section opened with \"" + firstChar + "\", but it's empty.");
            }
        }
        this.extraInfo = extraInfoTmp;
        this.major = parts[0];
        this.minor = parts[1];
        this.micro = parts[2];
        this.intValue = calculateIntValue();
        this.gaeCompliant = gaeCompliant;
        this.buildDate = buildDate;
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    public Version(int major, int minor, int micro) {
        this(major, minor, micro, null, null, null);
    }

    public Version(int intValue) {
        this.intValue = intValue;
        this.micro = intValue % 1000;
        this.minor = (intValue / 1000) % 1000;
        this.major = intValue / 1000000;
        this.extraInfo = null;
        this.gaeCompliant = null;
        this.buildDate = null;
        this.originalStringValue = null;
    }

    public Version(int major, int minor, int micro, String extraInfo, Boolean gaeCompatible, Date buildDate) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.extraInfo = extraInfo;
        this.gaeCompliant = gaeCompatible;
        this.buildDate = buildDate;
        this.intValue = calculateIntValue();
        this.originalStringValue = null;
    }

    private int calculateIntValue() {
        return intValueFor(this.major, this.minor, this.micro);
    }

    public static int intValueFor(int major, int minor, int micro) {
        return (major * 1000000) + (minor * 1000) + micro;
    }

    private String getStringValue() {
        if (this.originalStringValue != null) {
            return this.originalStringValue;
        }
        String calculatedStringValue = this.calculatedStringValue;
        if (calculatedStringValue == null) {
            synchronized (this) {
                calculatedStringValue = this.calculatedStringValue;
                if (calculatedStringValue == null) {
                    calculatedStringValue = this.major + "." + this.minor + "." + this.micro;
                    if (this.extraInfo != null) {
                        calculatedStringValue = calculatedStringValue + "-" + this.extraInfo;
                    }
                    this.calculatedStringValue = calculatedStringValue;
                }
            }
        }
        return calculatedStringValue;
    }

    public String toString() {
        return getStringValue();
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getMicro() {
        return this.micro;
    }

    public String getExtraInfo() {
        return this.extraInfo;
    }

    public Boolean isGAECompliant() {
        return this.gaeCompliant;
    }

    public Date getBuildDate() {
        return this.buildDate;
    }

    public int intValue() {
        return this.intValue;
    }

    public int hashCode() {
        int i;
        int r = this.hashCode;
        if (r != 0) {
            return r;
        }
        synchronized (this) {
            if (this.hashCode == 0) {
                int result = (31 * ((31 * ((31 * ((31 * 1) + (this.buildDate == null ? 0 : this.buildDate.hashCode()))) + (this.extraInfo == null ? 0 : this.extraInfo.hashCode()))) + (this.gaeCompliant == null ? 0 : this.gaeCompliant.hashCode()))) + this.intValue;
                if (result == 0) {
                    result = -1;
                }
                this.hashCode = result;
            }
            i = this.hashCode;
        }
        return i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Version other = (Version) obj;
        if (this.intValue != other.intValue || other.hashCode() != hashCode()) {
            return false;
        }
        if (this.buildDate == null) {
            if (other.buildDate != null) {
                return false;
            }
        } else if (!this.buildDate.equals(other.buildDate)) {
            return false;
        }
        if (this.extraInfo == null) {
            if (other.extraInfo != null) {
                return false;
            }
        } else if (!this.extraInfo.equals(other.extraInfo)) {
            return false;
        }
        if (this.gaeCompliant == null) {
            return other.gaeCompliant == null;
        }
        if (!this.gaeCompliant.equals(other.gaeCompliant)) {
            return false;
        }
        return true;
    }
}
