package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/SecurityRoleRef.class */
public class SecurityRoleRef implements Serializable {
    private static final long serialVersionUID = 1;
    private String name = null;
    private String link = null;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SecurityRoleRef[");
        sb.append("name=");
        sb.append(this.name);
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        sb.append(']');
        return sb.toString();
    }
}
