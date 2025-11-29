package org.springframework.jmx.export.notification;

import javax.management.Notification;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/notification/NotificationPublisher.class */
public interface NotificationPublisher {
    void sendNotification(Notification notification) throws UnableToSendNotificationException;
}
