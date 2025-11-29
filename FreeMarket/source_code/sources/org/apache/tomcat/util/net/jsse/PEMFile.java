package org.apache.tomcat.util.net.jsse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.buf.Asn1Parser;
import org.apache.tomcat.util.buf.Asn1Writer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.asm.Opcodes;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/jsse/PEMFile.class */
public class PEMFile {
    private static final StringManager sm = StringManager.getManager((Class<?>) PEMFile.class);
    private static final byte[] OID_EC_PUBLIC_KEY = {6, 7, 42, -122, 72, -50, 61, 2, 1};
    private static final String OID_PKCS5_PBES2 = "1.2.840.113549.1.5.13";
    private static final String PBES2 = "PBES2";
    private List<X509Certificate> certificates;
    private PrivateKey privateKey;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/jsse/PEMFile$Format.class */
    private enum Format {
        PKCS1,
        PKCS8,
        RFC5915
    }

    public static String toPEM(X509Certificate certificate) throws CertificateEncodingException {
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN CERTIFICATE-----");
        result.append(System.lineSeparator());
        Base64 b64 = new Base64(64);
        result.append(b64.encodeAsString(certificate.getEncoded()));
        result.append("-----END CERTIFICATE-----");
        return result.toString();
    }

    public List<X509Certificate> getCertificates() {
        return this.certificates;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PEMFile(String filename) throws GeneralSecurityException, IOException {
        this(filename, null);
    }

    public PEMFile(String filename, String password) throws GeneralSecurityException, IOException {
        this(filename, password, null);
    }

    public PEMFile(String filename, String password, String keyAlgorithm) throws GeneralSecurityException, IOException {
        this(filename, ConfigFileLoader.getSource().getResource(filename).getInputStream(), password, keyAlgorithm);
    }

    public PEMFile(String filename, InputStream fileStream, String password, String keyAlgorithm) throws GeneralSecurityException, IOException {
        this.certificates = new ArrayList();
        List<Part> parts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.US_ASCII));
        Part part = null;
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith(Part.BEGIN_BOUNDARY)) {
                    part = new Part();
                    part.type = line.substring(Part.BEGIN_BOUNDARY.length(), line.length() - Part.FINISH_BOUNDARY.length()).trim();
                } else if (line.startsWith(Part.END_BOUNDARY)) {
                    parts.add(part);
                    part = null;
                } else if (part != null && !line.contains(":") && !line.startsWith(" ")) {
                    part.content += line;
                } else if (part != null && line.contains(":") && !line.startsWith(" ") && line.startsWith("DEK-Info: ")) {
                    String[] pieces = line.split(" ")[1].split(",");
                    if (pieces.length == 2) {
                        part.algorithm = pieces[0];
                        part.ivHex = pieces[1];
                    }
                }
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        reader.close();
        for (Part part2 : parts) {
            switch (part2.type) {
                case "PRIVATE KEY":
                    this.privateKey = part2.toPrivateKey(null, keyAlgorithm, Format.PKCS8, filename);
                    break;
                case "EC PRIVATE KEY":
                    this.privateKey = part2.toPrivateKey(null, "EC", Format.RFC5915, filename);
                    break;
                case "ENCRYPTED PRIVATE KEY":
                    this.privateKey = part2.toPrivateKey(password, keyAlgorithm, Format.PKCS8, filename);
                    break;
                case "RSA PRIVATE KEY":
                    if (part2.algorithm == null) {
                        this.privateKey = part2.toPrivateKey(null, keyAlgorithm, Format.PKCS1, filename);
                        break;
                    } else {
                        this.privateKey = part2.toPrivateKey(password, keyAlgorithm, Format.PKCS1, filename);
                        break;
                    }
                case "CERTIFICATE":
                case "X509 CERTIFICATE":
                    this.certificates.add(part2.toCertificate());
                    break;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/net/jsse/PEMFile$Part.class */
    private class Part {
        public static final String BEGIN_BOUNDARY = "-----BEGIN ";
        public static final String END_BOUNDARY = "-----END ";
        public static final String FINISH_BOUNDARY = "-----";
        public static final String PRIVATE_KEY = "PRIVATE KEY";
        public static final String EC_PRIVATE_KEY = "EC PRIVATE KEY";
        public static final String ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
        public static final String RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
        public static final String CERTIFICATE = "CERTIFICATE";
        public static final String X509_CERTIFICATE = "X509 CERTIFICATE";
        public String type;
        public String content;
        public String algorithm;
        public String ivHex;

        private Part() {
            this.content = "";
            this.algorithm = null;
            this.ivHex = null;
        }

        private byte[] decode() {
            return Base64.decodeBase64(this.content);
        }

        public X509Certificate toCertificate() throws CertificateException {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(decode()));
        }

        public PrivateKey toPrivateKey(String password, String keyAlgorithm, Format format, String filename) throws GeneralSecurityException, IOException {
            String secretKeyAlgorithm;
            String cipherTransformation;
            int keyLength;
            KeySpec keySpec = null;
            if (password == null) {
                switch (format) {
                    case PKCS1:
                        keySpec = parsePKCS1(decode());
                        break;
                    case PKCS8:
                        keySpec = new PKCS8EncodedKeySpec(decode());
                        break;
                    case RFC5915:
                        keySpec = new PKCS8EncodedKeySpec(rfc5915ToPkcs8(decode()));
                        break;
                }
            } else if (this.algorithm == null) {
                EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(decode());
                String pbeAlgorithm = getPBEAlgorithm(privateKeyInfo);
                SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(pbeAlgorithm);
                SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
                Cipher cipher = Cipher.getInstance(pbeAlgorithm);
                cipher.init(2, secretKey, privateKeyInfo.getAlgParameters());
                keySpec = privateKeyInfo.getKeySpec(cipher);
            } else {
                switch (this.algorithm) {
                    case "DES-CBC":
                        secretKeyAlgorithm = "DES";
                        cipherTransformation = "DES/CBC/PKCS5Padding";
                        keyLength = 8;
                        break;
                    case "DES-EDE3-CBC":
                        secretKeyAlgorithm = "DESede";
                        cipherTransformation = "DESede/CBC/PKCS5Padding";
                        keyLength = 24;
                        break;
                    case "AES-256-CBC":
                        secretKeyAlgorithm = "AES";
                        cipherTransformation = "AES/CBC/PKCS5Padding";
                        keyLength = 32;
                        break;
                    default:
                        secretKeyAlgorithm = this.algorithm;
                        cipherTransformation = this.algorithm;
                        keyLength = 8;
                        break;
                }
                byte[] iv = fromHex(this.ivHex);
                byte[] key = deriveKey(keyLength, password, iv);
                SecretKey secretKey2 = new SecretKeySpec(key, secretKeyAlgorithm);
                Cipher cipher2 = Cipher.getInstance(cipherTransformation);
                cipher2.init(2, secretKey2, new IvParameterSpec(iv));
                byte[] pkcs1 = cipher2.doFinal(decode());
                keySpec = parsePKCS1(pkcs1);
            }
            InvalidKeyException exception = new InvalidKeyException(PEMFile.sm.getString("pemFile.parseError", filename));
            if (keyAlgorithm == null) {
                for (String algorithm : new String[]{"RSA", "DSA", "EC"}) {
                    try {
                        return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                    } catch (InvalidKeySpecException e) {
                        exception.addSuppressed(e);
                    }
                }
            } else {
                try {
                    return KeyFactory.getInstance(keyAlgorithm).generatePrivate(keySpec);
                } catch (InvalidKeySpecException e2) {
                    exception.addSuppressed(e2);
                }
            }
            throw exception;
        }

        private String getPBEAlgorithm(EncryptedPrivateKeyInfo privateKeyInfo) {
            AlgorithmParameters parameters = privateKeyInfo.getAlgParameters();
            String algName = privateKeyInfo.getAlgName();
            if (parameters != null && (PEMFile.OID_PKCS5_PBES2.equals(algName) || PEMFile.PBES2.equals(algName))) {
                return parameters.toString();
            }
            return privateKeyInfo.getAlgName();
        }

        private byte[] deriveKey(int keyLength, String password, byte[] iv) throws NoSuchAlgorithmException {
            byte[] key = new byte[keyLength];
            int insertPosition = 0;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] pw = password.getBytes(StandardCharsets.UTF_8);
            while (insertPosition < keyLength) {
                digest.update(pw);
                digest.update(iv, 0, 8);
                byte[] round = digest.digest();
                digest.update(round);
                System.arraycopy(round, 0, key, insertPosition, Math.min(keyLength - insertPosition, round.length));
                insertPosition += round.length;
            }
            return key;
        }

        /* JADX WARN: Type inference failed for: r0v30, types: [byte[], byte[][]] */
        /* JADX WARN: Type inference failed for: r3v3, types: [byte[], byte[][]] */
        /* JADX WARN: Type inference failed for: r3v6, types: [byte[], byte[][]] */
        private byte[] rfc5915ToPkcs8(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() != 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(4);
            int privateKeyLen = p.parseLength();
            byte[] privateKey = new byte[privateKeyLen];
            p.parseBytes(privateKey);
            p.parseTag(160);
            int oidLen = p.parseLength();
            byte[] oid = new byte[oidLen];
            p.parseBytes(oid);
            if (oid[0] != 6) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(Opcodes.IF_ICMPLT);
            int publicKeyLen = p.parseLength();
            byte[] publicKey = new byte[publicKeyLen];
            p.parseBytes(publicKey);
            if (publicKey[0] != 3) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            return Asn1Writer.writeSequence(new byte[]{Asn1Writer.writeInteger(0), Asn1Writer.writeSequence(new byte[]{PEMFile.OID_EC_PUBLIC_KEY, oid}), Asn1Writer.writeOctetString(Asn1Writer.writeSequence(new byte[]{Asn1Writer.writeInteger(1), Asn1Writer.writeOctetString(privateKey), Asn1Writer.writeTag((byte) -95, publicKey)}))});
        }

        private RSAPrivateCrtKeySpec parsePKCS1(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() == 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.noMultiPrimes"));
            }
            return new RSAPrivateCrtKeySpec(p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt());
        }

        private byte[] fromHex(String hexString) {
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length(); i += 2) {
                bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
            }
            return bytes;
        }
    }
}
