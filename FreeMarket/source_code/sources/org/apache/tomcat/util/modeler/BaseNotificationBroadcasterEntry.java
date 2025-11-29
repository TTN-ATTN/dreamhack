package org.apache.tomcat.util.modeler;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/* compiled from: BaseNotificationBroadcaster.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/modeler/BaseNotificationBroadcasterEntry.class */
class BaseNotificationBroadcasterEntry {
    public NotificationFilter filter;
    public Object handback;
    public NotificationListener listener;

    BaseNotificationBroadcasterEntry(NotificationListener listener, NotificationFilter filter, Object handback) {
        this.filter = null;
        this.handback = null;
        this.listener = null;
        this.listener = listener;
        this.filter = filter;
        this.handback = handback;
    }
}
