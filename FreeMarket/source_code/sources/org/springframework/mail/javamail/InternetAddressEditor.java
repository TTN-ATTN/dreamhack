package org.springframework.mail.javamail;

import java.beans.PropertyEditorSupport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/InternetAddressEditor.class */
public class InternetAddressEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            try {
                setValue(new InternetAddress(text));
                return;
            } catch (AddressException ex) {
                throw new IllegalArgumentException("Could not parse mail address: " + ex.getMessage());
            }
        }
        setValue(null);
    }

    public String getAsText() {
        InternetAddress value = (InternetAddress) getValue();
        return value != null ? value.toUnicodeString() : "";
    }
}
