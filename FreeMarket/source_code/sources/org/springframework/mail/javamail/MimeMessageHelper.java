package org.springframework.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/MimeMessageHelper.class */
public class MimeMessageHelper {
    public static final int MULTIPART_MODE_NO = 0;
    public static final int MULTIPART_MODE_MIXED = 1;
    public static final int MULTIPART_MODE_RELATED = 2;
    public static final int MULTIPART_MODE_MIXED_RELATED = 3;
    private static final String MULTIPART_SUBTYPE_MIXED = "mixed";
    private static final String MULTIPART_SUBTYPE_RELATED = "related";
    private static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";
    private static final String CONTENT_TYPE_ALTERNATIVE = "text/alternative";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
    private static final String HEADER_PRIORITY = "X-Priority";
    private final MimeMessage mimeMessage;

    @Nullable
    private MimeMultipart rootMimeMultipart;

    @Nullable
    private MimeMultipart mimeMultipart;

    @Nullable
    private final String encoding;
    private FileTypeMap fileTypeMap;
    private boolean encodeFilenames;
    private boolean validateAddresses;

    public MimeMessageHelper(MimeMessage mimeMessage) {
        this(mimeMessage, (String) null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, @Nullable String encoding) {
        this.encodeFilenames = false;
        this.validateAddresses = false;
        this.mimeMessage = mimeMessage;
        this.encoding = encoding != null ? encoding : getDefaultEncoding(mimeMessage);
        this.fileTypeMap = getDefaultFileTypeMap(mimeMessage);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart) throws MessagingException {
        this(mimeMessage, multipart, (String) null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart, @Nullable String encoding) throws MessagingException {
        this(mimeMessage, multipart ? 3 : 0, encoding);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
        this(mimeMessage, multipartMode, (String) null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode, @Nullable String encoding) throws MessagingException {
        this.encodeFilenames = false;
        this.validateAddresses = false;
        this.mimeMessage = mimeMessage;
        createMimeMultiparts(mimeMessage, multipartMode);
        this.encoding = encoding != null ? encoding : getDefaultEncoding(mimeMessage);
        this.fileTypeMap = getDefaultFileTypeMap(mimeMessage);
    }

    public final MimeMessage getMimeMessage() {
        return this.mimeMessage;
    }

    protected void createMimeMultiparts(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
        switch (multipartMode) {
            case 0:
                setMimeMultiparts(null, null);
                return;
            case 1:
                MimeMultipart mixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
                mimeMessage.setContent(mixedMultipart);
                setMimeMultiparts(mixedMultipart, mixedMultipart);
                return;
            case 2:
                MimeMultipart relatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
                mimeMessage.setContent(relatedMultipart);
                setMimeMultiparts(relatedMultipart, relatedMultipart);
                return;
            case 3:
                MimeMultipart rootMixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
                mimeMessage.setContent(rootMixedMultipart);
                MimeMultipart nestedRelatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
                MimeBodyPart relatedBodyPart = new MimeBodyPart();
                relatedBodyPart.setContent(nestedRelatedMultipart);
                rootMixedMultipart.addBodyPart(relatedBodyPart);
                setMimeMultiparts(rootMixedMultipart, nestedRelatedMultipart);
                return;
            default:
                throw new IllegalArgumentException("Only multipart modes MIXED_RELATED, RELATED and NO supported");
        }
    }

    protected final void setMimeMultiparts(@Nullable MimeMultipart root, @Nullable MimeMultipart main) {
        this.rootMimeMultipart = root;
        this.mimeMultipart = main;
    }

    public final boolean isMultipart() {
        return this.rootMimeMultipart != null;
    }

    public final MimeMultipart getRootMimeMultipart() throws IllegalStateException {
        if (this.rootMimeMultipart == null) {
            throw new IllegalStateException("Not in multipart mode - create an appropriate MimeMessageHelper via a constructor that takes a 'multipart' flag if you need to set alternative texts or add inline elements or attachments.");
        }
        return this.rootMimeMultipart;
    }

    public final MimeMultipart getMimeMultipart() throws IllegalStateException {
        if (this.mimeMultipart == null) {
            throw new IllegalStateException("Not in multipart mode - create an appropriate MimeMessageHelper via a constructor that takes a 'multipart' flag if you need to set alternative texts or add inline elements or attachments.");
        }
        return this.mimeMultipart;
    }

    @Nullable
    protected String getDefaultEncoding(MimeMessage mimeMessage) {
        if (mimeMessage instanceof SmartMimeMessage) {
            return ((SmartMimeMessage) mimeMessage).getDefaultEncoding();
        }
        return null;
    }

    @Nullable
    public String getEncoding() {
        return this.encoding;
    }

    protected FileTypeMap getDefaultFileTypeMap(MimeMessage mimeMessage) {
        FileTypeMap fileTypeMap;
        if ((mimeMessage instanceof SmartMimeMessage) && (fileTypeMap = ((SmartMimeMessage) mimeMessage).getDefaultFileTypeMap()) != null) {
            return fileTypeMap;
        }
        ConfigurableMimeFileTypeMap fileTypeMap2 = new ConfigurableMimeFileTypeMap();
        fileTypeMap2.afterPropertiesSet();
        return fileTypeMap2;
    }

    public void setFileTypeMap(@Nullable FileTypeMap fileTypeMap) {
        this.fileTypeMap = fileTypeMap != null ? fileTypeMap : getDefaultFileTypeMap(getMimeMessage());
    }

    public FileTypeMap getFileTypeMap() {
        return this.fileTypeMap;
    }

    public void setEncodeFilenames(boolean encodeFilenames) {
        this.encodeFilenames = encodeFilenames;
    }

    public boolean isEncodeFilenames() {
        return this.encodeFilenames;
    }

    public void setValidateAddresses(boolean validateAddresses) {
        this.validateAddresses = validateAddresses;
    }

    public boolean isValidateAddresses() {
        return this.validateAddresses;
    }

    protected void validateAddress(InternetAddress address) throws AddressException {
        if (isValidateAddresses()) {
            address.validate();
        }
    }

    protected void validateAddresses(InternetAddress[] addresses) throws AddressException {
        for (InternetAddress address : addresses) {
            validateAddress(address);
        }
    }

    public void setFrom(InternetAddress from) throws AddressException, MessagingException {
        Assert.notNull(from, "From address must not be null");
        validateAddress(from);
        this.mimeMessage.setFrom(from);
    }

    public void setFrom(String from) throws AddressException, MessagingException {
        Assert.notNull(from, "From address must not be null");
        setFrom(parseAddress(from));
    }

    public void setFrom(String from, String personal) throws AddressException, MessagingException, UnsupportedEncodingException {
        Assert.notNull(from, "From address must not be null");
        setFrom(getEncoding() != null ? new InternetAddress(from, personal, getEncoding()) : new InternetAddress(from, personal));
    }

    public void setReplyTo(InternetAddress replyTo) throws AddressException, MessagingException {
        Assert.notNull(replyTo, "Reply-to address must not be null");
        validateAddress(replyTo);
        this.mimeMessage.setReplyTo(new InternetAddress[]{replyTo});
    }

    public void setReplyTo(String replyTo) throws AddressException, MessagingException {
        Assert.notNull(replyTo, "Reply-to address must not be null");
        setReplyTo(parseAddress(replyTo));
    }

    public void setReplyTo(String replyTo, String personal) throws AddressException, MessagingException, UnsupportedEncodingException {
        Assert.notNull(replyTo, "Reply-to address must not be null");
        InternetAddress replyToAddress = getEncoding() != null ? new InternetAddress(replyTo, personal, getEncoding()) : new InternetAddress(replyTo, personal);
        setReplyTo(replyToAddress);
    }

    public void setTo(InternetAddress to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address must not be null");
        validateAddress(to);
        this.mimeMessage.setRecipient(Message.RecipientType.TO, to);
    }

    public void setTo(InternetAddress[] to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address array must not be null");
        validateAddresses(to);
        this.mimeMessage.setRecipients(Message.RecipientType.TO, to);
    }

    public void setTo(String to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address must not be null");
        setTo(parseAddress(to));
    }

    public void setTo(String[] to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address array must not be null");
        InternetAddress[] addresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            addresses[i] = parseAddress(to[i]);
        }
        setTo(addresses);
    }

    public void addTo(InternetAddress to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address must not be null");
        validateAddress(to);
        this.mimeMessage.addRecipient(Message.RecipientType.TO, to);
    }

    public void addTo(String to) throws AddressException, MessagingException {
        Assert.notNull(to, "To address must not be null");
        addTo(parseAddress(to));
    }

    public void addTo(String to, String personal) throws AddressException, MessagingException, UnsupportedEncodingException {
        Assert.notNull(to, "To address must not be null");
        addTo(getEncoding() != null ? new InternetAddress(to, personal, getEncoding()) : new InternetAddress(to, personal));
    }

    public void setCc(InternetAddress cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address must not be null");
        validateAddress(cc);
        this.mimeMessage.setRecipient(Message.RecipientType.CC, cc);
    }

    public void setCc(InternetAddress[] cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address array must not be null");
        validateAddresses(cc);
        this.mimeMessage.setRecipients(Message.RecipientType.CC, cc);
    }

    public void setCc(String cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address must not be null");
        setCc(parseAddress(cc));
    }

    public void setCc(String[] cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address array must not be null");
        InternetAddress[] addresses = new InternetAddress[cc.length];
        for (int i = 0; i < cc.length; i++) {
            addresses[i] = parseAddress(cc[i]);
        }
        setCc(addresses);
    }

    public void addCc(InternetAddress cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address must not be null");
        validateAddress(cc);
        this.mimeMessage.addRecipient(Message.RecipientType.CC, cc);
    }

    public void addCc(String cc) throws AddressException, MessagingException {
        Assert.notNull(cc, "Cc address must not be null");
        addCc(parseAddress(cc));
    }

    public void addCc(String cc, String personal) throws AddressException, MessagingException, UnsupportedEncodingException {
        Assert.notNull(cc, "Cc address must not be null");
        addCc(getEncoding() != null ? new InternetAddress(cc, personal, getEncoding()) : new InternetAddress(cc, personal));
    }

    public void setBcc(InternetAddress bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address must not be null");
        validateAddress(bcc);
        this.mimeMessage.setRecipient(Message.RecipientType.BCC, bcc);
    }

    public void setBcc(InternetAddress[] bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address array must not be null");
        validateAddresses(bcc);
        this.mimeMessage.setRecipients(Message.RecipientType.BCC, bcc);
    }

    public void setBcc(String bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address must not be null");
        setBcc(parseAddress(bcc));
    }

    public void setBcc(String[] bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address array must not be null");
        InternetAddress[] addresses = new InternetAddress[bcc.length];
        for (int i = 0; i < bcc.length; i++) {
            addresses[i] = parseAddress(bcc[i]);
        }
        setBcc(addresses);
    }

    public void addBcc(InternetAddress bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address must not be null");
        validateAddress(bcc);
        this.mimeMessage.addRecipient(Message.RecipientType.BCC, bcc);
    }

    public void addBcc(String bcc) throws AddressException, MessagingException {
        Assert.notNull(bcc, "Bcc address must not be null");
        addBcc(parseAddress(bcc));
    }

    public void addBcc(String bcc, String personal) throws AddressException, MessagingException, UnsupportedEncodingException {
        Assert.notNull(bcc, "Bcc address must not be null");
        addBcc(getEncoding() != null ? new InternetAddress(bcc, personal, getEncoding()) : new InternetAddress(bcc, personal));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.mail.MessagingException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.mail.internet.AddressException */
    private InternetAddress parseAddress(String address) throws AddressException, MessagingException {
        InternetAddress[] parsed = InternetAddress.parse(address);
        if (parsed.length != 1) {
            throw new AddressException("Illegal address", address);
        }
        InternetAddress raw = parsed[0];
        try {
            return getEncoding() != null ? new InternetAddress(raw.getAddress(), raw.getPersonal(), getEncoding()) : raw;
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to parse embedded personal name to correct encoding", ex);
        }
    }

    public void setPriority(int priority) throws MessagingException {
        this.mimeMessage.setHeader(HEADER_PRIORITY, Integer.toString(priority));
    }

    public void setSentDate(Date sentDate) throws MessagingException {
        Assert.notNull(sentDate, "Sent date must not be null");
        this.mimeMessage.setSentDate(sentDate);
    }

    public void setSubject(String subject) throws MessagingException {
        Assert.notNull(subject, "Subject must not be null");
        if (getEncoding() != null) {
            this.mimeMessage.setSubject(subject, getEncoding());
        } else {
            this.mimeMessage.setSubject(subject);
        }
    }

    public void setText(String text) throws IllegalStateException, MessagingException {
        setText(text, false);
    }

    public void setText(String text, boolean html) throws IllegalStateException, MessagingException {
        MimeBodyPart mainPart;
        Assert.notNull(text, "Text must not be null");
        if (isMultipart()) {
            mainPart = getMainPart();
        } else {
            mainPart = this.mimeMessage;
        }
        if (html) {
            setHtmlTextToMimePart(mainPart, text);
        } else {
            setPlainTextToMimePart(mainPart, text);
        }
    }

    public void setText(String plainText, String htmlText) throws MessagingException {
        Assert.notNull(plainText, "Plain text must not be null");
        Assert.notNull(htmlText, "HTML text must not be null");
        MimeMultipart messageBody = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
        getMainPart().setContent(messageBody, CONTENT_TYPE_ALTERNATIVE);
        MimeBodyPart plainTextPart = new MimeBodyPart();
        setPlainTextToMimePart(plainTextPart, plainText);
        messageBody.addBodyPart(plainTextPart);
        MimeBodyPart htmlTextPart = new MimeBodyPart();
        setHtmlTextToMimePart(htmlTextPart, htmlText);
        messageBody.addBodyPart(htmlTextPart);
    }

    private MimeBodyPart getMainPart() throws IllegalStateException, MessagingException {
        MimeMultipart mimeMultipart = getMimeMultipart();
        MimeBodyPart bodyPart = null;
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bp = mimeMultipart.getBodyPart(i);
            if (bp.getFileName() == null) {
                bodyPart = (MimeBodyPart) bp;
            }
        }
        if (bodyPart == null) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeMultipart.addBodyPart(mimeBodyPart);
            bodyPart = mimeBodyPart;
        }
        return bodyPart;
    }

    private void setPlainTextToMimePart(MimePart mimePart, String text) throws MessagingException {
        if (getEncoding() != null) {
            mimePart.setText(text, getEncoding());
        } else {
            mimePart.setText(text);
        }
    }

    private void setHtmlTextToMimePart(MimePart mimePart, String text) throws MessagingException {
        if (getEncoding() != null) {
            mimePart.setContent(text, "text/html;charset=" + getEncoding());
        } else {
            mimePart.setContent(text, "text/html");
        }
    }

    public void addInline(String contentId, DataSource dataSource) throws MessagingException {
        Assert.notNull(contentId, "Content ID must not be null");
        Assert.notNull(dataSource, "DataSource must not be null");
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDisposition("inline");
        mimeBodyPart.setContentID("<" + contentId + ">");
        mimeBodyPart.setDataHandler(new DataHandler(dataSource));
        getMimeMultipart().addBodyPart(mimeBodyPart);
    }

    public void addInline(String contentId, File file) throws MessagingException {
        Assert.notNull(file, "File must not be null");
        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(getFileTypeMap());
        addInline(contentId, (DataSource) dataSource);
    }

    public void addInline(String contentId, Resource resource) throws MessagingException {
        Assert.notNull(resource, "Resource must not be null");
        String contentType = getFileTypeMap().getContentType(resource.getFilename());
        addInline(contentId, resource, contentType);
    }

    public void addInline(String contentId, InputStreamSource inputStreamSource, String contentType) throws MessagingException {
        Assert.notNull(inputStreamSource, "InputStreamSource must not be null");
        if ((inputStreamSource instanceof Resource) && ((Resource) inputStreamSource).isOpen()) {
            throw new IllegalArgumentException("Passed-in Resource contains an open stream: invalid argument. JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
        }
        DataSource dataSource = createDataSource(inputStreamSource, contentType, "inline");
        addInline(contentId, dataSource);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.mail.MessagingException */
    public void addAttachment(String attachmentFilename, DataSource dataSource) throws MessagingException {
        Assert.notNull(attachmentFilename, "Attachment filename must not be null");
        Assert.notNull(dataSource, "DataSource must not be null");
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setDisposition(FileUploadBase.ATTACHMENT);
            mimeBodyPart.setFileName(isEncodeFilenames() ? MimeUtility.encodeText(attachmentFilename) : attachmentFilename);
            mimeBodyPart.setDataHandler(new DataHandler(dataSource));
            getRootMimeMultipart().addBodyPart(mimeBodyPart);
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to encode attachment filename", ex);
        }
    }

    public void addAttachment(String attachmentFilename, File file) throws MessagingException {
        Assert.notNull(file, "File must not be null");
        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(getFileTypeMap());
        addAttachment(attachmentFilename, (DataSource) dataSource);
    }

    public void addAttachment(String attachmentFilename, InputStreamSource inputStreamSource) throws MessagingException {
        String contentType = getFileTypeMap().getContentType(attachmentFilename);
        addAttachment(attachmentFilename, inputStreamSource, contentType);
    }

    public void addAttachment(String attachmentFilename, InputStreamSource inputStreamSource, String contentType) throws MessagingException {
        Assert.notNull(inputStreamSource, "InputStreamSource must not be null");
        if ((inputStreamSource instanceof Resource) && ((Resource) inputStreamSource).isOpen()) {
            throw new IllegalArgumentException("Passed-in Resource contains an open stream: invalid argument. JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
        }
        DataSource dataSource = createDataSource(inputStreamSource, contentType, attachmentFilename);
        addAttachment(attachmentFilename, dataSource);
    }

    protected DataSource createDataSource(final InputStreamSource inputStreamSource, final String contentType, final String name) {
        return new DataSource() { // from class: org.springframework.mail.javamail.MimeMessageHelper.1
            public InputStream getInputStream() throws IOException {
                return inputStreamSource.getInputStream();
            }

            public OutputStream getOutputStream() {
                throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
            }

            public String getContentType() {
                return contentType;
            }

            public String getName() {
                return name;
            }
        };
    }
}
