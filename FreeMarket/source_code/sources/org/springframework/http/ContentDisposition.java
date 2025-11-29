package org.springframework.http;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ContentDisposition.class */
public final class ContentDisposition {
    private static final Pattern BASE64_ENCODED_PATTERN = Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?B\\?([+/0-9a-zA-Z]+=*)\\?=");
    private static final String INVALID_HEADER_FIELD_PARAMETER_FORMAT = "Invalid header field parameter format (as defined in RFC 5987)";

    @Nullable
    private final String type;

    @Nullable
    private final String name;

    @Nullable
    private final String filename;

    @Nullable
    private final Charset charset;

    @Nullable
    private final Long size;

    @Nullable
    private final ZonedDateTime creationDate;

    @Nullable
    private final ZonedDateTime modificationDate;

    @Nullable
    private final ZonedDateTime readDate;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ContentDisposition$Builder.class */
    public interface Builder {
        Builder name(String name);

        Builder filename(String filename);

        Builder filename(String filename, Charset charset);

        @Deprecated
        Builder size(Long size);

        @Deprecated
        Builder creationDate(ZonedDateTime creationDate);

        @Deprecated
        Builder modificationDate(ZonedDateTime modificationDate);

        @Deprecated
        Builder readDate(ZonedDateTime readDate);

        ContentDisposition build();
    }

    private ContentDisposition(@Nullable String type, @Nullable String name, @Nullable String filename, @Nullable Charset charset, @Nullable Long size, @Nullable ZonedDateTime creationDate, @Nullable ZonedDateTime modificationDate, @Nullable ZonedDateTime readDate) {
        this.type = type;
        this.name = name;
        this.filename = filename;
        this.charset = charset;
        this.size = size;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.readDate = readDate;
    }

    public boolean isAttachment() {
        return this.type != null && this.type.equalsIgnoreCase(FileUploadBase.ATTACHMENT);
    }

    public boolean isFormData() {
        return this.type != null && this.type.equalsIgnoreCase(FileUploadBase.FORM_DATA);
    }

