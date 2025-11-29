package org.apache.catalina.users;

import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/users/MemoryGroup.class */
public class MemoryGroup extends GenericGroup<MemoryUserDatabase> {
    MemoryGroup(MemoryUserDatabase database, String groupname, String description) {
        super(database, groupname, description, null);
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("<group groupname=\"");
        sb.append(Escape.xml(this.groupname));
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(Escape.xml(this.description));
            sb.append("\"");
        }
        sb.append(" roles=\"");
        StringBuilder rsb = new StringBuilder();
        StringUtils.join((Iterable) this.roles, ',', x -> {
            return Escape.xml(x.getRolename());
        }, rsb);
        sb.append((CharSequence) rsb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }
}
