package org.apache.catalina.realm;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.LoginContext;
import org.apache.catalina.TomcatPrincipal;
import org.ietf.jgss.GSSCredential;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/realm/GenericPrincipal.class */
public class GenericPrincipal implements TomcatPrincipal, Serializable {
    private static final long serialVersionUID = 1;
    protected final String name;
    protected final String password;
    protected final String[] roles;
    protected final Principal userPrincipal;
    protected final transient LoginContext loginContext;
    protected transient GSSCredential gssCredential;
    protected final Map<String, Object> attributes;

    public GenericPrincipal(String name, String password, List<String> roles) {
        this(name, password, roles, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal) {
        this(name, password, roles, userPrincipal, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal, LoginContext loginContext) {
        this(name, password, roles, userPrincipal, loginContext, null, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal, LoginContext loginContext, GSSCredential gssCredential, Map<String, Object> attributes) {
        this.gssCredential = null;
        this.name = name;
        this.password = password;
        this.userPrincipal = userPrincipal;
        if (roles == null) {
            this.roles = new String[0];
        } else {
            this.roles = (String[]) roles.toArray(new String[0]);
            if (this.roles.length > 1) {
                Arrays.sort(this.roles);
            }
        }
        this.loginContext = loginContext;
        this.gssCredential = gssCredential;
        this.attributes = attributes != null ? Collections.unmodifiableMap(attributes) : null;
    }

    @Override // java.security.Principal
    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String[] getRoles() {
        return this.roles;
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public Principal getUserPrincipal() {
        if (this.userPrincipal != null) {
            return this.userPrincipal;
        }
        return this;
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public GSSCredential getGssCredential() {
        return this.gssCredential;
    }

    protected void setGssCredential(GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }

    public boolean hasRole(String role) {
        if ("*".equals(role)) {
            return true;
        }
        return role != null && Arrays.binarySearch(this.roles, role) >= 0;
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("GenericPrincipal[");
        sb.append(this.name);
        sb.append('(');
        for (String role : this.roles) {
            sb.append(role).append(',');
        }
        sb.append(")]");
        return sb.toString();
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public void logout() throws Exception {
        if (this.loginContext != null) {
            this.loginContext.logout();
        }
        if (this.gssCredential != null) {
            this.gssCredential.dispose();
        }
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public Object getAttribute(String name) {
        if (this.attributes == null || name == null) {
            return null;
        }
        return this.attributes.get(name);
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public Enumeration<String> getAttributeNames() {
        if (this.attributes == null) {
            return Collections.emptyEnumeration();
        }
        return Collections.enumeration(this.attributes.keySet());
    }

    private Object writeReplace() {
        return new SerializablePrincipal(this.name, this.password, this.roles, this.userPrincipal, this.attributes);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/realm/GenericPrincipal$SerializablePrincipal.class */
    private static class SerializablePrincipal implements Serializable {
        private static final long serialVersionUID = 1;
        private final String name;
        private final String password;
        private final String[] roles;
        private final Principal principal;
        private final Map<String, Object> attributes;

        SerializablePrincipal(String name, String password, String[] roles, Principal principal, Map<String, Object> attributes) {
            this.name = name;
            this.password = password;
            this.roles = roles;
            if (principal instanceof Serializable) {
                this.principal = principal;
            } else {
                this.principal = null;
            }
            this.attributes = attributes;
        }

        private Object readResolve() {
            return new GenericPrincipal(this.name, this.password, Arrays.asList(this.roles), this.principal, null, null, this.attributes);
        }
    }
}
