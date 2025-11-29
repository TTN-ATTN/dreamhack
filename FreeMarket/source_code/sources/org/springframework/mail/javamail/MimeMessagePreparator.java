package org.springframework.mail.javamail;

import javax.mail.internet.MimeMessage;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/MimeMessagePreparator.class */
public interface MimeMessagePreparator {
    void prepare(MimeMessage mimeMessage) throws Exception;
}
