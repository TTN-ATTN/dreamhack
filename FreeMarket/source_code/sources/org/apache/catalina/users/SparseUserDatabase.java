package org.apache.catalina.users;

import org.apache.catalina.UserDatabase;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/users/SparseUserDatabase.class */
public abstract class SparseUserDatabase implements UserDatabase {
    @Override // org.apache.catalina.UserDatabase
    public boolean isSparse() {
        return true;
    }
}
