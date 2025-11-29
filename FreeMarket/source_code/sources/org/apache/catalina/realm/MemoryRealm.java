package org.apache.catalina.realm;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/realm/MemoryRealm.class */
public class MemoryRealm extends RealmBase {
    private static final Log log = LogFactory.getLog((Class<?>) MemoryRealm.class);
    private static Digester digester = null;
    private static final Object digesterLock = new Object();
    private String pathname = "conf/tomcat-users.xml";
    private final Map<String, GenericPrincipal> principals = new HashMap();

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        GenericPrincipal principal = this.principals.get(username);
        if (principal == null || principal.getPassword() == null) {
            getCredentialHandler().mutate(credentials);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, principal.getPassword());
        if (validated) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateSuccess", username));
            }
            return principal;
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("memoryRealm.authenticateFailure", username));
            return null;
        }
        return null;
    }

    void addUser(String username, String password, String roles) {
        List<String> list = new ArrayList<>();
        String strSubstring = roles + ",";
        while (true) {
            String roles2 = strSubstring;
            int comma = roles2.indexOf(44);
            if (comma >= 0) {
                String role = roles2.substring(0, comma).trim();
                list.add(role);
                strSubstring = roles2.substring(comma + 1);
            } else {
                GenericPrincipal principal = new GenericPrincipal(username, password, list);
                this.principals.put(username, principal);
                return;
            }
        }
    }

    protected Digester getDigester() {
        synchronized (digesterLock) {
            if (digester == null) {
                digester = new Digester();
                digester.setValidating(false);
                try {
                    digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                } catch (Exception e) {
                    log.warn(sm.getString("memoryRealm.xmlFeatureEncoding"), e);
                }
                digester.addRuleSet(new MemoryRuleSet());
            }
        }
        return digester;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        GenericPrincipal principal = this.principals.get(username);
        if (principal != null) {
            return principal.getPassword();
        }
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        return this.principals.get(username);
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException, IOException {
        String pathName = getPathname();
        try {
            InputStream is = ConfigFileLoader.getSource().getResource(pathName).getInputStream();
            try {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("memoryRealm.loadPath", pathName));
                }
                Digester digester2 = getDigester();
                try {
                    try {
                        synchronized (digester2) {
                            digester2.push(this);
                            digester2.parse(is);
                        }
                        digester2.reset();
                        if (is != null) {
                            is.close();
                        }
                        super.startInternal();
                    } catch (Exception e) {
                        throw new LifecycleException(sm.getString("memoryRealm.readXml"), e);
                    }
                } catch (Throwable th) {
                    digester2.reset();
                    throw th;
                }
            } finally {
            }
        } catch (IOException ioe) {
            throw new LifecycleException(sm.getString("memoryRealm.loadExist", pathName), ioe);
        }
    }
}