    public boolean isInline() {
        return this.type != null && this.type.equalsIgnoreCase("inline");
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getFilename() {
        return this.filename;
    }

    @Nullable
    public Charset getCharset() {
        return this.charset;
    }

    @Nullable
    @Deprecated
    public Long getSize() {
        return this.size;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getModificationDate() {
        return this.modificationDate;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getReadDate() {
        return this.readDate;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentDisposition)) {
            return false;
        }
        ContentDisposition otherCd = (ContentDisposition) other;
        return ObjectUtils.nullSafeEquals(this.type, otherCd.type) && ObjectUtils.nullSafeEquals(this.name, otherCd.name) && ObjectUtils.nullSafeEquals(this.filename, otherCd.filename) && ObjectUtils.nullSafeEquals(this.charset, otherCd.charset) && ObjectUtils.nullSafeEquals(this.size, otherCd.size) && ObjectUtils.nullSafeEquals(this.creationDate, otherCd.creationDate) && ObjectUtils.nullSafeEquals(this.modificationDate, otherCd.modificationDate) && ObjectUtils.nullSafeEquals(this.readDate, otherCd.readDate);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.type);
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.name))) + ObjectUtils.nullSafeHashCode(this.filename))) + ObjectUtils.nullSafeHashCode(this.charset))) + ObjectUtils.nullSafeHashCode(this.size))) + ObjectUtils.nullSafeHashCode(this.creationDate))) + ObjectUtils.nullSafeHashCode(this.modificationDate))) + ObjectUtils.nullSafeHashCode(this.readDate);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.type != null) {
            sb.append(this.type);
        }
        if (this.name != null) {
            sb.append("; name=\"");
            sb.append(this.name).append('\"');
        }
        if (this.filename != null) {
            if (this.charset == null || StandardCharsets.US_ASCII.equals(this.charset)) {
                sb.append("; filename=\"");
                sb.append(escapeQuotationsInFilename(this.filename)).append('\"');
            } else {
                sb.append("; filename*=");
                sb.append(encodeFilename(this.filename, this.charset));
            }
        }
        if (this.size != null) {
            sb.append("; size=");
            sb.append(this.size);
        }
        if (this.creationDate != null) {
            sb.append("; creation-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.creationDate));
            sb.append('\"');
        }
        if (this.modificationDate != null) {
            sb.append("; modification-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.modificationDate));
            sb.append('\"');
        }
        if (this.readDate != null) {
            sb.append("; read-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.readDate));
            sb.append('\"');
        }
        return sb.toString();
    }

    public static Builder attachment() {
        return builder(FileUploadBase.ATTACHMENT);
    }

    public static Builder formData() {
        return builder(FileUploadBase.FORM_DATA);
    }

    public static Builder inline() {
        return builder("inline");
    }

    public static Builder builder(String type) {
        return new BuilderImpl(type);
    }

    public static ContentDisposition empty() {
        return new ContentDisposition("", null, null, null, null, null, null, null);
    }

    public static ContentDisposition parse(String contentDisposition) {
        String strSubstring;
        List<String> parts = tokenize(contentDisposition);
        String type = parts.get(0);
        String name = null;
        String filename = null;
        Charset charset = null;
        Long size = null;
        ZonedDateTime creationDate = null;
        ZonedDateTime modificationDate = null;
        ZonedDateTime readDate = null;
        for (int i = 1; i < parts.size(); i++) {
            String part = parts.get(i);
            int eqIndex = part.indexOf(61);
            if (eqIndex != -1) {
                String attribute = part.substring(0, eqIndex);
                if (part.startsWith("\"", eqIndex + 1) && part.endsWith("\"")) {
                    strSubstring = part.substring(eqIndex + 2, part.length() - 1);
                } else {
                    strSubstring = part.substring(eqIndex + 1);
                }
                String value = strSubstring;
                if (attribute.equals("name")) {
                    name = value;
                } else if (attribute.equals("filename*")) {
                    int idx1 = value.indexOf(39);
                    int idx2 = value.indexOf(39, idx1 + 1);
                    if (idx1 != -1 && idx2 != -1) {
                        charset = Charset.forName(value.substring(0, idx1).trim());
                        Assert.isTrue(StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset), "Charset must be UTF-8 or ISO-8859-1");
                        filename = decodeFilename(value.substring(idx2 + 1), charset);
                    } else {
                        filename = decodeFilename(value, StandardCharsets.US_ASCII);
                    }
                } else if (attribute.equals("filename") && filename == null) {
                    if (value.startsWith("=?")) {
                        Matcher matcher = BASE64_ENCODED_PATTERN.matcher(value);
                        if (matcher.find()) {
                            String match1 = matcher.group(1);
                            String match2 = matcher.group(2);
                            filename = new String(Base64.getDecoder().decode(match2), Charset.forName(match1));
                        } else {
                            filename = value;
                        }
                    } else {
                        filename = value;
                    }
                } else if (attribute.equals(InputTag.SIZE_ATTRIBUTE)) {
                    size = Long.valueOf(Long.parseLong(value));
                } else if (attribute.equals("creation-date")) {
                    try {
                        creationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e) {
                    }
                } else if (attribute.equals("modification-date")) {
                    try {
                        modificationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e2) {
                    }
                } else if (attribute.equals("read-date")) {
                    try {
                        readDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e3) {
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid content disposition format");
            }
        }
        return new ContentDisposition(type, name, filename, charset, size, creationDate, modificationDate, readDate);
    }

    private static List<String> tokenize(String headerValue) {
        int index = headerValue.indexOf(59);
        String type = (index >= 0 ? headerValue.substring(0, index) : headerValue).trim();
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Content-Disposition header must not be empty");
        }
        List<String> parts = new ArrayList<>();
        parts.add(type);
        if (index >= 0) {
            do {
                int nextIndex = index + 1;
                boolean quoted = false;
                boolean escaped = false;
                while (nextIndex < headerValue.length()) {
                    char ch2 = headerValue.charAt(nextIndex);
                    if (ch2 == ';') {
                        if (!quoted) {
                            break;
                        }
                    } else if (!escaped && ch2 == '\"') {
                        quoted = !quoted;
                    }
                    escaped = !escaped && ch2 == '\\';
                    nextIndex++;
                }
                String part = headerValue.substring(index + 1, nextIndex).trim();
                if (!part.isEmpty()) {
                    parts.add(part);
                }
                index = nextIndex;
            } while (index < headerValue.length());
        }
        return parts;
    }

    private static String decodeFilename(String filename, Charset charset) {
        Assert.notNull(filename, "'filename' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        byte[] value = filename.getBytes(charset);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int index = 0;
        while (index < value.length) {
            byte b = value[index];
            if (isRFC5987AttrChar(b)) {
                baos.write((char) b);
                index++;
            } else if (b == 37 && index < value.length - 2) {
                char[] array = {(char) value[index + 1], (char) value[index + 2]};
                try {
                    baos.write(Integer.parseInt(String.valueOf(array), 16));
                    index += 3;
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT, ex);
                }
            } else {
                throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT);
            }
        }
        return StreamUtils.copyToString(baos, charset);
    }

    private static boolean isRFC5987AttrChar(byte c) {
        return (c >= 48 && c <= 57) || (c >= 97 && c <= 122) || ((c >= 65 && c <= 90) || c == 33 || c == 35 || c == 36 || c == 38 || c == 43 || c == 45 || c == 46 || c == 94 || c == 95 || c == 96 || c == 124 || c == 126);
    }

    private static String escapeQuotationsInFilename(String filename) {
        if (filename.indexOf(34) == -1 && filename.indexOf(92) == -1) {
            return filename;
        }
        boolean escaped = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (!escaped && c == '\"') {
                sb.append("\\\"");
            } else {
                sb.append(c);
            }
            escaped = !escaped && c == '\\';
        }
        if (escaped) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String encodeFilename(String input, Charset charset) {
        Assert.notNull(input, "'input' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        Assert.isTrue(!StandardCharsets.US_ASCII.equals(charset), "ASCII does not require encoding");
        Assert.isTrue(StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset), "Only UTF-8 and ISO-8859-1 are supported");
        byte[] source = input.getBytes(charset);
        int len = source.length;
        StringBuilder sb = new StringBuilder(len << 1);
        sb.append(charset.name());
        sb.append("''");
        for (byte b : source) {
            if (isRFC5987AttrChar(b)) {
                sb.append((char) b);
            } else {
                sb.append('%');
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 15, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 15, 16));
                sb.append(hex1);
                sb.append(hex2);
            }
        }
        return sb.toString();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ContentDisposition$BuilderImpl.class */
    private static class BuilderImpl implements Builder {
        private final String type;

        @Nullable
        private String name;

        @Nullable
        private String filename;

        @Nullable
        private Charset charset;

        @Nullable
        private Long size;

        @Nullable
        private ZonedDateTime creationDate;

        @Nullable
        private ZonedDateTime modificationDate;

        @Nullable
        private ZonedDateTime readDate;

        public BuilderImpl(String type) {
            Assert.hasText(type, "'type' must not be not empty");
            this.type = type;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder filename(String filename) {
            Assert.hasText(filename, "No filename");
            this.filename = filename;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder filename(String filename, Charset charset) {
            Assert.hasText(filename, "No filename");
            this.filename = filename;
            this.charset = charset;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder creationDate(ZonedDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder modificationDate(ZonedDateTime modificationDate) {
            this.modificationDate = modificationDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder readDate(ZonedDateTime readDate) {
            this.readDate = readDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public ContentDisposition build() {
            return new ContentDisposition(this.type, this.name, this.filename, this.charset, this.size, this.creationDate, this.modificationDate, this.readDate);
        }
    }
}
