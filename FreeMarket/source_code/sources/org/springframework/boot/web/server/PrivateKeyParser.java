package org.springframework.boot.web.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/PrivateKeyParser.class */
final class PrivateKeyParser {
    private static final String PKCS1_HEADER = "-+BEGIN\\s+RSA\\s+PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+";
    private static final String PKCS1_FOOTER = "-+END\\s+RSA\\s+PRIVATE\\s+KEY[^-]*-+";
    private static final String PKCS8_HEADER = "-+BEGIN\\s+PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+";
    private static final String PKCS8_FOOTER = "-+END\\s+PRIVATE\\s+KEY[^-]*-+";
    private static final String EC_HEADER = "-+BEGIN\\s+EC\\s+PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+";
    private static final String EC_FOOTER = "-+END\\s+EC\\s+PRIVATE\\s+KEY[^-]*-+";
    private static final String BASE64_TEXT = "([a-z0-9+/=\\r\\n]+)";
    private static final List<PemParser> PEM_PARSERS;
    private static final int[] RSA_ALGORITHM;
    private static final int[] EC_ALGORITHM;
    private static final int[] EC_PARAMETERS;

    static {
        List<PemParser> parsers = new ArrayList<>();
        parsers.add(new PemParser(PKCS1_HEADER, PKCS1_FOOTER, PrivateKeyParser::createKeySpecForPkcs1, "RSA"));
        parsers.add(new PemParser(EC_HEADER, EC_FOOTER, PrivateKeyParser::createKeySpecForEc, "EC"));
        parsers.add(new PemParser(PKCS8_HEADER, PKCS8_FOOTER, PKCS8EncodedKeySpec::new, "RSA", "EC", "DSA"));
        PEM_PARSERS = Collections.unmodifiableList(parsers);
        RSA_ALGORITHM = new int[]{42, 134, 72, 134, 247, 13, 1, 1, 1};
        EC_ALGORITHM = new int[]{42, 134, 72, HttpServletResponse.SC_PARTIAL_CONTENT, 61, 2, 1};
        EC_PARAMETERS = new int[]{43, 129, 4, 0, 34};
    }

    private PrivateKeyParser() {
    }

    private static PKCS8EncodedKeySpec createKeySpecForPkcs1(byte[] bytes) {
        return createKeySpecForAlgorithm(bytes, RSA_ALGORITHM, null);
    }

    private static PKCS8EncodedKeySpec createKeySpecForEc(byte[] bytes) {
        return createKeySpecForAlgorithm(bytes, EC_ALGORITHM, EC_PARAMETERS);
    }

    private static PKCS8EncodedKeySpec createKeySpecForAlgorithm(byte[] bytes, int[] algorithm, int[] parameters) {
        try {
            DerEncoder encoder = new DerEncoder();
            encoder.integer(0);
            DerEncoder algorithmIdentifier = new DerEncoder();
            algorithmIdentifier.objectIdentifier(algorithm);
            algorithmIdentifier.objectIdentifier(parameters);
            byte[] byteArray = algorithmIdentifier.toByteArray();
            encoder.sequence(byteArray);
            encoder.octetString(bytes);
            return new PKCS8EncodedKeySpec(encoder.toSequence());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static PrivateKey parse(String resource) {
        try {
            String text = readText(resource);
            for (PemParser pemParser : PEM_PARSERS) {
                PrivateKey privateKey = pemParser.parse(text);
                if (privateKey != null) {
                    return privateKey;
                }
            }
            throw new IllegalStateException("Unrecognized private key format");
        } catch (Exception ex) {
            throw new IllegalStateException("Error loading private key file " + resource, ex);
        }
    }

    private static String readText(String resource) throws IOException {
        URL url = ResourceUtils.getURL(resource);
        Reader reader = new InputStreamReader(url.openStream());
        Throwable th = null;
        try {
            try {
                String strCopyToString = FileCopyUtils.copyToString(reader);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        reader.close();
                    }
                }
                return strCopyToString;
            } finally {
            }
        } catch (Throwable th3) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    reader.close();
                }
            }
            throw th3;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/PrivateKeyParser$PemParser.class */
    private static class PemParser {
        private final Pattern pattern;
        private final Function<byte[], PKCS8EncodedKeySpec> keySpecFactory;
        private final String[] algorithms;

        PemParser(String header, String footer, Function<byte[], PKCS8EncodedKeySpec> keySpecFactory, String... algorithms) {
            this.pattern = Pattern.compile(header + PrivateKeyParser.BASE64_TEXT + footer, 2);
            this.algorithms = algorithms;
            this.keySpecFactory = keySpecFactory;
        }

        PrivateKey parse(String text) {
            Matcher matcher = this.pattern.matcher(text);
            if (matcher.find()) {
                return parse(decodeBase64(matcher.group(1)));
            }
            return null;
        }

        private static byte[] decodeBase64(String content) {
            byte[] contentBytes = content.replaceAll("\r", "").replaceAll("\n", "").getBytes();
            return Base64Utils.decode(contentBytes);
        }

        private PrivateKey parse(byte[] bytes) throws NoSuchAlgorithmException {
            try {
                PKCS8EncodedKeySpec keySpec = this.keySpecFactory.apply(bytes);
                for (String algorithm : this.algorithms) {
                    KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
                    try {
                        return keyFactory.generatePrivate(keySpec);
                    } catch (InvalidKeySpecException e) {
                    }
                }
                return null;
            } catch (GeneralSecurityException ex) {
                throw new IllegalArgumentException("Unexpected key format", ex);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/PrivateKeyParser$DerEncoder.class */
    static class DerEncoder {
        private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        DerEncoder() {
        }

        void objectIdentifier(int... encodedObjectIdentifier) throws IOException {
            int code = encodedObjectIdentifier != null ? 6 : 5;
            codeLengthBytes(code, bytes(encodedObjectIdentifier));
        }

        void integer(int... encodedInteger) throws IOException {
            codeLengthBytes(2, bytes(encodedInteger));
        }

        void octetString(byte[] bytes) throws IOException {
            codeLengthBytes(4, bytes);
        }

        void sequence(int... elements) throws IOException {
            sequence(bytes(elements));
        }

        void sequence(byte[] bytes) throws IOException {
            codeLengthBytes(48, bytes);
        }

        void codeLengthBytes(int code, byte[] bytes) throws IOException {
            this.stream.write(code);
            int length = bytes != null ? bytes.length : 0;
            if (length <= 127) {
                this.stream.write(length & Const.MAX_ARRAY_DIMENSIONS);
            } else {
                ByteArrayOutputStream lengthStream = new ByteArrayOutputStream();
                while (length != 0) {
                    lengthStream.write(length & Const.MAX_ARRAY_DIMENSIONS);
                    length >>= 8;
                }
                byte[] lengthBytes = lengthStream.toByteArray();
                this.stream.write(128 | lengthBytes.length);
                for (int i = lengthBytes.length - 1; i >= 0; i--) {
                    this.stream.write(lengthBytes[i]);
                }
            }
            if (bytes != null) {
                this.stream.write(bytes);
            }
        }

        private static byte[] bytes(int... elements) {
            if (elements == null) {
                return null;
            }
            byte[] result = new byte[elements.length];
            for (int i = 0; i < elements.length; i++) {
                result[i] = (byte) elements[i];
            }
            return result;
        }

        byte[] toSequence() throws IOException {
            DerEncoder sequenceEncoder = new DerEncoder();
            sequenceEncoder.sequence(toByteArray());
            return sequenceEncoder.toByteArray();
        }

        byte[] toByteArray() {
            return this.stream.toByteArray();
        }
    }
}
