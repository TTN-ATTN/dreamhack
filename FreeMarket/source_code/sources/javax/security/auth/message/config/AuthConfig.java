package javax.security.auth.message.config;

import javax.security.auth.message.MessageInfo;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/security/auth/message/config/AuthConfig.class */
public interface AuthConfig {
    String getMessageLayer();

    String getAppContext();

    String getAuthContextID(MessageInfo messageInfo);

    void refresh();

    boolean isProtected();
}
