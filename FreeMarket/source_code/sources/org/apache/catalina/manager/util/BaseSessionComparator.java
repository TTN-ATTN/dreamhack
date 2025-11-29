package org.apache.catalina.manager.util;

import java.util.Comparator;
import org.apache.catalina.Session;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/manager/util/BaseSessionComparator.class */
public abstract class BaseSessionComparator<T> implements Comparator<Session> {
    public abstract Comparable<T> getComparableObject(Session session);

    @Override // java.util.Comparator
    public final int compare(Session s1, Session s2) {
        Comparable<T> c1 = getComparableObject(s1);
        Comparable<T> c2 = getComparableObject(s2);
        if (c1 == null) {
            return c2 == null ? 0 : -1;
        }
        if (c2 == null) {
            return 1;
        }
        return c1.compareTo(c2);
    }
}
