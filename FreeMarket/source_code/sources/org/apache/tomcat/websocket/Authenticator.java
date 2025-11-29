package org.apache.tomcat.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/Authenticator.class */
public abstract class Authenticator {
    private static final StringManager sm = StringManager.getManager((Class<?>) Authenticator.class);
    private static final Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|([^,=\"]+))\\s*,?");

    public abstract String getAuthorization(String str, String str2, String str3, String str4, String str5) throws AuthenticationException;

    public abstract String getSchemeName();

    @Deprecated
    public String getAuthorization(String requestUri, String authenticateHeader, Map<String, Object> userProperties) throws AuthenticationException {
        return getAuthorization(requestUri, authenticateHeader, (String) userProperties.get(Constants.WS_AUTHENTICATION_USER_NAME), (String) userProperties.get(Constants.WS_AUTHENTICATION_PASSWORD), (String) userProperties.get(Constants.WS_AUTHENTICATION_REALM));
    }

    @Deprecated
    public Map<String, String> parseWWWAuthenticateHeader(String authenticateHeader) {
        return parseAuthenticateHeader(authenticateHeader);
    }

    public Map<String, String> parseAuthenticateHeader(String authenticateHeader) {
        Matcher m = pattern.matcher(authenticateHeader);
        Map<String, String> parameterMap = new HashMap<>();
        while (m.find()) {
            String key = m.group(1);
            String qtedValue = m.group(3);
            String value = m.group(4);
            parameterMap.put(key, qtedValue != null ? qtedValue : value);
        }
        return parameterMap;
    }

    protected void validateUsername(String userName) throws AuthenticationException {
        if (userName == null) {
            throw new AuthenticationException(sm.getString("authenticator.nullUserName"));
        }
    }

    protected void validatePassword(String password) throws AuthenticationException {
        if (password == null) {
            throw new AuthenticationException(sm.getString("authenticator.nullPassword"));
        }
    }

    protected void validateRealm(String userRealm, String serverRealm) throws AuthenticationException {
        if (userRealm == null) {
            return;
        }
        String userRealm2 = userRealm.trim();
        if (userRealm2.length() == 0) {
            return;
        }
        if (serverRealm != null) {
            serverRealm = serverRealm.trim();
            if (userRealm2.equals(serverRealm)) {
                return;
            }
        }
        throw new AuthenticationException(sm.getString("authenticator.realmMismatch", userRealm2, serverRealm));
    }
}
