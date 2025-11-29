package org.apache.logging.log4j.message;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/MessageFactory.class */
public interface MessageFactory {
    Message newMessage(Object message);

    Message newMessage(String message);

    Message newMessage(String message, Object... params);
}
