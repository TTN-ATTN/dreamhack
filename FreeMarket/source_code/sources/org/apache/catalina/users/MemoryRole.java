package org.apache.catalina.users;

import org.apache.tomcat.util.security.Escape;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/users/MemoryRole.class */
public class MemoryRole extends GenericRole<MemoryUserDatabase> {
    MemoryRole(MemoryUserDatabase database, String rolename, String description) {
        super(database, rolename, description);
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("<role rolename=\"");
        sb.append(Escape.xml(this.rolename));
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(Escape.xml(this.description));
            sb.append("\"");
        }
        sb.append("/>");
        return sb.toString();
    }
}
