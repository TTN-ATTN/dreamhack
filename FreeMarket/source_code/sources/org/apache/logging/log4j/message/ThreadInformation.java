package org.apache.logging.log4j.message;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/ThreadInformation.class */
public interface ThreadInformation {
    void printThreadInfo(StringBuilder sb);

    void printStack(StringBuilder sb, StackTraceElement[] trace);
}
