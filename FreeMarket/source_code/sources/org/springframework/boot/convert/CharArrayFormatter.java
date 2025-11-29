package org.springframework.boot.convert;

import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/convert/CharArrayFormatter.class */
final class CharArrayFormatter implements Formatter<char[]> {
    CharArrayFormatter() {
    }

    @Override // org.springframework.format.Printer
    public String print(char[] object, Locale locale) {
        return new String(object);
    }

    @Override // org.springframework.format.Parser
    public char[] parse(String text, Locale locale) throws ParseException {
        return text.toCharArray();
    }
}
