package org.springframework.boot.web.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/CertificateParser.class */
final class CertificateParser {
    private static final String HEADER = "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+";
    private static final String BASE64_TEXT = "([a-z0-9+/=\\r\\n]+)";
    private static final String FOOTER = "-+END\\s+.*CERTIFICATE[^-]*-+";
    private static final Pattern PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);

    private CertificateParser() {
    }

    static X509Certificate[] parse(String path) {
        CertificateFactory factory = getCertificateFactory();
        List<X509Certificate> certificates = new ArrayList<>();
        certificates.getClass();
        readCertificates(path, factory, (v1) -> {
            r2.add(v1);
        });
        return (X509Certificate[]) certificates.toArray(new X509Certificate[0]);
    }

    private static CertificateFactory getCertificateFactory() {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException ex) {
            throw new IllegalStateException("Unable to get X.509 certificate factory", ex);
        }
    }

    private static void readCertificates(String resource, CertificateFactory factory, Consumer<X509Certificate> consumer) {
        try {
            String text = readText(resource);
            Matcher matcher = PATTERN.matcher(text);
            while (matcher.find()) {
                String encodedText = matcher.group(1);
                byte[] decodedBytes = decodeBase64(encodedText);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                while (inputStream.available() > 0) {
                    consumer.accept((X509Certificate) factory.generateCertificate(inputStream));
                }
            }
        } catch (IOException | CertificateException ex) {
            throw new IllegalStateException("Error reading certificate from '" + resource + "' : " + ex.getMessage(), ex);
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

    private static byte[] decodeBase64(String content) {
        byte[] bytes = content.replaceAll("\r", "").replaceAll("\n", "").getBytes();
        return Base64Utils.decode(bytes);
    }
}
