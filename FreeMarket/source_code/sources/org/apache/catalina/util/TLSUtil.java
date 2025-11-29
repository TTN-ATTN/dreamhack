package org.apache.catalina.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/util/TLSUtil.class */
public class TLSUtil {
    public static boolean isTLSRequestAttribute(String name) {
        switch (name) {
            case "javax.servlet.request.X509Certificate":
            case "javax.servlet.request.cipher_suite":
            case "javax.servlet.request.key_size":
            case "javax.servlet.request.ssl_session_id":
            case "javax.servlet.request.ssl_session_mgr":
            case "org.apache.tomcat.util.net.secure_protocol_version":
            case "org.apache.tomcat.util.net.secure_requested_protocol_versions":
            case "org.apache.tomcat.util.net.secure_requested_ciphers":
                return true;
            default:
                return false;
        }
    }
}
