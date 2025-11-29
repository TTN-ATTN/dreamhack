package javax.websocket;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/SessionException.class */
public class SessionException extends Exception {
    private static final long serialVersionUID = 1;
    private final Session session;

    public SessionException(String message, Throwable cause, Session session) {
        super(message, cause);
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }
}
