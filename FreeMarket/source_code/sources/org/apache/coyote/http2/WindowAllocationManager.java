package org.apache.coyote.http2;

import org.apache.coyote.ActionCode;
import org.apache.coyote.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/WindowAllocationManager.class */
class WindowAllocationManager {
    private static final Log log = LogFactory.getLog((Class<?>) WindowAllocationManager.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) WindowAllocationManager.class);
    private static final int NONE = 0;
    private static final int STREAM = 1;
    private static final int CONNECTION = 2;
    private final Stream stream;
    private int waitingFor = 0;

    WindowAllocationManager(Stream stream) {
        this.stream = stream;
    }

    void waitForStream(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitFor.stream", this.stream.getConnectionId(), this.stream.getIdAsString(), Long.toString(timeout)));
        }
        waitFor(1, timeout);
    }

    void waitForConnection(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitFor.connection", this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.stream.getConnectionAllocationRequested()), Long.toString(timeout)));
        }
        waitFor(2, timeout);
    }

    void waitForStreamNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitForNonBlocking.stream", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        waitForNonBlocking(1);
    }

    void waitForConnectionNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitForNonBlocking.connection", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        waitForNonBlocking(2);
    }

    void notifyStream() {
        notify(1);
    }

    void notifyConnection() {
        notify(2);
    }

    void notifyAny() {
        notify(3);
    }

    boolean isWaitingForStream() {
        return isWaitingFor(1);
    }

    boolean isWaitingForConnection() {
        return isWaitingFor(2);
    }

    private boolean isWaitingFor(int waitTarget) {
        boolean z;
        synchronized (this.stream) {
            z = (this.waitingFor & waitTarget) > 0;
        }
        return z;
    }

    private void waitFor(int waitTarget, long timeout) throws InterruptedException {
        synchronized (this.stream) {
            if (this.waitingFor != 0) {
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", this.stream.getConnectionId(), this.stream.getIdAsString()));
            }
            this.waitingFor = waitTarget;
            if (timeout < 0) {
                this.stream.wait();
            } else {
                this.stream.wait(timeout);
            }
        }
    }

    private void waitForNonBlocking(int waitTarget) {
        synchronized (this.stream) {
            if (this.waitingFor == 0) {
                this.waitingFor = waitTarget;
            } else if (this.waitingFor != waitTarget) {
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", this.stream.getConnectionId(), this.stream.getIdAsString()));
            }
        }
    }

    private void notify(int notifyTarget) {
        synchronized (this.stream) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("windowAllocationManager.notify", this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.waitingFor), Integer.toString(notifyTarget)));
            }
            if ((notifyTarget & this.waitingFor) > 0) {
                this.waitingFor = 0;
                Response response = this.stream.getCoyoteResponse();
                if (response != null) {
                    if (response.getWriteListener() == null) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("windowAllocationManager.notified", this.stream.getConnectionId(), this.stream.getIdAsString()));
                        }
                        this.stream.notify();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("windowAllocationManager.dispatched", this.stream.getConnectionId(), this.stream.getIdAsString()));
                        }
                        response.action(ActionCode.DISPATCH_WRITE, null);
                        response.action(ActionCode.DISPATCH_EXECUTE, null);
                    }
                }
            }
        }
    }
}
