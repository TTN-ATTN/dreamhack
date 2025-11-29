package org.springframework.boot.env;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.boot.origin.TextResourceOrigin;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedPropertiesLoader.class */
class OriginTrackedPropertiesLoader {
    private final Resource resource;

    OriginTrackedPropertiesLoader(Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }

    List<Document> load() throws IOException {
        return load(true);
    }

    List<Document> load(boolean expandLists) throws IOException {
        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        StringBuilder buffer = new StringBuilder();
        CharacterReader reader = new CharacterReader(this.resource);
        Throwable th = null;
        while (reader.read()) {
            try {
                try {
                    if (reader.isCommentPrefixCharacter()) {
                        char commentPrefixCharacter = reader.getCharacter();
                        if (isNewDocument(reader)) {
                            if (!document.isEmpty()) {
                                documents.add(document);
                            }
                            document = new Document();
                        } else {
                            if (document.isEmpty() && !documents.isEmpty()) {
                                document = documents.remove(documents.size() - 1);
                            }
                            reader.setLastLineCommentPrefixCharacter(commentPrefixCharacter);
                            reader.skipComment();
                        }
                    } else {
                        reader.setLastLineCommentPrefixCharacter(-1);
                        loadKeyAndValue(expandLists, document, reader, buffer);
                    }
                } finally {
                }
            } catch (Throwable th2) {
                if (reader != null) {
                    if (th != null) {
                        try {
                            reader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        reader.close();
                    }
                }
                throw th2;
            }
        }
        if (reader != null) {
            if (0 != 0) {
                try {
                    reader.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                reader.close();
            }
        }
        if (!document.isEmpty() && !documents.contains(document)) {
            documents.add(document);
        }
        return documents;
    }

    private void loadKeyAndValue(boolean expandLists, Document document, CharacterReader reader, StringBuilder buffer) throws IOException {
        String key = loadKey(buffer, reader).trim();
        if (expandLists && key.endsWith(ClassUtils.ARRAY_SUFFIX)) {
            String key2 = key.substring(0, key.length() - 2);
            int index = 0;
            do {
                OriginTrackedValue value = loadValue(buffer, reader, true);
                int i = index;
                index++;
                document.put(key2 + PropertyAccessor.PROPERTY_KEY_PREFIX + i + "]", value);
                if (!reader.isEndOfLine()) {
                    reader.read();
                }
            } while (!reader.isEndOfLine());
            return;
        }
        OriginTrackedValue value2 = loadValue(buffer, reader, false);
        document.put(key, value2);
    }

    private String loadKey(StringBuilder buffer, CharacterReader reader) throws IOException {
        buffer.setLength(0);
        boolean previousWhitespace = false;
        while (!reader.isEndOfLine()) {
            if (reader.isPropertyDelimiter()) {
                reader.read();
                return buffer.toString();
            }
            if (!reader.isWhiteSpace() && previousWhitespace) {
                return buffer.toString();
            }
            previousWhitespace = reader.isWhiteSpace();
            buffer.append(reader.getCharacter());
            reader.read();
        }
        return buffer.toString();
    }

    private OriginTrackedValue loadValue(StringBuilder buffer, CharacterReader reader, boolean splitLists) throws IOException {
        buffer.setLength(0);
        while (reader.isWhiteSpace() && !reader.isEndOfLine()) {
            reader.read();
        }
        TextResourceOrigin.Location location = reader.getLocation();
        while (!reader.isEndOfLine() && (!splitLists || !reader.isListDelimiter())) {
            buffer.append(reader.getCharacter());
            reader.read();
        }
        Origin origin = new TextResourceOrigin(this.resource, location);
        return OriginTrackedValue.of(buffer.toString(), origin);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isNewDocument(org.springframework.boot.env.OriginTrackedPropertiesLoader.CharacterReader r6) throws java.io.IOException {
        /*
            r5 = this;
            r0 = r6
            boolean r0 = r0.isSameLastLineCommentPrefix()
            if (r0 == 0) goto L9
            r0 = 0
            return r0
        L9:
            r0 = r6
            org.springframework.boot.origin.TextResourceOrigin$Location r0 = r0.getLocation()
            int r0 = r0.getColumn()
            if (r0 != 0) goto L17
            r0 = 1
            goto L18
        L17:
            r0 = 0
        L18:
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L34
            r0 = r5
            r1 = r6
            r2 = r6
            r3 = r2
            java.lang.Class r3 = r3.getClass()
            boolean r2 = r2::isHyphenCharacter
            boolean r0 = r0.readAndExpect(r1, r2)
            if (r0 == 0) goto L34
            r0 = 1
            goto L35
        L34:
            r0 = 0
        L35:
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L51
            r0 = r5
            r1 = r6
            r2 = r6
            r3 = r2
            java.lang.Class r3 = r3.getClass()
            boolean r2 = r2::isHyphenCharacter
            boolean r0 = r0.readAndExpect(r1, r2)
            if (r0 == 0) goto L51
            r0 = 1
            goto L52
        L51:
            r0 = 0
        L52:
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L6e
            r0 = r5
            r1 = r6
            r2 = r6
            r3 = r2
            java.lang.Class r3 = r3.getClass()
            boolean r2 = r2::isHyphenCharacter
            boolean r0 = r0.readAndExpect(r1, r2)
            if (r0 == 0) goto L6e
            r0 = 1
            goto L6f
        L6e:
            r0 = 0
        L6f:
            r7 = r0
            r0 = r6
            boolean r0 = r0.isEndOfLine()
            if (r0 != 0) goto L80
            r0 = r6
            boolean r0 = r0.read()
            r0 = r6
            org.springframework.boot.env.OriginTrackedPropertiesLoader.CharacterReader.access$200(r0)
        L80:
            r0 = r7
            if (r0 == 0) goto L8f
            r0 = r6
            boolean r0 = r0.isEndOfLine()
            if (r0 == 0) goto L8f
            r0 = 1
            goto L90
        L8f:
            r0 = 0
        L90:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.env.OriginTrackedPropertiesLoader.isNewDocument(org.springframework.boot.env.OriginTrackedPropertiesLoader$CharacterReader):boolean");
    }

    private boolean readAndExpect(CharacterReader reader, BooleanSupplier check) throws IOException {
        reader.read();
        return check.getAsBoolean();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedPropertiesLoader$CharacterReader.class */
    private static class CharacterReader implements Closeable {
        private static final String[] ESCAPES = {"trnf", "\t\r\n\f"};
        private final LineNumberReader reader;
        private int columnNumber = -1;
        private boolean escaped;
        private int character;
        private int lastLineCommentPrefixCharacter;

        CharacterReader(Resource resource) throws IOException {
            this.reader = new LineNumberReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1));
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.reader.close();
        }

        boolean read() throws IOException {
            this.escaped = false;
            this.character = this.reader.read();
            this.columnNumber++;
            if (this.columnNumber == 0) {
                skipWhitespace();
            }
            if (this.character == 92) {
                this.escaped = true;
                readEscaped();
            } else if (this.character == 10) {
                this.columnNumber = -1;
            }
            return !isEndOfFile();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void skipWhitespace() throws IOException {
            while (isWhiteSpace()) {
                this.character = this.reader.read();
                this.columnNumber++;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLastLineCommentPrefixCharacter(int lastLineCommentPrefixCharacter) {
            this.lastLineCommentPrefixCharacter = lastLineCommentPrefixCharacter;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void skipComment() throws IOException {
            while (this.character != 10 && this.character != -1) {
                this.character = this.reader.read();
            }
            this.columnNumber = -1;
        }

        private void readEscaped() throws IOException {
            this.character = this.reader.read();
            int escapeIndex = ESCAPES[0].indexOf(this.character);
            if (escapeIndex != -1) {
                this.character = ESCAPES[1].charAt(escapeIndex);
                return;
            }
            if (this.character == 10) {
                this.columnNumber = -1;
                read();
            } else if (this.character == 117) {
                readUnicode();
            }
        }

        private void readUnicode() throws IOException {
            this.character = 0;
            for (int i = 0; i < 4; i++) {
                int digit = this.reader.read();
                if (digit >= 48 && digit <= 57) {
                    this.character = ((this.character << 4) + digit) - 48;
                } else if (digit >= 97 && digit <= 102) {
                    this.character = (((this.character << 4) + digit) - 97) + 10;
                } else if (digit >= 65 && digit <= 70) {
                    this.character = (((this.character << 4) + digit) - 65) + 10;
                } else {
                    throw new IllegalStateException("Malformed \\uxxxx encoding.");
                }
            }
        }

        boolean isWhiteSpace() {
            return !this.escaped && (this.character == 32 || this.character == 9 || this.character == 12);
        }

        boolean isEndOfFile() {
            return this.character == -1;
        }

        boolean isEndOfLine() {
            return this.character == -1 || (!this.escaped && this.character == 10);
        }

        boolean isListDelimiter() {
            return !this.escaped && this.character == 44;
        }

        boolean isPropertyDelimiter() {
            return !this.escaped && (this.character == 61 || this.character == 58);
        }

        char getCharacter() {
            return (char) this.character;
        }

        TextResourceOrigin.Location getLocation() {
            return new TextResourceOrigin.Location(this.reader.getLineNumber(), this.columnNumber);
        }

        boolean isSameLastLineCommentPrefix() {
            return this.lastLineCommentPrefixCharacter == this.character;
        }

        boolean isCommentPrefixCharacter() {
            return this.character == 35 || this.character == 33;
        }

        boolean isHyphenCharacter() {
            return this.character == 45;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedPropertiesLoader$Document.class */
    static class Document {
        private final Map<String, OriginTrackedValue> values = new LinkedHashMap();

        Document() {
        }

        void put(String key, OriginTrackedValue value) {
            if (!key.isEmpty()) {
                this.values.put(key, value);
            }
        }

        boolean isEmpty() {
            return this.values.isEmpty();
        }

        Map<String, OriginTrackedValue> asMap() {
            return this.values;
        }
    }
}
