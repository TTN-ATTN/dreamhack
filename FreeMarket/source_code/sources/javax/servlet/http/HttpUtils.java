package javax.servlet.http;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.springframework.beans.factory.BeanFactory;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpUtils.class */
public class HttpUtils {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    public static Hashtable<String, String[]> parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Hashtable<String, String[]> ht = new Hashtable<>();
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s, BeanFactory.FACTORY_BEAN_PREFIX);
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1), sb);
            if (ht.containsKey(key)) {
                String[] oldVals = ht.get(key);
                valArray = (String[]) Arrays.copyOf(oldVals, oldVals.length + 1);
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[]{val};
            }
            ht.put(key, valArray);
        }
        return ht;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0070, code lost:
    
        r10 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x007d, code lost:
    
        throw new java.lang.IllegalArgumentException(r10.getMessage(), r10);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.Hashtable<java.lang.String, java.lang.String[]> parsePostData(int r7, javax.servlet.ServletInputStream r8) {
        /*
            r0 = r7
            if (r0 > 0) goto Lc
            java.util.Hashtable r0 = new java.util.Hashtable
            r1 = r0
            r1.<init>()
            return r0
        Lc:
            r0 = r8
            if (r0 != 0) goto L18
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            r1.<init>()
            throw r0
        L18:
            r0 = r7
            byte[] r0 = new byte[r0]
            r9 = r0
            r0 = 0
            r10 = r0
        L1e:
            r0 = r8
            r1 = r9
            r2 = r10
            r3 = r7
            r4 = r10
            int r3 = r3 - r4
            int r0 = r0.read(r1, r2, r3)     // Catch: java.io.IOException -> L50
            r11 = r0
            r0 = r11
            if (r0 > 0) goto L42
            java.util.ResourceBundle r0 = javax.servlet.http.HttpUtils.lStrings     // Catch: java.io.IOException -> L50
            java.lang.String r1 = "err.io.short_read"
            java.lang.String r0 = r0.getString(r1)     // Catch: java.io.IOException -> L50
            r12 = r0
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch: java.io.IOException -> L50
            r1 = r0
            r2 = r12
            r1.<init>(r2)     // Catch: java.io.IOException -> L50
            throw r0     // Catch: java.io.IOException -> L50
        L42:
            r0 = r10
            r1 = r11
            int r0 = r0 + r1
            r10 = r0
            r0 = r7
            r1 = r10
            int r0 = r0 - r1
            if (r0 > 0) goto L1e
            goto L5e
        L50:
            r10 = move-exception
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            r2 = r10
            java.lang.String r2 = r2.getMessage()
            r3 = r10
            r1.<init>(r2, r3)
            throw r0
        L5e:
            java.lang.String r0 = new java.lang.String     // Catch: java.io.UnsupportedEncodingException -> L70
            r1 = r0
            r2 = r9
            r3 = 0
            r4 = r7
            java.lang.String r5 = "8859_1"
            r1.<init>(r2, r3, r4, r5)     // Catch: java.io.UnsupportedEncodingException -> L70
            r10 = r0
            r0 = r10
            java.util.Hashtable r0 = parseQueryString(r0)     // Catch: java.io.UnsupportedEncodingException -> L70
            return r0
        L70:
            r10 = move-exception
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            r2 = r10
            java.lang.String r2 = r2.getMessage()
            r3 = r10
            r1.<init>(r2, r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.servlet.http.HttpUtils.parsePostData(int, javax.servlet.ServletInputStream):java.util.Hashtable");
    }

    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            switch (c) {
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e2) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() != 2) {
                            break;
                        } else {
                            i++;
                            break;
                        }
                    }
                case '+':
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
                    break;
            }
            i++;
        }
        return sb.toString();
    }

    public static StringBuffer getRequestURL(HttpServletRequest req) {
        StringBuffer url = new StringBuffer();
        String scheme = req.getScheme();
        int port = req.getServerPort();
        String urlPath = req.getRequestURI();
        url.append(scheme);
        url.append("://");
        url.append(req.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(req.getServerPort());
        }
        url.append(urlPath);
        return url;
    }
}
