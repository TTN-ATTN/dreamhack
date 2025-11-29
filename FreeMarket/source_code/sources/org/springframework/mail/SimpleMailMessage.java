package org.springframework.mail;

import java.io.Serializable;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/SimpleMailMessage.class */
public class SimpleMailMessage implements MailMessage, Serializable {

    @Nullable
    private String from;

    @Nullable
    private String replyTo;

    @Nullable
    private String[] to;

    @Nullable
    private String[] cc;

    @Nullable
    private String[] bcc;

    @Nullable
    private Date sentDate;

    @Nullable
    private String subject;

    @Nullable
    private String text;

    public SimpleMailMessage() {
    }

    public SimpleMailMessage(SimpleMailMessage original) {
        Assert.notNull(original, "'original' message argument must not be null");
        this.from = original.getFrom();
        this.replyTo = original.getReplyTo();
        this.to = copyOrNull(original.getTo());
        this.cc = copyOrNull(original.getCc());
        this.bcc = copyOrNull(original.getBcc());
        this.sentDate = original.getSentDate();
        this.subject = original.getSubject();
        this.text = original.getText();
    }

    @Override // org.springframework.mail.MailMessage
    public void setFrom(@Nullable String from) {
        this.from = from;
    }

    @Nullable
    public String getFrom() {
        return this.from;
    }

    @Override // org.springframework.mail.MailMessage
    public void setReplyTo(@Nullable String replyTo) {
        this.replyTo = replyTo;
    }

    @Nullable
    public String getReplyTo() {
        return this.replyTo;
    }

    @Override // org.springframework.mail.MailMessage
    public void setTo(@Nullable String to) {
        this.to = new String[]{to};
    }

    @Override // org.springframework.mail.MailMessage
    public void setTo(String... to) {
        this.to = to;
    }

    @Nullable
    public String[] getTo() {
        return this.to;
    }

    @Override // org.springframework.mail.MailMessage
    public void setCc(@Nullable String cc) {
        this.cc = new String[]{cc};
    }

    @Override // org.springframework.mail.MailMessage
    public void setCc(@Nullable String... cc) {
        this.cc = cc;
    }

    @Nullable
    public String[] getCc() {
        return this.cc;
    }

    @Override // org.springframework.mail.MailMessage
    public void setBcc(@Nullable String bcc) {
        this.bcc = new String[]{bcc};
    }

    @Override // org.springframework.mail.MailMessage
    public void setBcc(@Nullable String... bcc) {
        this.bcc = bcc;
    }

    @Nullable
    public String[] getBcc() {
        return this.bcc;
    }

    @Override // org.springframework.mail.MailMessage
    public void setSentDate(@Nullable Date sentDate) {
        this.sentDate = sentDate;
    }

    @Nullable
    public Date getSentDate() {
        return this.sentDate;
    }

    @Override // org.springframework.mail.MailMessage
    public void setSubject(@Nullable String subject) {
        this.subject = subject;
    }

    @Nullable
    public String getSubject() {
        return this.subject;
    }

    @Override // org.springframework.mail.MailMessage
    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public String getText() {
        return this.text;
    }

    public void copyTo(MailMessage target) throws MailParseException {
        Assert.notNull(target, "'target' MailMessage must not be null");
        if (getFrom() != null) {
            target.setFrom(getFrom());
        }
        if (getReplyTo() != null) {
            target.setReplyTo(getReplyTo());
        }
        if (getTo() != null) {
            target.setTo(copy(getTo()));
        }
        if (getCc() != null) {
            target.setCc(copy(getCc()));
        }
        if (getBcc() != null) {
            target.setBcc(copy(getBcc()));
        }
        if (getSentDate() != null) {
            target.setSentDate(getSentDate());
        }
        if (getSubject() != null) {
            target.setSubject(getSubject());
        }
        if (getText() != null) {
            target.setText(getText());
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SimpleMailMessage)) {
            return false;
        }
        SimpleMailMessage otherMessage = (SimpleMailMessage) other;
        return ObjectUtils.nullSafeEquals(this.from, otherMessage.from) && ObjectUtils.nullSafeEquals(this.replyTo, otherMessage.replyTo) && ObjectUtils.nullSafeEquals(this.to, otherMessage.to) && ObjectUtils.nullSafeEquals(this.cc, otherMessage.cc) && ObjectUtils.nullSafeEquals(this.bcc, otherMessage.bcc) && ObjectUtils.nullSafeEquals(this.sentDate, otherMessage.sentDate) && ObjectUtils.nullSafeEquals(this.subject, otherMessage.subject) && ObjectUtils.nullSafeEquals(this.text, otherMessage.text);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.from);
        return (29 * ((29 * ((29 * ((29 * ((29 * ((29 * hashCode) + ObjectUtils.nullSafeHashCode(this.replyTo))) + ObjectUtils.nullSafeHashCode((Object[]) this.to))) + ObjectUtils.nullSafeHashCode((Object[]) this.cc))) + ObjectUtils.nullSafeHashCode((Object[]) this.bcc))) + ObjectUtils.nullSafeHashCode(this.sentDate))) + ObjectUtils.nullSafeHashCode(this.subject);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleMailMessage: ");
        sb.append("from=").append(this.from).append("; ");
        sb.append("replyTo=").append(this.replyTo).append("; ");
        sb.append("to=").append(StringUtils.arrayToCommaDelimitedString(this.to)).append("; ");
        sb.append("cc=").append(StringUtils.arrayToCommaDelimitedString(this.cc)).append("; ");
        sb.append("bcc=").append(StringUtils.arrayToCommaDelimitedString(this.bcc)).append("; ");
        sb.append("sentDate=").append(this.sentDate).append("; ");
        sb.append("subject=").append(this.subject).append("; ");
        sb.append("text=").append(this.text);
        return sb.toString();
    }

    @Nullable
    private static String[] copyOrNull(@Nullable String[] state) {
        if (state == null) {
            return null;
        }
        return copy(state);
    }

    private static String[] copy(String[] state) {
        return (String[]) state.clone();
    }
}
