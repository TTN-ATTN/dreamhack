package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/Manager.class */
public interface Manager {
    Context getContext();

    void setContext(Context context);

    SessionIdGenerator getSessionIdGenerator();

    void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator);

    long getSessionCounter();

    void setSessionCounter(long j);

    int getMaxActive();

    void setMaxActive(int i);

    int getActiveSessions();

    long getExpiredSessions();

    void setExpiredSessions(long j);

    int getRejectedSessions();

    int getSessionMaxAliveTime();

    void setSessionMaxAliveTime(int i);

    int getSessionAverageAliveTime();

    int getSessionCreateRate();

    int getSessionExpireRate();

    void add(Session session);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    @Deprecated
    void changeSessionId(Session session);

    void changeSessionId(Session session, String str);

    Session createEmptySession();

    Session createSession(String str);

    Session findSession(String str) throws IOException;

    Session[] findSessions();

    void load() throws ClassNotFoundException, IOException;

    void remove(Session session);

    void remove(Session session, boolean z);

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void unload() throws IOException;

    void backgroundProcess();

    boolean willAttributeDistribute(String str, Object obj);

    void setNotifyBindingListenerOnUnchangedValue(boolean z);

    void setNotifyAttributeListenerOnUnchangedValue(boolean z);

    default String rotateSessionId(Session session) {
        String newSessionId;
        boolean duplicate = true;
        do {
            newSessionId = getSessionIdGenerator().generateSessionId();
            try {
                if (findSession(newSessionId) == null) {
                    duplicate = false;
                }
            } catch (IOException e) {
            }
        } while (duplicate);
        changeSessionId(session, newSessionId);
        return newSessionId;
    }

    default boolean getNotifyBindingListenerOnUnchangedValue() {
        return false;
    }

    default boolean getNotifyAttributeListenerOnUnchangedValue() {
        return true;
    }
}
