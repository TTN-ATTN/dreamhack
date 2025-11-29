package org.springframework.mail.javamail;

import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailParseException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/MimeMailMessage.class */
public class MimeMailMessage implements MailMessage {
    private final MimeMessageHelper helper;

    public MimeMailMessage(MimeMessageHelper mimeMessageHelper) {
        this.helper = mimeMessageHelper;
    }

    public MimeMailMessage(MimeMessage mimeMessage) {
        this.helper = new MimeMessageHelper(mimeMessage);
    }

    public final MimeMessageHelper getMimeMessageHelper() {
        return this.helper;
    }

    public final MimeMessage getMimeMessage() {
        return this.helper.getMimeMessage();
    }

    @Override // org.springframework.mail.MailMessage
    public void setFrom(String from) throws MailParseException, AddressException {
        try {
            this.helper.setFrom(from);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setReplyTo(String replyTo) throws MailParseException, AddressException {
        try {
            this.helper.setReplyTo(replyTo);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setTo(String to) throws MailParseException, AddressException {
        try {
            this.helper.setTo(to);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setTo(String... to) throws MailParseException, AddressException {
        try {
            this.helper.setTo(to);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setCc(String cc) throws MailParseException, AddressException {
        try {
            this.helper.setCc(cc);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setCc(String... cc) throws MailParseException, AddressException {
        try {
            this.helper.setCc(cc);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setBcc(String bcc) throws MailParseException, AddressException {
        try {
            this.helper.setBcc(bcc);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setBcc(String... bcc) throws MailParseException, AddressException {
        try {
            this.helper.setBcc(bcc);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setSentDate(Date sentDate) throws MailParseException {
        try {
            this.helper.setSentDate(sentDate);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setSubject(String subject) throws MailParseException {
        try {
            this.helper.setSubject(subject);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }

    @Override // org.springframework.mail.MailMessage
    public void setText(String text) throws IllegalStateException, MailParseException {
        try {
            this.helper.setText(text);
        } catch (MessagingException ex) {
            throw new MailParseException((Throwable) ex);
        }
    }
}
