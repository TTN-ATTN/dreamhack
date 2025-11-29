package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/Group.class */
public interface Group extends Principal {
    String getDescription();

    void setDescription(String str);

    String getGroupname();

    void setGroupname(String str);

    Iterator<Role> getRoles();

    UserDatabase getUserDatabase();

    Iterator<User> getUsers();

    void addRole(Role role);

    boolean isInRole(Role role);

    void removeRole(Role role);

    void removeRoles();
}
