package org.springframework.mail.javamail;

import javax.activation.FileTypeMap;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/SmartMimeMessage.class */
class SmartMimeMessage extends MimeMessage {

    @Nullable
    private final String defaultEncoding;

    @Nullable
    private final FileTypeMap defaultFileTypeMap;

    public SmartMimeMessage(Session session, @Nullable String defaultEncoding, @Nullable FileTypeMap defaultFileTypeMap) {
        super(session);
        this.defaultEncoding = defaultEncoding;
        this.defaultFileTypeMap = defaultFileTypeMap;
    }

    @Nullable
    public final String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    @Nullable
    public final FileTypeMap getDefaultFileTypeMap() {
        return this.defaultFileTypeMap;
    }
}
