package freemarker.ext.beans;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/CharacterOrString.class */
final class CharacterOrString {
    private final String stringValue;

    CharacterOrString(String stringValue) {
        this.stringValue = stringValue;
    }

    String getAsString() {
        return this.stringValue;
    }

    char getAsChar() {
        return this.stringValue.charAt(0);
    }
}
