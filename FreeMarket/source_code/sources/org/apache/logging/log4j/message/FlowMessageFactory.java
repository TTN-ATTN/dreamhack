package org.apache.logging.log4j.message;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/FlowMessageFactory.class */
public interface FlowMessageFactory {
    EntryMessage newEntryMessage(Message message);

    ExitMessage newExitMessage(Object result, Message message);

    ExitMessage newExitMessage(EntryMessage message);

    ExitMessage newExitMessage(Object result, EntryMessage message);
}
