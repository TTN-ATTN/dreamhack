package org.apache.coyote.http2;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/AbstractStream.class */
abstract class AbstractStream {
    private static final Log log = LogFactory.getLog((Class<?>) AbstractStream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) AbstractStream.class);
    private final Integer identifier;
    private final String idAsString;
    private volatile AbstractStream parentStream = null;
    private final Set<AbstractNonZeroStream> childStreams = ConcurrentHashMap.newKeySet();
    private long windowSize = 65535;
    private volatile int connectionAllocationRequested = 0;
    private volatile int connectionAllocationMade = 0;

    abstract String getConnectionId();

    abstract int getWeight();

    AbstractStream(Integer identifier) {
        this.identifier = identifier;
        this.idAsString = identifier.toString();
    }

    final Integer getIdentifier() {
        return this.identifier;
    }

    final String getIdAsString() {
        return this.idAsString;
    }

    final int getIdAsInt() {
        return this.identifier.intValue();
    }

    final void detachFromParent() {
        if (this.parentStream != null) {
            this.parentStream.getChildStreams().remove(this);
            this.parentStream = null;
        }
    }

    final void addChild(AbstractNonZeroStream child) {
        child.setParentStream(this);
        this.childStreams.add(child);
    }

    final boolean isDescendant(AbstractStream stream) {
        AbstractStream parent;
        AbstractStream parentStream = stream.getParentStream();
        while (true) {
            parent = parentStream;
            if (parent == null || parent == this) {
                break;
            }
            parentStream = parent.getParentStream();
        }
        return parent != null;
    }

    final AbstractStream getParentStream() {
        return this.parentStream;
    }

    final void setParentStream(AbstractStream parentStream) {
        this.parentStream = parentStream;
    }

    final Set<AbstractNonZeroStream> getChildStreams() {
        return this.childStreams;
    }

    final synchronized void setWindowSize(long windowSize) {
        this.windowSize = windowSize;
    }

    final synchronized long getWindowSize() {
        return this.windowSize;
    }

    synchronized void incrementWindowSize(int increment) throws Http2Exception {
        this.windowSize += increment;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("abstractStream.windowSizeInc", getConnectionId(), getIdAsString(), Integer.toString(increment), Long.toString(this.windowSize)));
        }
        if (this.windowSize > 2147483647L) {
            String msg = sm.getString("abstractStream.windowSizeTooBig", getConnectionId(), this.identifier, Integer.toString(increment), Long.toString(this.windowSize));
            if (this.identifier.intValue() == 0) {
                throw new ConnectionException(msg, Http2Error.FLOW_CONTROL_ERROR);
            }
            throw new StreamException(msg, Http2Error.FLOW_CONTROL_ERROR, this.identifier.intValue());
        }
    }

    final synchronized void decrementWindowSize(int decrement) {
        this.windowSize -= decrement;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("abstractStream.windowSizeDec", getConnectionId(), getIdAsString(), Integer.toString(decrement), Long.toString(this.windowSize)));
        }
    }

    final int getConnectionAllocationRequested() {
        return this.connectionAllocationRequested;
    }

    final void setConnectionAllocationRequested(int connectionAllocationRequested) {
        log.debug(sm.getString("abstractStream.setConnectionAllocationRequested", getConnectionId(), getIdAsString(), Integer.toString(this.connectionAllocationRequested), Integer.toString(connectionAllocationRequested)));
        this.connectionAllocationRequested = connectionAllocationRequested;
    }

    final int getConnectionAllocationMade() {
        return this.connectionAllocationMade;
    }

    final void setConnectionAllocationMade(int connectionAllocationMade) {
        log.debug(sm.getString("abstractStream.setConnectionAllocationMade", getConnectionId(), getIdAsString(), Integer.toString(this.connectionAllocationMade), Integer.toString(connectionAllocationMade)));
        this.connectionAllocationMade = connectionAllocationMade;
    }
}
